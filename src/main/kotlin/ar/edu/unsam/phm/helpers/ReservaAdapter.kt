package ar.edu.unsam.phm.helpers

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
        reservasOcupadas: List<Reserva>
    ): DetalleReservaDTO
    fun toDto(reserva: Reserva, calificacionPromedio: Double): ReservaDTO
}

@Service
class ReservaMapper(private val libroMapper: LibroAdapter): ReservaAdapter {

    override fun toDetalleReservaDto(reserva: Reserva,
                                     bibliokarmas: Int,
                                     calificacionPromedio: Double,
                                     ultimasResenias: List<Resenia>,
                                     reservasOcupadas: List<Reserva>): DetalleReservaDTO {
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
            libroDetalleDTO = libroMapper.toDetalleDto(reserva.libro)
        )
    }

    override fun toDtoReservaPag(page: Page<Reserva>): PaginationResponseDTO<ReservaDTO> {
        val reservasDto = page.content.map { this.toDto(it, it.libro.calcularCalificacionPromedio()) }
        return PaginationResponseDTO(
            items = reservasDto,
            page = page.number,
            size = page.size,
            totalItems = page.totalElements.toInt(),
            totalPages = page.totalPages
        )
    }

    override fun toDto(reserva: Reserva, calificacionPromedio: Double): ReservaDTO {
        val libroResumen = LibroResumen(reserva.libro.data.titulo, reserva.libro.data.autor, calificacionPromedio, reserva.libro.data.portada)
        return ReservaDTO( reserva.id, libroResumen, reserva.libro.dueno.nombreCompleto,
            reserva.calcularBiblioKarmas(), reserva.fechaDesde, reserva.fechaHasta, reserva.calcularEstado(), reserva.puedeCalificar())
    }
}
