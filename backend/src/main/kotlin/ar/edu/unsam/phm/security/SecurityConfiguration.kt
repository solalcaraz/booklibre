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
            // La API es stateless y el token viaja en un header, no en cookies: CSRF no aplica
            .csrf { it.disable() }
            .authorizeHttpRequests {
                // El preflight de CORS llega sin token, así que OPTIONS tiene que pasar en todas las rutas
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/auth/register", "/auth/login", "/error", "/auth/refresh").permitAll()
                    .requestMatchers("/imagenes/**").permitAll()
                    // Los permisos por rol del dashboard se resuelven en cada data fetcher con @PreAuthorize
                    .requestMatchers("/graphql", "/graphiql/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/reserva/librosDisponibles/**")
                    .hasAnyRole("LECTOR", "LECTOR_PUBLICADOR")
                    .requestMatchers(HttpMethod.POST, "/libro/nuevo")
                    .hasAnyRole("PUBLICADOR", "LECTOR_PUBLICADOR")
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
