package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service

@Service
class UsuarioService(private val usuarioRepositorio: UsuarioRepositorio) {

    fun getUsuarioById(id: String): Usuario {
        return usuarioRepositorio.getById(id.toInt())
    }

    fun updateUsuario(userPatch: UsuarioPatchRequest, id: String) {
        val usuario = usuarioRepositorio.getById(id.toInt())

        usuario.email = userPatch.email
        usuario.ciudad = userPatch.ciudad
        usuario.telefono = userPatch.telefono
        usuario.nombreCompleto = userPatch.nombreCompleto
        usuario.descripcion = userPatch.descripcion
        if(userPatch.imagen == "") usuario.imagen = Environment.API_URL + "/imagenes/default_user.png"
        else usuario.imagen = userPatch.imagen
        /* TODO: cambiar tipo de usuario */

        return usuarioRepositorio.update(usuario)
    }
}