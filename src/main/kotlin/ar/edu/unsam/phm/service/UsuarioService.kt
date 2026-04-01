package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepositorio: UsuarioRepositorio,
    private val reservaRepositorio: ReservaRepositorio,
    private val usuarioMapper: UsuarioMapper
) {
    fun getUsuarioById(id: Int): Usuario {
        val usuario = usuarioRepositorio.getById(id)

        usuario.librosLeidos = reservaRepositorio.cantidadLeidosDe(usuario)
        usuario.librosPrestados = reservaRepositorio.cantidadPrestadosDe(usuario)
        /* No actualizo el repositorio porque lo voy a calcular siempre, no me interesa persistir esta data  */

        return usuario
    }

    fun updateUsuario(userPatch: UsuarioPatchRequest, id: Int) {
        val usuario = usuarioRepositorio.getById(id)

        val usuarioActualizado = usuarioMapper.fromDto(userPatch, usuario)

        return usuarioRepositorio.update(usuarioActualizado)
    }
}