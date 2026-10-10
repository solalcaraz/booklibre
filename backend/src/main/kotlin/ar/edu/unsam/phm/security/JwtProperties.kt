package ar.edu.unsam.phm.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
data class JwtProperties(
    val key: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long
)