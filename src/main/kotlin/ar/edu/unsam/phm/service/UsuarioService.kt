package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioDTO
import ar.edu.unsam.phm.dto.UsuarioPatch
import ar.edu.unsam.phm.dto.UsuarioRegister
import ar.edu.unsam.phm.env.Enviroment
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.exceptions.UnauthorizedException
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service

@Service
class UsuarioService(private val usuarioRepositorio: UsuarioRepositorio) {
    fun getUsuarioById(id: String): Usuario {
        return usuarioRepositorio.getById(id.toInt())
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

    fun updateUsuario(userPatch: UsuarioPatch, id: String) {
        val usuario = usuarioRepositorio.getById(id.toInt())

        usuario.email = userPatch.email
        usuario.ciudad = userPatch.ciudad
        usuario.telefono = userPatch.telefono
        usuario.nombreCompleto = userPatch.nombreCompleto
        usuario.descripcion = userPatch.descripcion
        if(userPatch.imagen == "") usuario.imagen = Enviroment.API_URL + "/imagenes/default_user.png"
        else usuario.imagen = userPatch.imagen
        /* TODO: cambiar tipo de usuario */

        return usuarioRepositorio.update(usuario)
    }
}