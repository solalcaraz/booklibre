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

// El rol viaja en el token firmado, así que no hace falta consultar la base en cada request
@Component
class JwtAuthenticationFilter(
    private val tokenHelper: TokenHelper,
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
                if (tokenHelper.isValid(jwtToken, userId) && tokenHelper.extractType(jwtToken) == "access") {
                    val role = tokenHelper.extractRole(jwtToken)
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

                    val authToken = UsernamePasswordAuthenticationToken(
                        userId.toString(),
                        null,
                        authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }

            filterChain.doFilter(request, response)

        } catch (e: ExpiredJwtException) {
            // El 401 explícito es lo que le indica al front que tiene que renovar el token
            logger.warn(e.message)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Token expired\",\"message\":\"${e.message}\"}")
        }
    }

    // Con un token vencido en el header, login, registro y refresh devolverían 401: no se filtran
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/auth/")
    }

    private fun String?.doesNotContainBearerToken(): Boolean =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue(): String =
        this.substringAfter("Bearer ")
}