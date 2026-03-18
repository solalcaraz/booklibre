package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.ReservaSimulada
import java.time.LocalDate

//Data para el front.
data class DetalleReservaDTO(
    val libroDetalleDTO: LibroDetalleDTO,
    val bibliokarmasTotales: Int,
    val calificacionPromedio: Double,
    val ultimasResenias: List<ReseniaDTO>,
    val rangosFechasOcupadas: List<RangoFechaDTO>
)

data class ReseniaDTO(
    val id: Int?,
    val nombreUsuario: String,
    val puntuacion: Int,
    val comentario: String
)

data class RangoFechaDTO(
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate
)

//Serializacion a DTO.
fun ReservaSimulada.toDetalleReservaDTO(): DetalleReservaDTO {
    val libro = this.reserva.libro

    return DetalleReservaDTO(
        bibliokarmasTotales = this.bibliokarmas,
        calificacionPromedio = this.promedio,

        ultimasResenias = this.ultimasReservas.map { reserva ->
            val resenia = reserva.resenia!!
            ReseniaDTO(
                id = reserva.id,
                nombreUsuario = resenia.nombreUsuario,
                puntuacion = resenia.puntuacion,
                comentario = resenia.comentario
            )
        },
        rangosFechasOcupadas = this.reservasOcupadas.map { reservaOcupada ->
            RangoFechaDTO(
                fechaDesde = reservaOcupada.fechaDesde,
                fechaHasta = reservaOcupada.fechaHasta
            )
        },
        libroDetalleDTO = libro.toDetalleDTO(),
    )
}