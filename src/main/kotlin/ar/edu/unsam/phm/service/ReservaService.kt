package ar.edu.unsam.phm.service

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
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.LibroSpecifications
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.text.toInt
import org.springframework.transaction.annotation.Transactional

@Service
class ReservaService(
    private val reservaRepositorio: ReservaRepositorio,
    private val reservaRepository: ReservaRepository,
    private val libroRepositorio: LibroRepository,
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

    @Transactional(readOnly = true)
    fun getLibrosDisponiblesParaReserva(usuarioId: Int, filtros: FiltrosLibro): Page<Reserva> {
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NotFoundException("Usuario no encontrado") }

        val idLibrosOcupados = reservaRepositorio.getIdLibrosOcupadosEntre(filtros.fechaDesde, filtros.fechaHasta)

        // se arma la specification combinando todos los filtros (DEBERIA SER UN HELPER PRIVADO?)
        val spec = Specification.where(LibroSpecifications.noEsDelUsuario(usuarioId))
            .and(LibroSpecifications.noEstaOcupado(idLibrosOcupados))
            .and(LibroSpecifications.conGeneros(filtros.generos))
            .and(LibroSpecifications.conPaginasMin(filtros.cantPaginasMin))
            .and(LibroSpecifications.conPaginasMax(filtros.cantPaginasMax))
            .and(LibroSpecifications.conIsbn(filtros.isbn))
            .and(LibroSpecifications.conUsuarioPrestador(filtros.usuarioPrestador))
            .and(LibroSpecifications.conBusqueda(filtros.busqueda))

        // se obtiene la pagina a mostrar
        val pageable = PageRequest.of(filtros.numeroPagina, filtros.tamanoPaginado, filtros.orden.toSort())

        // se transforma Page<Libro> en Page<Reserva>
        return libroRepositorio.findAll(spec, pageable).map { libro ->
            Reserva(
                libro = libro,
                lector = usuario,
                fechaDesde = filtros.fechaDesde,
                fechaHasta = filtros.fechaHasta)
        }
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

        val biblioKarmas = reserva.calcularBiblioKarmas()
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
