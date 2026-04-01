package ar.edu.unsam.phm.dto

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