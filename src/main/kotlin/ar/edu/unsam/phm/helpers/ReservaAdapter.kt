package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.dto.*
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

interface ReservaAdapter {
    fun toDtoReservaPag(page: Page<Reserva>): PaginationResponseDTO<ReservaDTO>
    fun toDetalleReservaDto(
        reserva: Reserva,
        bibliokarmas: Int,
        calificacionPromedio: Double,
        ultimasResenias: List<Resenia>,
        reservasOcupadas: List<Reserva>,
        //Agrego aca para evitar el problema del Bean.
        libro: LibroDocument
    ): DetalleReservaDTO
    fun toDto(reserva: Reserva): ReservaDTO
}

@Service
//LibroDocument no es un Bean por lo que no funca.
class ReservaMapper(private val libroMapper: LibroMapper): ReservaAdapter {

    override fun toDetalleReservaDto(
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

    override fun toDtoReservaPag(page: Page<Reserva>): PaginationResponseDTO<ReservaDTO> {
        val reservasDto = page.content.map {
            this.toDto(it)
        }

        return PaginationResponseDTO(
            items = reservasDto,
            page = page.number,
            size = page.size,
            totalItems = page.totalElements.toInt(),
            totalPages = page.totalPages
        )
    }

    override fun toDto(reserva: Reserva): ReservaDTO {
        return ReservaDTO(
            reserva.id,
            reserva.libroResumen,
            reserva.libroResumen.duenoNombre,
            reserva.bibliokarmasGanados,
            reserva.fechaDesde,
            reserva.fechaHasta,
            reserva.calcularEstado(),
            reserva.puedeCalificar()
        )
    }
}
