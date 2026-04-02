package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service
import kotlin.text.toIntOrNull


@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val reservaRepositorio: ReservaRepositorio,
    private val usuarioMapper: UsuarioMapper
) {
    fun getUsuarioById(id: String): Usuario {
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")
        val usuario = usuarioRepository.findById(idNumerico).orElseThrow { NotFoundException("Usuario no encontrado") }

        usuario.librosLeidos = reservaRepositorio.cantidadLeidosDe(usuario)
        usuario.librosPrestados = reservaRepositorio.cantidadPrestadosDe(usuario)
        /* No actualizo el repositorio porque lo voy a calcular siempre, no me interesa persistir esta data  */

        return usuario
    }

    fun updateUsuario(userPatch: UsuarioPatchRequest, id: String) {
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")
        val usuario = usuarioRepository.findById(idNumerico).orElseThrow { NotFoundException("Usuario no encontrado") }

        val usuarioActualizado = usuarioMapper.fromDto(userPatch, usuario)

        usuarioRepository.save(usuarioActualizado)

        return
    }
}