package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioDTO
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.exceptions.UnauthorizedException
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service

@Service
class UsuarioService(private val usuarioRepositorio: UsuarioRepositorio) {
    fun login(usuario: UsuarioDTO): Usuario {
        /* Busco el primer usuario que matchee con el email y si no devuelvo null y por
        lo tanto NotFoundException */
        val userReal =
            usuarioRepositorio.search(usuario.email).firstOrNull() ?:
            throw NotFoundException("Usuario no registrado.")

        if (!userReal.validarPassword(usuario.password) ) {
            throw UnauthorizedException("Usuario o contraseña incorrecto.")
        }

        return userReal
    }
}