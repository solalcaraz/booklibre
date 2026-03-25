package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.*
import org.springframework.stereotype.Service

interface ReservaAdapter {
    fun toDtoReservaPag(page: PaginationResponseDTO<Reserva>, promedioPorLibro: Map<Int, Double>, cantidadDeReservasPorLibro: Map<Int, Int>): PaginationResponseDTO<ReservaDTO>
    fun toDetalleReservaDto(
        reserva: Reserva,
        bibliokarmas: Int,
        calificacionPromedio: Double,
        ultimasReservas: List<Reserva>,
        reservasOcupadas: List<Reserva>
    ): DetalleReservaDTO
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

    override fun toDtoReservaPag(page: PaginationResponseDTO<Reserva>, promedioPorLibro: Map<Int, Double>, cantidadDeReservasPorLibro: Map<Int, Int>): PaginationResponseDTO<ReservaDTO> {
        val reservasDto = page.items.map { this.toDto(it, promedioPorLibro[it.libro.id]!!, cantidadDeReservasPorLibro[it.libro.id]!! ) }
        return PaginationResponseDTO(
            items = reservasDto,
            page = page.page,
            size = page.size,
            totalItems = page.totalItems,
            totalPages = page.totalPages
        )
    }

    private fun toDto(reserva: Reserva, puntuacion: Double, cantidadReservas: Int): ReservaDTO {
        val libroResumen = LibroResumen(reserva.libro.data.titulo, reserva.libro.data.autor, puntuacion, reserva.libro.data.portada)
        return ReservaDTO( reserva.id, libroResumen, reserva.lector.nombreCompleto,
            reserva.calcularBiblioKarmas(cantidadReservas), reserva.fechaDesde, reserva.fechaHasta, reserva.calcularEstado(), reserva.puedeCalificar(), reserva.resenia?.puntuacion)
    }
}
