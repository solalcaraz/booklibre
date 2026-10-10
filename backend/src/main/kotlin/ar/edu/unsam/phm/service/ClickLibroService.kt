package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.ClickLibroDocument
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.ClickLibroRepository
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClickLibroService(
    private val clickLibroRepository: ClickLibroRepository,
    private val usuarioRepository: UsuarioRepository,
    private val libroDocumentRepository: LibroDocumentRepository,
    private val libroPopularRedisService: LibroPopularRedisService,
) {
    @Transactional(readOnly = false)
    fun registrar(userId: Int, libroId: String) {
        val usuario = usuarioRepository.findById(userId).orElseThrow { NotFoundException("Usuario no encontrado") }
        val libro = libroDocumentRepository.findById(libroId).orElseThrow { NotFoundException("Libro no encontrado") }

        clickLibroRepository.save(
            ClickLibroDocument(
                usuarioId = usuario.id!!,
                usuarioNombre = usuario.nombreCompleto,
                libroId = libro.id!!,
                libroTitulo = libro.titulo
            )
        )

        libroPopularRedisService.registrarClick(libro.id!!)
    }
}
