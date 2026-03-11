package ar.edu.unsam.phm.dto

import java.time.LocalDate

data class ReservaDTO(
    val libroResumen: LibroResumen,
    val prestadoPor: String,
    val bibliokarma: Number,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate
)

data class ReservaRequestDTO(
    val inputSearch: String
)
