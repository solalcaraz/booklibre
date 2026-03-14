package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Reserva(
    override var id: Int? = null,
    val libro: Libro,
    val lector: Usuario,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate,

    //Una reseña no existe sin reserva, un usuario puede comentar sobre un libro que reservo.
    var resenia: Resenia? = null, //Después de devolver el libro.

) : EntidadConId {
    init {
        require(!fechaHasta.isBefore(fechaDesde)
                && lector.id != libro.dueno.id) { // no puedo reservar mis propios libros ...
            "La fecha hasta no puede ser anterior a la fecha desde, ni puedo reservar mis propios libros"
        }
    }

    fun estaActiva(hoy: LocalDate = LocalDate.now()): Boolean {
        return !hoy.isBefore(fechaDesde) && !hoy.isAfter(fechaHasta)
    }

    fun seSuperponeCon(desde: LocalDate, hasta: LocalDate): Boolean {
        return !(hasta.isBefore(fechaDesde) || desde.isAfter(fechaHasta))
    }

    fun estaFinalizada(): Boolean {
        return LocalDate.now().isAfter(this.fechaHasta)
    }

    //Métodos para trabajar con reseña
    fun agregarReseña(puntaje: Int, comentario: String) {
       require( puntaje in 0..5){ "El puntaje debe estar entre 0 y 5"}
       require( comentario.isNotBlank()){"El comentario no puede estar vacio"}
       resenia = Resenia(lector.nombreCompleto,puntaje, comentario = comentario)
    }

    fun calcularDias() = ChronoUnit.DAYS.between(fechaDesde, fechaHasta).toInt() + 1

    fun calcularBiblioKarmas(cantidadReservas: Int): Int {
        return 5 * calcularDias() + libro.tipo.plusBiblioKarma(libro, lector, cantidadReservas)
    }
}