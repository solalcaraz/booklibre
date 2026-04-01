package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.PerfilUsuarioDTO
import ar.edu.unsam.phm.dto.LoginResponseDTO
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.env.Environment
import org.springframework.stereotype.Service

interface UsuarioAdapter {
    fun toDto(usuario: Usuario): LoginResponseDTO
    fun toFullDto(usuario: Usuario): PerfilUsuarioDTO
    fun fromDto(userPatch: UsuarioPatchRequest, usuario: Usuario): Usuario
}

@Service
class UsuarioMapper: UsuarioAdapter {
    override fun toDto(usuario: Usuario): LoginResponseDTO {
        return LoginResponseDTO(
            usuario.id!!,
            usuario.nombreCompleto,
            usuario.imagen, usuario.bibliokarmas
        )
    }

    override fun toFullDto(usuario: Usuario): PerfilUsuarioDTO {
        return PerfilUsuarioDTO(
            nombreCompleto = usuario.nombreCompleto,
            email = usuario.email,
            descripcion = usuario.descripcion,
            telefono = usuario.telefono,
            anoUnido = usuario.anoUnido,
            ciudad = usuario.ciudad,
            bibliokarmas = usuario.bibliokarmas,
            tipoUsuario = usuario.tipoUsuario.label,
            imagen = usuario.imagen,
            librosLeidos = usuario.librosLeidos,
            librosPrestados = usuario.librosPrestados
        )
    }

    override fun fromDto(userPatch: UsuarioPatchRequest, usuario: Usuario): Usuario {
        usuario.email = userPatch.email
        usuario.ciudad = userPatch.ciudad
        usuario.telefono = userPatch.telefono
        usuario.nombreCompleto = userPatch.nombreCompleto
        usuario.descripcion = userPatch.descripcion
        if (userPatch.imagen == "") usuario.imagen = Environment.API_URL + "/imagenes/default_user.png"
        else usuario.imagen = userPatch.imagen
        usuario.cambiarTipo(userPatch.tipoUsuario)

        return usuario
    }

}