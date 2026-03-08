package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioDTO
import ar.edu.unsam.phm.dto.UsuarioLogeadoDTO
import ar.edu.unsam.phm.dto.UsuarioRegister
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.exceptions.UnauthorizedException
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service

@Service
class UsuarioService(private val usuarioRepositorio: UsuarioRepositorio) {
    fun getUsuario(usuario: UsuarioLogeadoDTO): Usuario {
        return usuarioRepositorio.getById(usuario.id)
    }

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

    fun register(usuario: UsuarioRegister): Usuario {
        val existe = usuarioRepositorio.search(usuario.email).firstOrNull()
        if(existe != null) throw ConflictException("Este usuario ya está registrado")

        val nuevoUsuario = Usuario(
            nombreCompleto = usuario.nombreCompleto,
            email = usuario.email,
            password = usuario.password
        )
        usuarioRepositorio.create(nuevoUsuario)

        /*nuevoUsuario no tiene ID asignado, solo despues de create se le asigna,
        por eso lo busco de nuevo*/
        return usuarioRepositorio.search(nuevoUsuario.email).first()
    }
}