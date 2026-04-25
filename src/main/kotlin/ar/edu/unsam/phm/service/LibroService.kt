package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import org.springframework.stereotype.Service
import ar.edu.unsam.phm.dto.MiLibroProjection
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.helpers.LibroAdapter
import ar.edu.unsam.phm.exceptions.ForbiddenException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional

@Service
class LibroService(
    val libroRepository: LibroRepository,
    val usuarioRepository: UsuarioRepository,
    val libroMapper: LibroAdapter
) {
    @Transactional(readOnly = true)
    fun getByIdForUser(id: Int): Libro {
        val idUsuarioLogueado = SecurityContextHolder.getContext().authentication.name.toInt()
        val libro = libroRepository.findById(id)
            .orElseThrow { NotFoundException("Libro no encontrado.") }
        if (libro.dueno.id != idUsuarioLogueado) {
            throw ForbiddenException("No tenés permisos para modificar este libro.")
        }
        return libro
    }

    @Transactional
    fun create(libroDto: LibroDetalleDTO) {
        val idUsuarioLogueado = SecurityContextHolder.getContext().authentication.name.toInt()
        val dueno = usuarioRepository.findById(idUsuarioLogueado).orElseThrow { NotFoundException("Usuario no encontrado") }
        val nuevoLibro = libroMapper.fromDto(libroDto, dueno)
        libroRepository.save(nuevoLibro)
    }

    @Transactional
    fun update(id: Int, libroDto: LibroDetalleDTO) {
        val libroExistente = getByIdForUser(id)
        val libroActualizado = libroMapper.fromDto(libroDto, libroExistente.dueno)
        libroRepository.save(libroActualizado)
    }

    fun buscarMisLibros(
        userId: Int,
        filter: String,
        sort: String?,
        page: Int,
        size: Int
    ): PaginationResponseDTO<MiLibroProjection> {

        val usuario = usuarioRepository.findById(userId).orElseThrow { NotFoundException("Usuario no encontrado") }
        val paginado = PageRequest.of(page, size)

        val librosPage = when (filter) {
            "Disponibles" -> libroRepository.librosDisponiblesDe(usuario.id!!, sort, paginado)
            "Prestados" -> libroRepository.librosPrestadosDe(usuario.id!!, sort, paginado)
            else -> libroRepository.todosLibrosDe(usuario.id!!, sort, paginado)
        }

        /* Esto lo puedo mover al controller, sería serializar el DTO final */
        return PaginationResponseDTO(
            items = librosPage.content,
            page = librosPage.number,
            size = librosPage.size,
            totalItems = librosPage.totalElements.toInt(),
            totalPages = librosPage.totalPages
        )
    }

    @Transactional
    fun despublicarLibro(id: String){
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")
        val libro = libroRepository.findById(idNumerico)
            .orElseThrow { NotFoundException("Libro no encontrado") }

        val idAutenticado = SecurityContextHolder.getContext().authentication.name

        if (libro.dueno.id != idAutenticado.toInt()) {
            throw ForbiddenException("No podés despublicar un libro que no es tuyo")
        }

        libro.activo = false

        libroRepository.save(libro)
    }
}