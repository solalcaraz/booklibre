package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.text.toIntOrNull


@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val reservaRepository: ReservaRepository,
    private val usuarioMapper: UsuarioMapper
) {
    fun getUsuarioById(id: Int): Usuario {
        val usuario = usuarioRepository.findById(id).orElseThrow { NotFoundException("Usuario no encontrado") }

        usuario.librosLeidos = reservaRepository.cantidadLeidosDe(usuario.id!!)
        usuario.librosPrestados = reservaRepository.cantidadPrestadosDe(usuario.id!!)

        return usuario
    }

    @Transactional
    fun updateUsuario(userPatch: UsuarioPatchRequest, id: Int) {
        val usuario = usuarioRepository.findById(id).orElseThrow { NotFoundException("Usuario no encontrado") }

        val prevUser = usuarioRepository.findByEmail(userPatch.email)

        if (prevUser != null && prevUser.id != usuario.id) {
            throw ConflictException("Email ya utilizado por otro usuario")
        }

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