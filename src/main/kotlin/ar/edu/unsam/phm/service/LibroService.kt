package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.UsuarioLibroDocument
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import org.springframework.stereotype.Service
import ar.edu.unsam.phm.dto.MiLibroProjection
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.helpers.LibroMapper
import ar.edu.unsam.phm.exceptions.ForbiddenException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDate

@Service
class LibroService(
    val libroRepository: LibroDocumentRepository,
    val usuarioRepository: UsuarioRepository,
    val libroMapper: LibroMapper
) {
    private fun getIdUsuarioLogueado () =
        SecurityContextHolder.getContext().authentication.name.toInt()

    fun getByIdForUser(id: String): LibroDocument {
        val libro = libroRepository.findById(id)
            .orElseThrow { NotFoundException("Libro no encontrado.") }
        if (libro.dueno.id != getIdUsuarioLogueado()) {
            throw ForbiddenException("No tenés permisos para modificar este libro.")
        }
        return libro
    }

    fun create(libroDto: LibroDetalleDTO) {
        val usuarioLogueado = usuarioRepository.findById(getIdUsuarioLogueado()).orElseThrow { NotFoundException("Usuario no encontrado") }
        val nuevoLibro = libroMapper.fromDto(libroDto).apply {
            dueno = UsuarioLibroDocument(
                id = usuarioLogueado.id,
                nombreCompleto = usuarioLogueado.nombreCompleto,
                imagen = usuarioLogueado.imagen
            )
        }
        libroRepository.save(nuevoLibro)
    }

    fun update(id: String, libroDto: LibroDetalleDTO) {
        val libroExistente = getByIdForUser(id)

        // Si no le volvemos a pasar los valores existentes de estas variables va a generar un registro nuevo
        val libroActualizado = libroMapper.fromDto(libroDto).apply {
            this.id = id
            this.dueno = libroExistente.dueno
            this.reservasPendientes = libroExistente.reservasPendientes
            this.promedioCalificacion = libroExistente.promedioCalificacion
            this.cantidadReservas = libroExistente.cantidadReservas
        }

        libroRepository.save(libroActualizado)
    }

//    fun buscarMisLibros(
//        userId: Int,
//        filter: String,
//        sort: String?,
//        page: Int,
//        size: Int
//    ): PaginationResponseDTO<MiLibroProjection> {
//
//        val usuario = usuarioRepository.findById(userId).orElseThrow { NotFoundException("Usuario no encontrado") }
//        val paginado = PageRequest.of(page, size)
//
//        val librosPage = when (filter) {
//            "Disponibles" -> libroRepository.librosDisponiblesDe(usuario.id!!, sort, paginado)
//            "Prestados" -> libroRepository.librosPrestadosDe(usuario.id!!, sort, paginado)
//            else -> libroRepository.todosLibrosDe(usuario.id!!, sort, paginado)
//        }
//
//        /* Esto lo puedo mover al controller, sería serializar el DTO final */
//        return PaginationResponseDTO(
//            items = librosPage.content,
//            page = librosPage.number,
//            size = librosPage.size,
//            totalItems = librosPage.totalElements.toInt(),
//            totalPages = librosPage.totalPages
//        )
//    }

    fun buscarMisLibros(
        userId: Int,
        filter: String,
        sort: String?,
        page: Int,
        size: Int
    ): PaginationResponseDTO<MiLibroProjection> {
        val paginado = PageRequest.of(page, size)
        val hoy = LocalDate.now()

        val librosPage = when (filter) {
            "Disponibles" -> libroRepository.findDisponiblesByDuenoId(userId, hoy, paginado)
            "Prestados" -> libroRepository.findPrestadosByDuenoId(userId, hoy, paginado)
            else -> libroRepository.findByDuenoIdAndActivoTrue(userId, paginado)
        }

        return PaginationResponseDTO(
            items = librosPage.content.map { libro ->
                MiLibroProjection(
                    id = libro.id!!,
                    titulo = libro.titulo,
                    autor = libro.autor,
                    genero = libro.genero,
                    estadoReserva = if (libro.reservasPendientes.any { it.fechaInicio <= hoy && it.fechaFin >= hoy }) "PRESTADO" else "DISPONIBLE",
                    agregadoEl = libro.fechaPublicacion,
                    portada = libro.portada
                )
            },
            page = librosPage.number,
            size = librosPage.size,
            totalItems = librosPage.totalElements.toInt(),
            totalPages = librosPage.totalPages
        )
    }

    fun despublicarLibro(id: String){
        val libro = getByIdForUser(id)
        libro.activo = false
        libroRepository.save(libro)
    }
}