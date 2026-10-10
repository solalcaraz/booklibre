package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.dto.*
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class ReservaMapper(private val libroMapper: LibroMapper) {

    fun toDetalleReservaDto(
        reserva: Reserva,
        bibliokarmas: Int,
        calificacionPromedio: Double,
        ultimasResenias: List<Resenia>,
        reservasOcupadas: List<Reserva>,
        libro: LibroDocument
    ): DetalleReservaDTO {
        return DetalleReservaDTO(
            bibliokarmasTotales = bibliokarmas,
            calificacionPromedio = calificacionPromedio,
            ultimasResenias = ultimasResenias.map { resenia ->
                ReseniaDTO(
                    id = resenia.id,
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
            libroDetalleDTO = libroMapper.toDetalleDto(libro)
        )
    }

    fun toDtoReservaPag(page: Page<Reserva>): PaginationResponseDTO<ReservaDTO> =
        page.map { toDto(it) }.toPaginationDTO()

    fun toDto(reserva: Reserva): ReservaDTO {
        return ReservaDTO(
            reserva.id,
            reserva.libroResumen,
            reserva.libroResumen.duenoNombre,
            reserva.bibliokarmasGanados,
            reserva.fechaDesde,
            reserva.fechaHasta,
            reserva.calcularEstado(),
            reserva.puedeCalificar(),
            reserva.puntuacionResenia()
        )
    }
}
