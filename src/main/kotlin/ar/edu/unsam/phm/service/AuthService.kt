package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.LoginRequest
import ar.edu.unsam.phm.dto.RegisterRequest
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.exceptions.UnauthorizedException
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(private val usuarioRepository: UsuarioRepository) {

    fun login(usuario: LoginRequest): Usuario {
        /* Busco el primer usuario que matchee con el email y si no devuelvo null y por
        lo tanto NotFoundException */

        val userReal = usuarioRepository.findByEmail(usuario.email) ?:
            throw NotFoundException("Usuario no registrado.")

        if (!userReal.validarPassword(usuario.password) ) {
            throw UnauthorizedException("Usuario o contraseña incorrecto.")
        }

        return userReal
    }

    @Transactional
    fun register(usuario: RegisterRequest): Usuario {
        val existe = usuarioRepository.findByEmail(usuario.email)
        if (existe != null) throw ConflictException("Este usuario ya está registrado")

        val nuevoUsuario = Usuario(
            nombreCompleto = usuario.nombreCompleto,
            email = usuario.email,
            password = usuario.password
        )

        usuarioRepository.save(nuevoUsuario)

        /* nuevoUsuario no tiene ID asignado, solo después de create se le asigna,
        por eso lo busco de nuevo */

        return usuarioRepository.findByEmail(nuevoUsuario.email)!!
    }

}