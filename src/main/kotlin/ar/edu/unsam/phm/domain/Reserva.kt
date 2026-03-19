package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class BusquedaPor(val value: String) {
    TAB_PRESTADOS_A_MI("prestados-a-mi"),
    TAB_PRESTADOS_POR_MI("prestados-por-mi")
}

enum class EstadoReserva(val value: String) {
    ACTIVO("ACTIVO"),
    DEVUELTO("DEVUELTO"),
    PROXIMO_A_VENCER("PROXIMO A VENCER")
}

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
        require(!fechaHasta.isBefore(fechaDesde)) { // no puedo reservar mis propios libros ...
            "La fecha hasta no puede ser anterior a la fecha desde, ni puedo reservar mis propios libros"
        }
        require(lector.id != libro.dueno.id){
            "No puedo reservar mis propios libros"
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

    fun agregarReseña(puntaje: Int, comentario: String, fechaCreacion: LocalDate = LocalDate.now()) {
        require( puntaje in 0..5){ "El puntaje debe estar entre 0 y 5"}
        require( comentario.isNotBlank()){"El comentario no puede estar vacio"}
        require(this.estaActiva() || this.estaFinalizada()) {
            "No podés reseñar un libro cuyo préstamo todavía no comenzó"
        }
       resenia = Resenia(
           lector.nombreCompleto, puntaje, comentario
       )
    }

    fun calcularDias() = ChronoUnit.DAYS.between(fechaDesde, fechaHasta).toInt() + 1

    fun calcularBiblioKarmas(cantidadReservas: Int): Int {
        return 5 * calcularDias() + libro.tipo.plusBiblioKarma(libro, lector, cantidadReservas) // pasar bibliokarmas como int, sirve pasar el objeto usuario completo?
    }

    fun calcularEstado(): String {
        if (estaFinalizada()) return EstadoReserva.DEVUELTO.value

        val diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaHasta)
        if (diasRestantes in 0..2) {
            return EstadoReserva.PROXIMO_A_VENCER.value
        }
        return EstadoReserva.ACTIVO.value
    }

    fun puedeCalificar(): Boolean = resenia == null && estaFinalizada()
}

data class ReservaSimulada(
    val reserva: Reserva,
    val cantidadReservasHistoricas: Int,
    val promedio: Double,
    val ultimasReservas: List<Reserva>,
    val reservasOcupadas: List<Reserva>,
    val bibliokarmas: Int
)

