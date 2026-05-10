package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.LibroMongo
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.dto.*
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

interface ReservaAdapter {
    fun toDtoReservaPag(page: Page<ReservaDTO>): PaginationResponseDTO<ReservaDTO>
    fun toDetalleReservaDto(
        reserva: Reserva,
        bibliokarmas: Int,
        calificacionPromedio: Double,
        ultimasResenias: List<Resenia>,
        reservasOcupadas: List<Reserva>
    ): DetalleReservaDTO
    fun toDto(reserva: Reserva, bibliokarmas: Int, libroMongo: LibroMongo): ReservaDTO
}

@Service
class ReservaMapper(private val libroMapper: LibroAdapter): ReservaAdapter {

    override fun toDetalleReservaDto(
        reserva: Reserva,
        bibliokarmas: Int,
        calificacionPromedio: Double,
        ultimasResenias: List<Resenia>,
        reservasOcupadas: List<Reserva>
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
            libroDetalleDTO = libroMapper.toDetalleDto(reserva.libro)
        )
    }

    override fun toDtoReservaPag(page: Page<ReservaDTO>): PaginationResponseDTO<ReservaDTO> {
        return PaginationResponseDTO(
            items = page.content,
            page = page.number,
            size = page.size,
            totalItems = page.totalElements.toInt(),
            totalPages = page.totalPages
        )
    }

    override fun toDto(reserva: Reserva, bibliokarmas: Int, libroMongo: LibroMongo): ReservaDTO {
        val libroResumen = LibroResumen(
            libroMongo.titulo,
            libroMongo.autor,
            libroMongo.promedioCalificacion,
            libroMongo.portada
        )
        return ReservaDTO(
            reserva.id,
            libroResumen,
            libroMongo.dueno.nombreCompleto,
            bibliokarmas,           // valor recibido como parámetro, no recalculado
            reserva.fechaDesde,
            reserva.fechaHasta,
            reserva.calcularEstado(),
            reserva.puedeCalificar()
        )
    }
}
