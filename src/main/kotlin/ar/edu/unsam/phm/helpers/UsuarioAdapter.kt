package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioCompletoDTO
import ar.edu.unsam.phm.dto.UsuarioLogeadoDTO
import org.springframework.stereotype.Service

interface UsuarioAdapter {
    fun toDto(usuario: Usuario): UsuarioLogeadoDTO
    fun toFullDto(usuario: Usuario): UsuarioCompletoDTO
}

@Service
class UsuarioMapper: UsuarioAdapter {
    override fun toDto(usuario: Usuario): UsuarioLogeadoDTO {
        return UsuarioLogeadoDTO( usuario.id!!, usuario.nombreCompleto)
    }

    override fun toFullDto(usuario: Usuario): UsuarioCompletoDTO {
        return UsuarioCompletoDTO(
            nombreCompleto = usuario.nombreCompleto,
            email = usuario.email,
            descripcion = usuario.descripcion,
            telefono = usuario.telefono,
            anoUnido = usuario.anoUnido,
            ciudad = usuario.ciudad,
            bibliokarmas = usuario.bibliokarmas,
            tipoUsuario = usuario.tipoUsuario.nombre(),
            imagen = usuario.imagen,
            librosLeidos = usuario.librosLeidos,
            librosPrestados = usuario.librosPrestados
        )
    }

}