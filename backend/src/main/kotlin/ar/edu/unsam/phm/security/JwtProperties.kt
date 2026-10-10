package ar.edu.unsam.phm.security

import org.springframework.boot.context.properties.ConfigurationProperties

/* Este objeto JwtProperties mapea las variables de configuración de application.yml */
/* La secret-key alojada en las variables de entorno para firmar/verificar JWT */
/* La duración del accessToken y el refreshToken (aún no implementado) */
/* SpringBoot convierte kebab-case → camelCase */

@ConfigurationProperties(prefix = "security")
data class JwtProperties(
    val key: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long
)