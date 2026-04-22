package ar.edu.unsam.phm.security

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/* Esto sería como un middleware de node. Establece como tiene que actuar el security filter
y delega al siguiente eslabón de la filter chain. */

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenHelper: TokenHelper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwtToken = request.extractTokenFromCookie()

            if (jwtToken == null) {
                filterChain.doFilter(request, response)
                return
            }

            val email = tokenHelper.extractEmail(jwtToken)

            if (email != null && SecurityContextHolder.getContext().authentication == null) {
                val foundUser = userDetailsService.loadUserByUsername(email)

                if (tokenHelper.isValid(jwtToken, foundUser)) {
                    updateContext(foundUser, request)
                }

                filterChain.doFilter(request, response)
            }
        } catch (e: ExpiredJwtException) {
            // Captura la excepción de token expirado y devuelve el status code adecuado (401-Unauthorized)
            logger.warn(e.message)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"The access token expired\"")
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Token expired\",\"message\":\"${e.message}\"}")

            // Importante: NO llamar a filterChain.doFilter() después de manejar el error
            return
        }

    }

    /* Si el token expira, debería poder loguear de nuevo y no recibir 401, por eso salto el filter
    tanto para login como register como /refresh más adelante */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/auth/")
    }

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authToken
    }
    private fun String?.doesNotContainBearerToken(): Boolean =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue(): String =
        this.substringAfter("Bearer ")

    private fun HttpServletRequest.extractTokenFromCookie(): String? =
        this.cookies?.firstOrNull { it.name == "ACCESS_TOKEN" }?.value
}