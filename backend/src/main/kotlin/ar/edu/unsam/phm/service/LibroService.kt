package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.UsuarioLibroDocument
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import org.springframework.stereotype.Service
import ar.edu.unsam.phm.dto.MiLibroProjection
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.toPaginationDTO
import ar.edu.unsam.phm.helpers.LibroMapper
import ar.edu.unsam.phm.exceptions.ForbiddenException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.ClickLibroRepository
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDate

@Service
class LibroService(
    val libroRepository: LibroDocumentRepository,
    val usuarioRepository: UsuarioRepository,
    val clickLibroRepository: ClickLibroRepository,
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

        // El DTO no trae dueño, reservas ni calificación: sin copiarlos se perderían al guardar
        val libroActualizado = libroMapper.fromDto(libroDto).apply {
            this.id = id
            this.dueno = libroExistente.dueno
            this.reservasPendientes = libroExistente.reservasPendientes
            this.promedioCalificacion = libroExistente.promedioCalificacion
            this.cantidadReservas = libroExistente.cantidadReservas
        }

        libroRepository.save(libroActualizado)
    }

    fun buscarMisLibros(
        userId: Int,
        filter: String,
        sort: String?,
        page: Int,
        size: Int
    ): PaginationResponseDTO<MiLibroProjection> {
        val paginado = PageRequest.of(page, size)
        val hoy = LocalDate.now()

        val librosPage = libroRepository.findMisLibrosDe(userId, hoy, filter, sort, paginado)

        val idsLibros = librosPage.content.mapNotNull { it.id }
        // Solo se cuentan los clicks de los libros de la página actual
        val clicksPorLibro = if (idsLibros.isEmpty()) {
            emptyMap()
        } else {
            clickLibroRepository.findByLibroIdIn(idsLibros).groupingBy { it.libroId }.eachCount()
        }

        return librosPage.map { libro ->
            MiLibroProjection(
                id = libro.id!!,
                titulo = libro.titulo,
                autor = libro.autor,
                genero = libro.genero,
                estadoReserva = if (libro.reservasPendientes.any { it.fechaInicio <= hoy && it.fechaFin >= hoy }) "PRESTADO" else "DISPONIBLE",
                agregadoEl = libro.fechaPublicacion,
                portada = libro.portada,
                clicks = clicksPorLibro[libro.id] ?: 0
            )
        }.toPaginationDTO()
    }

    fun despublicarLibro(id: String){
        val libro = getByIdForUser(id)
        libro.activo = false
        libroRepository.save(libro)
    }
}
