package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId
import java.time.LocalDate

class Reserva(
    override var id: Int? = null,
    val libro: Libro,
    val usuario: Usuario,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate
) : EntidadConId {
    init {
        require(!fechaHasta.isBefore(fechaDesde)) {
            "La fecha hasta no puede ser anterior a la fecha desde"
        }
    }

    fun estaActiva(hoy: LocalDate = LocalDate.now()): Boolean {
        return !hoy.isBefore(fechaDesde) && !hoy.isAfter(fechaHasta)
    }

    fun seSuperponeCon(desde: LocalDate, hasta: LocalDate): Boolean {
        return !(hasta.isBefore(fechaDesde) || desde.isAfter(fechaHasta))
    }

}