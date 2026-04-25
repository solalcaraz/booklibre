package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.LoginRequest
import ar.edu.unsam.phm.dto.RefreshTokenResponse
import ar.edu.unsam.phm.dto.RegisterRequest
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.exceptions.UnauthorizedException
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.security.JwtProperties
import ar.edu.unsam.phm.security.TokenHelper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    private val encoder: PasswordEncoder,
    private val authManager: AuthenticationManager,
    private val tokenHelper: TokenHelper,
    private val jwtProperties: JwtProperties
) {
    fun login(usuario: LoginRequest): AuthenticationResponse {
        try {
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    usuario.email,
                    usuario.password
                )
            )
        } catch (_: Exception) {
            throw UnauthorizedException("El usuario o la contraseña son inválidos")
        }

        val foundUser = usuarioRepository.findByEmail(usuario.email)
            ?: throw NotFoundException("Usuario no encontrado")

        val accessToken = tokenHelper.generateAccessToken(
            foundUser,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        val refreshToken = tokenHelper.generateRefreshToken(
            foundUser,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
        )

        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    fun refreshToken(token: String): String? {
        val userId = tokenHelper.extractUserId(token)
            ?: throw UnauthorizedException("Refresh token inválido")

        if (tokenHelper.isExpired(token)) {
            throw UnauthorizedException("Refresh token expirado")
        }

        if (tokenHelper.extractType(token) != "refresh") {
            throw UnauthorizedException("Token inválido")
        }

        val usuario = usuarioRepository.findById(userId)
            .orElseThrow { UnauthorizedException("Usuario no encontrado") }

        val newAccessToken = tokenHelper.generateAccessToken(usuario,
            Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        return newAccessToken
    }

    @Transactional
    fun register(usuario: RegisterRequest): Usuario {
        val existe = usuarioRepository.findByEmail(usuario.email)
        if (existe != null) throw ConflictException("Este usuario ya está registrado")

        val nuevoUsuario = Usuario(
            nombreCompleto = usuario.nombreCompleto,
            email = usuario.email,
            password = encoder.encode(usuario.password)
        )

        return usuarioRepository.save(nuevoUsuario)
    }
}