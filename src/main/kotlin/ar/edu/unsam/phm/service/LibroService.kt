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
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import jakarta.persistence.EntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional

@Service
class LibroService(
    val libroRepository: LibroRepository,
    val reservaRepository: ReservaRepository,
    val usuarioRepository: UsuarioRepository,
    val libroMapper: LibroAdapter,
    val entityManager: EntityManager
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
        val dueno = usuarioRepository.findById(libroDto.idDueno).orElseThrow { NotFoundException("Usuario no encontrado") }
        val nuevoLibro = libroMapper.fromDto(libroDto, dueno)
        libroRepository.save(nuevoLibro)
    }


    // Cuando hacés un save() de un objeto con una clase distinta,
    // Hibernate no puede hacer un UPDATE de la columna discriminadora (tipo_libro).
    // La única forma de "engañar" a Hibernate sin borrar y rear de nuevo el registro es
    // modificar el discriminador manualmente antes de que el Service intente guardar.
    // Al final, optamos por hacer inmodificable el tipo para ser coherentes con respecto al enunciado,
    // y de este modo no es necesario hacer nada explícito en el código.
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