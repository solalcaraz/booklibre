package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.dto.CrearReservaDTO
import ar.edu.unsam.phm.dto.DetalleReservaDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReservaLibroDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.dto.toReservaLibroDTO
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.ReservaAdapter
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.text.toInt
import org.springframework.transaction.annotation.Transactional

@Service
class ReservaService(
    private val reservaRepositorio: ReservaRepositorio,
    private val reservaRepository: ReservaRepository,
    private val libroRepositorio: LibroRepositorio,
    private val libroRepository: LibroRepository,
    private val usuarioRepository: UsuarioRepository,
    private val reservaAdapter: ReservaAdapter
) {
    fun getMisReservas(payload: PaginationReqDTO<ReservaRequestDTO>): Page<Reserva> {
        val usuarioId = payload.filter?.usuarioId ?: throw BadRequestException("Falta el ID del usuario en el payload")
        return reservaRepository.findPrestadosAMi(
            usuarioId.toInt(),
            payload.filter.inputSearch,
            PageRequest.of(payload.pageNumber, payload.pageSize)
        )
    }

    fun getPrestadosPorMi(payload: PaginationReqDTO<ReservaRequestDTO>): Page<Reserva> {
        val usuarioId = payload.filter?.usuarioId ?: throw BadRequestException("Falta el ID del usuario en el payload")
        return reservaRepository.findPrestadosPorMi(
            usuarioId.toInt(),
            payload.filter.inputSearch,
            PageRequest.of(payload.pageNumber, payload.pageSize)
        )
    }

    fun getLibrosDisponiblesParaReserva(usuarioId: Int, filtros: FiltrosLibro): PaginationResponseDTO<ReservaLibroDTO> {
        // valido que el id del usuario existe y lo asigno
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NotFoundException("Usuario no encontrado") }

        /* filtro los id de los libros que estan ocupados en la fecha de filtros (id porque es un dato mas liviano que traer el objeto completo.
            para luego descartarlos en el filtro que hace el repo de libros porque pueden haber libros disponibles pero que nunca se reservaron
         */
        val idLibrosOcupados = reservaRepositorio.getIdLibrosOcupadosEntre(filtros.fechaDesde, filtros.fechaHasta)

        // trae la pre reserva de los libros filtrados y ordenados
        val reservaDeLibrosDisponibles =
            libroRepositorio.getLibrosDisponibles(filtros, usuario.id!!, idLibrosOcupados).map { libro ->
                val cantidadReservas = reservaRepositorio.getCantidadReservasPorLibro(libro.id!!)
                val puntuaciones = reservaRepositorio.getPuntuacionesByLibro(libro.id!!)
                Reserva(
                    libro = libro, lector = usuario, fechaDesde = filtros.fechaDesde, fechaHasta = filtros.fechaHasta
                ).toReservaLibroDTO(cantidadReservas, puntuaciones)
            }

        return paginate(reservaDeLibrosDisponibles, filtros.numeroPagina, filtros.tamanoPaginado)
    }

    @Transactional(readOnly = true)
    fun getDetalleReserva(libroId: Int, userId: Int, fechaDesde: LocalDate, fechaHasta: LocalDate): DetalleReservaDTO {
        val libro = libroRepository.findById(libroId).orElseThrow { NotFoundException("Libro no encontrado") }
        val usuario = usuarioRepository.findById(userId).orElseThrow { NotFoundException("Usuario no encontrado") }

        //Reserva simulada con fechas del front
        var reserva = Reserva(
            libro = libro,
            lector = usuario,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta,
        )

        val cantidadReservas = libro.cantidadReservas
        val biblioKarmas = reserva.calcularBiblioKarmas(cantidadReservas)
        val calificacionPromedio = libro.calcularCalificacionPromedio()
        val ultimasReservas = reservaRepository.findTop2UltimasReservas(libroId)
        val reservasOcupadas = reservaRepository.findReservasOcupadas(libroId)

        return reservaAdapter.toDetalleReservaDto(
            reserva = reserva,
            bibliokarmas = biblioKarmas,
            calificacionPromedio = calificacionPromedio,
            ultimasReservas = ultimasReservas,
            reservasOcupadas = reservasOcupadas
        )
    }

    @Transactional(readOnly = false)
    fun crearReserva(userID: Int, reserva: CrearReservaDTO): Reserva {

        val libro = libroRepository.findById(reserva.libroId).orElseThrow { NotFoundException("Libro no encontrado") }
        val usuario = usuarioRepository.findById(userID).orElseThrow { NotFoundException("Usuario no encontrado") }

        var nuevaReserva = Reserva(
            libro = libro,
            lector = usuario,
            fechaDesde = reserva.fechaDesde,
            fechaHasta = reserva.fechaHasta,
        )

        val seSuperpone = reservaRepository.existReservaSuperpuesta(libro.id!!, reserva.fechaDesde, reserva.fechaHasta)
        if (seSuperpone) {
            throw ConflictException("El libro ya se encuentra reservado en esas fechas.") //otra exception, el usuario la pifio
        }

        val cantidadReservas = libro.cantidadReservas
        val bibliokarmasGanados = nuevaReserva.calcularBiblioKarmas(cantidadReservas)
        usuario.aumentarBiblioKarmas(bibliokarmasGanados)
        usuarioRepository.save(usuario)
        reservaRepository.save(nuevaReserva)
        libro.aumentarReserva()
        return nuevaReserva
    }
}
