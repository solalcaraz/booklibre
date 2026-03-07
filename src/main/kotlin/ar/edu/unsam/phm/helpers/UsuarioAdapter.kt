package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioLogeadoDTO
import org.springframework.stereotype.Service

interface UsuarioAdapter {
    fun toDto(usuario: Usuario): UsuarioLogeadoDTO
}

@Service
class UsuarioMapper: UsuarioAdapter {
    override fun toDto(usuario: Usuario): UsuarioLogeadoDTO {
        return UsuarioLogeadoDTO( usuario.id!!, usuario.email, usuario.nombreCompleto)
    }
}