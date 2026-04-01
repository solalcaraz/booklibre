package ar.edu.unsam.phm.dto

import java.time.LocalDate

data class CrearReservaDTO(
  val libroId: Int,
  val fechaDesde: LocalDate,
  val fechaHasta: LocalDate
)
