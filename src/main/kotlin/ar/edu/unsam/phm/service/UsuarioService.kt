package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.text.toIntOrNull


@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val reservaRepository: ReservaRepository,
    private val usuarioMapper: UsuarioMapper
) {
    fun getUsuarioById(id: String): Usuario {
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")
        val usuario = usuarioRepository.findById(idNumerico).orElseThrow { NotFoundException("Usuario no encontrado") }

        usuario.librosLeidos = reservaRepository.cantidadLeidosDe(idNumerico)
        usuario.librosPrestados = reservaRepository.cantidadPrestadosDe(idNumerico)
        /* No actualizo el repositorio porque lo voy a calcular siempre, no me interesa persistir esta data  */

        return usuario
    }

    @Transactional
    fun updateUsuario(userPatch: UsuarioPatchRequest, id: String) {
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")
        val usuario = usuarioRepository.findById(idNumerico).orElseThrow { NotFoundException("Usuario no encontrado") }

        val usuarioActualizado = usuarioMapper.fromDto(userPatch, usuario)

        usuarioRepository.save(usuarioActualizado)

        return
    }

    fun getBibliokarma(id: String): Int {
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("Id incorrecto")
        val usuario = usuarioRepository.findById(idNumerico).orElseThrow { NotFoundException("Usuario no encontrado") }
        return usuario.bibliokarmas
    }
}