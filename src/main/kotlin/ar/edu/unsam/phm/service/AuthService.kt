package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.LoginRequest
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
    fun login(usuario: LoginRequest): String {
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

        return tokenHelper.generate(
            userId = foundUser.id!!,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )
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