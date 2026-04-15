package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.*
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

interface ReservaAdapter {
    fun toDtoReservaPag(page: Page<Reserva>): PaginationResponseDTO<ReservaDTO>
    fun toDetalleReservaDto(
        reserva: Reserva,
        bibliokarmas: Int,
        calificacionPromedio: Double,
        ultimasReservas: List<Reserva>,
        reservasOcupadas: List<Reserva>
    ): DetalleReservaDTO
    fun toDto(reserva: Reserva): ReservaDTO
}

@Service
class ReservaMapper(private val libroMapper: LibroAdapter): ReservaAdapter {

    override fun toDetalleReservaDto(reserva: Reserva,
                                     bibliokarmas: Int,
                                     calificacionPromedio: Double,
                                     ultimasReservas: List<Reserva>,
                                     reservasOcupadas: List<Reserva>): DetalleReservaDTO {
        return DetalleReservaDTO(
            bibliokarmasTotales = bibliokarmas,
            calificacionPromedio = calificacionPromedio,
            ultimasResenias = ultimasReservas.map { reserva ->
                val resenia = reserva.resenia!!
                ReseniaDTO(
                    id = reserva.id,
                    nombreUsuario = resenia.nombreUsuario,
                    puntuacion = resenia.puntuacion,
                    comentario = resenia.comentario
                )
            },
            rangosFechasOcupadas = reservasOcupadas.map { reservaOcupada ->
                RangoFechaDTO(
                    fechaDesde = reservaOcupada.fechaDesde,
                    fechaHasta = reservaOcupada.fechaHasta
                )
            },
            libroDetalleDTO = libroMapper.toDetalleDto(reserva.libro)
        )
    }

    override fun toDtoReservaPag(page: Page<Reserva>): PaginationResponseDTO<ReservaDTO> {
        val reservasDto = page.content.map { this.toDto(it ) }
        return PaginationResponseDTO(
            items = reservasDto,
            page = page.number,
            size = page.size,
            totalItems = page.totalElements.toInt(),
            totalPages = page.totalPages
        )
    }

    override fun toDto(reserva: Reserva): ReservaDTO {
        //Todo: implementar logica de bibliokarmas
        val libroResumen = LibroResumen(reserva.libro.data.titulo, reserva.libro.data.autor, reserva.libro.calcularCalificacionPromedio(), reserva.libro.data.portada)
        return ReservaDTO( reserva.id, libroResumen, reserva.libro.dueno.nombreCompleto,
            0, reserva.fechaDesde, reserva.fechaHasta, reserva.calcularEstado(), reserva.puedeCalificar(), reserva.resenia?.puntuacion)
    }
}
