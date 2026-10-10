package ar.edu.unsam.phm.security

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/* Esto sería como un middleware de node. Establece como tiene que actuar el security filter
y delega al siguiente eslabón de la filter chain. */

@Component
class JwtAuthenticationFilter(
    private val tokenHelper: TokenHelper,
    // Se eliminaron userDetailsService y usuarioRepository:
    // el JWT firmado ya es prueba suficiente de identidad.
    // No hace falta ir a la DB en cada request.
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader: String? = request.getHeader("Authorization")

            if (authHeader.doesNotContainBearerToken()) {
                filterChain.doFilter(request, response)
                return
            }

            val jwtToken = authHeader!!.extractTokenValue()
            val userId = tokenHelper.extractUserId(jwtToken)

            if (userId != null && SecurityContextHolder.getContext().authentication == null) {
                // Validamos solo contra la secret key (sin DB)
                if (tokenHelper.isValid(jwtToken, userId) && tokenHelper.extractType(jwtToken) == "access") {
                    val role = tokenHelper.extractRole(jwtToken)
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

                    val authToken = UsernamePasswordAuthenticationToken(
                        userId.toString(), // authentication.name sigue siendo el id del usuario
                        null,
                        authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }

            filterChain.doFilter(request, response)

        } catch (e: ExpiredJwtException) {
            // Captura la excepción de token expirado y devuelve el status code adecuado (401-Unauthorized)
            logger.warn(e.message)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Token expired\",\"message\":\"${e.message}\"}")
        }
    }

    /* Si el token expira, debería poder loguear de nuevo y no recibir 401, por eso salto el filter
    tanto para login como register como /refresh más adelante */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/auth/")
    }

    private fun String?.doesNotContainBearerToken(): Boolean =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue(): String =
        this.substringAfter("Bearer ")
}