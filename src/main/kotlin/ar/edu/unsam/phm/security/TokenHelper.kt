package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.domain.Usuario
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date

/* Este TokenHelper nos aporta funciones básicas para la manipulación del token del usuario */
/* Crearlo, saber si expiró, si es válido, extraer los claims o el email. */

@Service
class TokenHelper(
    jwtProperties: JwtProperties,
) {
    private val secretKey = Keys.hmacShaKeyFor(
        jwtProperties.key.toByteArray(),
    )

    fun generateAccessToken(usuario: Usuario, expirationDate: Date): String =
        generate(
            userId = usuario.id!!,
            expirationDate = expirationDate,
            additionalClaims = mapOf(
                "role" to usuario.tipoUsuario.name,
                "type" to "access"
            )
        )

    fun generateRefreshToken(usuario: Usuario, expirationDate: Date): String =
        generate(
            userId = usuario.id!!,
            expirationDate = expirationDate,
            additionalClaims = mapOf(
                "type" to "refresh"
            )
        )

    fun extractType(token: String): String? =
        getAllClaims(token)["type"] as? String

    // Extrae el rol del claim para construir las authorities sin tocar la DB
    fun extractRole(token: String): String? =
        getAllClaims(token)["role"] as? String

    fun generate(
        userId: Int,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String =
        Jwts.builder()
            .claims()
            .subject(userId.toString())
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .add(additionalClaims)
            .and()
            .signWith(secretKey)
            .compact()

    fun extractUserId(token: String): Int? =
        getAllClaims(token).subject.toIntOrNull()

    fun isExpired(token: String) : Boolean =
        getAllClaims(token).expiration.before(Date(System.currentTimeMillis()))

    fun isValid(token: String, userId: Int): Boolean {
        val tokenUserId = extractUserId(token)

        return tokenUserId == userId && !isExpired(token)
    }

    private fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()

        return parser.parseSignedClaims(token).payload
    }
}