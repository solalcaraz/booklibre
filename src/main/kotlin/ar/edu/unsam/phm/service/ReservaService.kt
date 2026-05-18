package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.dto.CrearReservaDTO
import ar.edu.unsam.phm.dto.DetalleReservaDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.dto.ReservaLibroDTO
import ar.edu.unsam.phm.dto.toReservaLibroDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.ReservaAdapter
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.ReseniaRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ReservaService(
    private val reservaRepository: ReservaRepository,
    private val libroRepository: LibroDocumentRepository,
    private val usuarioRepository: UsuarioRepository,
    private val reservaAdapter: ReservaAdapter,
    private val reseniaRepository: ReseniaRepository
) {

    @Transactional(readOnly = true)
    fun getMisReservas(payload: PaginationReqDTO<ReservaRequestDTO>): Page<ReservaDTO> {
        val usuarioId = payload.filter?.usuarioId ?: throw BadRequestException("Falta el ID del usuario en el payload")
        val pageable = PageRequest.of(payload.pageNumber, payload.pageSize)
        val librosMongoIds = libroRepository
            .findActivosByTituloOrAutor(payload.filter.inputSearch)
            .mapNotNull { it.id }

        if (librosMongoIds.isEmpty()) return PageImpl(emptyList(), pageable, 0)

        val pageReserva: Page<Reserva> = reservaRepository.findPrestadosAMiConLibrosMongo( usuarioId.toInt(), librosMongoIds, pageable  )
        return this.mapReservasToDto(pageReserva, pageable)
    }

    fun getPrestadosPorMi(payload: PaginationReqDTO<ReservaRequestDTO>): Page<ReservaDTO> {
        val usuarioId = payload.filter?.usuarioId ?: throw BadRequestException("Falta el ID del usuario en el payload")
        val pageable = PageRequest.of(payload.pageNumber, payload.pageSize)
        val librosMongoIds = libroRepository
            .findActivosByDuenoIdAndTituloOrAutor(usuarioId.toInt(), payload.filter.inputSearch)
            .mapNotNull { it.id }

        if (librosMongoIds.isEmpty()) return PageImpl(emptyList(), pageable, 0)

        val pageReserva: Page<Reserva> =  reservaRepository.findPrestadosPorMiConLibrosMongo(librosMongoIds, pageable) //usuarioId.toInt()
        return this.mapReservasToDto(pageReserva, pageable)
    }

    @Transactional(readOnly = true)
    fun getLibrosDisponiblesParaReserva(usuarioId: Int, filtros: FiltrosLibro): Page<ReservaLibroDTO> {
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NotFoundException("Usuario no encontrado") }

        val pageDeLibros = libroRepository.findDisponiblesParaReserva(usuarioId, filtros)

        // se transforma cada LibroDocument en una pre reserva y hace el mapeo (dto)
        return pageDeLibros.map { libro ->
            Reserva(
                lector = usuario,
                fechaDesde = filtros.fechaDesde,
                fechaHasta = filtros.fechaHasta,
                libroMongoId = libro.id!!,
            ).toReservaLibroDTO(libro)
        }
    }

    @Transactional(readOnly = true)
    fun getDetalleReserva(libroId: String, userId: Int, fechaDesde: LocalDate, fechaHasta: LocalDate): DetalleReservaDTO {
        val libro = libroRepository.findById(libroId).orElseThrow { NotFoundException("Libro no encontrado") }
        val usuario = usuarioRepository.findById(userId).orElseThrow{NotFoundException("Usuario no encontrado")}

        //Reserva simulada con fechas del front
        var reserva = Reserva(
            lector = usuario,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta,
            libroMongoId = libroId
        )

        val biblioKarmas = reserva.calcularBiblioKarmas(libro)
        val calificacionPromedio = libro.promedioCalificacion
        val ultimasResenias = reseniaRepository.findTop2ByLibroMongoIdOrderByFechaDesc(libroId);
        val reservasOcupadas = reservaRepository.findReservasOcupadas(libroId)

        return reservaAdapter.toDetalleReservaDto(
            reserva = reserva,
            bibliokarmas = biblioKarmas,
            calificacionPromedio = calificacionPromedio,
            ultimasResenias = ultimasResenias,
            reservasOcupadas = reservasOcupadas,
            libro = libro
        )
    }

    @Transactional(readOnly = false)
    fun crearReserva(userID: Int, reserva: CrearReservaDTO): ReservaDTO {

        val libro = libroRepository.findById(reserva.libroMongoID).orElseThrow { NotFoundException("Libro no encontrado") }
        val usuario = usuarioRepository.findById(userID).orElseThrow { NotFoundException("Usuario no encontrado") }

        var nuevaReserva = Reserva(
            lector = usuario,
            fechaDesde = reserva.fechaDesde,
            fechaHasta = reserva.fechaHasta,
            libroMongoId = reserva.libroMongoID
        )

        val seSuperpone = reservaRepository.existReservaSuperpuesta(libro.id!!, reserva.fechaDesde, reserva.fechaHasta)
        if (seSuperpone) {
            throw ConflictException("El libro ya se encuentra reservado en esas fechas.") //otra exception, el usuario la pifio
        }

        val bibliokarmasGanados = nuevaReserva.calcularBiblioKarmas(libro)
        usuario.aumentarBiblioKarmas(bibliokarmasGanados)
        usuarioRepository.save(usuario)
        reservaRepository.save(nuevaReserva)

//        val calificacionPromedio = libro.getCalificacionPromedio()

        //Todo: agregue un nuevo atributo libroMongoID dsp hay que enviarlo del front
        val libroMongo = libroRepository.findById(reserva.libroMongoID)
            .orElseThrow {
                IllegalStateException("No se encontró el libro)")
            }
        return reservaAdapter.toDto(nuevaReserva, bibliokarmasGanados, libroMongo)
    }

    private fun mapReservasToDto(pageReserva: Page<Reserva>, pageable: PageRequest  ): Page<ReservaDTO> {
        val reservasDto = pageReserva.content.map { reserva ->
            val libroMongoId = reserva.libroMongoId ?: throw IllegalStateException("La reserva no tiene libro Mongo asociado")

            val libroMongo = libroRepository.findById(libroMongoId)
                .orElseThrow {
                    IllegalStateException("No se encontró el libro Mongo $libroMongoId")
                }

            reservaAdapter.toDto(  reserva,reserva.calcularBiblioKarmas(libroMongo), libroMongo  )
        }

        return PageImpl(reservasDto, pageable, pageReserva.totalElements)
    }
}
