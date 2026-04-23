package ar.edu.unsam.phm.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.config.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.security.web.csrf.CsrfTokenRequestHandler
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler
import org.springframework.util.StringUtils
import java.util.function.Supplier

/* En este archivo se indica en qué endpoints se va a aplicar el filtro de autenticación */

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider
) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain =
        http
        .cors{ cors -> cors.configurationSource(corsConfigurationSource()) }
        .csrf {
            it.disable()
            /*it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            it.csrfTokenRequestHandler(SpaCsrfTokenRequestHandler())
            it.ignoringRequestMatchers("/auth/login", "/auth/register", "/error")*/
        }
        .authorizeHttpRequests {
            // Queremos que cualquier persona se pueda loguear y mostrar errores
            // Importante: no definirlo solo para el method POST
            // porque CORS requiere que también puedas mandar el OPTIONS para hacer el pre-flight
            it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/auth/register", "/auth/login", "/error") // agregar refresh token endpoint
            .permitAll()
            // Permisos de admin para modificar
            //---------------------------
            .requestMatchers(HttpMethod.POST, "/libro/nuevo")
                .hasAnyRole("Publicador", "Lector / Publicador")
            // Default: que se autentique para poder ver información
            .anyRequest()
            .authenticated()
        }
        .sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        .exceptionHandling(Customizer.withDefaults())
        .build()
}

/* Este HandlerCSRF es el que hizo Fer para el ejemplo de las heladerías, aparentemente
con el CsrfTokenRequestAttributeHandler() que trae Spring Security no alcanza para SPA... */

class SpaCsrfTokenRequestHandler : CsrfTokenRequestHandler {
    private val plain: CsrfTokenRequestHandler = CsrfTokenRequestAttributeHandler()
    private val xor: CsrfTokenRequestHandler = XorCsrfTokenRequestAttributeHandler()

    val logger: Logger = LoggerFactory.getLogger(SpaCsrfTokenRequestHandler::class.java)

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, csrfToken: Supplier<CsrfToken>) {
        /*
         * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
         * the CsrfToken when it is rendered in the response body.
         */
        xor.handle(request, response, csrfToken)
        /*
         * Render the token value to a cookie by causing the deferred token to be loaded.
         */
        csrfToken.get()
    }

    override fun resolveCsrfTokenValue(request: HttpServletRequest, csrfToken: CsrfToken): String? {
        logger.info("header name ${csrfToken.headerName}")
        val headerValue = request.getHeader(csrfToken.headerName)
        /*
         * If the request contains a request header, use CsrfTokenRequestAttributeHandler
         * to resolve the CsrfToken. This applies when a single-page application includes
         * the header value automatically, which was obtained via a cookie containing the
         * raw CsrfToken.
         */
        logger.info("header value $headerValue")
        return if (StringUtils.hasText(headerValue)) {
            logger.info("plain $plain")
            plain
        } else {
            /*
             * In all other cases (e.g. if the request contains a request parameter), use
             * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
             * when a server-side rendered form includes the _csrf request parameter as a
             * hidden input.
             */
            logger.info("xor $xor")
            xor
        }.resolveCsrfTokenValue(request, csrfToken)
    }
}