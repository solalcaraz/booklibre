package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.LoginRequest
import ar.edu.unsam.phm.dto.RegisterRequest
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.security.CustomUserDetailsService
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
    private val userDetailsService: CustomUserDetailsService,
    private val tokenHelper: TokenHelper,
    private val jwtProperties: JwtProperties
) {
    fun login(usuario: LoginRequest): AuthenticationResponse {
        /* Busco el primer usuario que matchee con el email y si no devuelvo null y por
        lo tanto NotFoundException */

        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                usuario.email,
                usuario.password
            )
        )

        val user = userDetailsService.loadUserByUsername(usuario.email)
        val accessToken = tokenHelper.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        return AuthenticationResponse(
            accessToken = accessToken
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

        /* ¿Armar un DTO register response para no mandar la entidad? */
        return usuarioRepository.save(nuevoUsuario)
    }

}