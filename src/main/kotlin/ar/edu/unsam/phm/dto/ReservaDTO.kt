package ar.edu.unsam.phm.dto

import java.time.LocalDate

data class ReservaDTO(
    val id: Int?,
    val libroResumen: LibroResumen,
    val prestadoPor: String,
    val bibliokarma: Number,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate,
    val estado: String,
    val puedeCalificar: Boolean,
)

data class ReservaRequestDTO(
    val inputSearch: String,
    val usuarioId: String,
)
