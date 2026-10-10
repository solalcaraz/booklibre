package ar.edu.unsam.phm.security

import org.springframework.security.config.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/* En este archivo se indica en qué endpoints se va a aplicar el filtro de autenticación */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider
) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain =
        http
            .csrf {
                it.disable()
                /*it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                it.csrfTokenRequestHandler(SpaCsrfTokenRequestHandler())
                it.ignoringRequestMatchers("/auth/login", "/auth/register", "/error")*/
            }
            /*.cors{ cors -> cors.configurationSource(corsConfigurationSource()) }*/
            .authorizeHttpRequests {
                // Queremos que cualquier persona se pueda loguear y mostrar errores
                // Importante: no definirlo solo para el method POST
                // porque CORS requiere que también puedas mandar el OPTIONS para hacer el pre-flight
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/auth/register", "/auth/login", "/error", "/auth/refresh")
                    .permitAll() // agregar refresh token endpoint
                    .requestMatchers("/imagenes/**").permitAll()
                    .requestMatchers("/graphql", "/graphiql/**").permitAll()
                    // Permisos de admin para modificar
                    // Solo los que pueden reservar (lector) acceden a este endpoint
                    .requestMatchers(HttpMethod.GET, "/reserva/librosDisponibles/**")
                    .hasAnyRole("LECTOR", "LECTOR_PUBLICADOR")
                    //---------------------------
                    .requestMatchers(HttpMethod.POST, "/libro/nuevo")
                    .hasAnyRole("PUBLICADOR", "LECTOR_PUBLICADOR")
                    // Default: que se autentique para poder ver información
                    // ---------------------------
                    .requestMatchers(HttpMethod.GET, "/reserva/detalles/**")
                    .hasAnyRole("LECTOR", "LECTOR_PUBLICADOR")
                    .requestMatchers(HttpMethod.POST, "/reserva/crear")
                    .hasAnyRole("LECTOR", "LECTOR_PUBLICADOR")
                    .requestMatchers(HttpMethod.POST, "/reserva/obtenerMisReservas")
                    .hasAnyRole("LECTOR", "LECTOR_PUBLICADOR")
                    .requestMatchers(HttpMethod.POST, "/reserva/obtenerPrestadosPorMi")
                    .hasAnyRole("PUBLICADOR", "LECTOR_PUBLICADOR")
                    .requestMatchers(HttpMethod.POST, "/reserva/calificar")
                    .hasAnyRole("LECTOR", "LECTOR_PUBLICADOR")
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
