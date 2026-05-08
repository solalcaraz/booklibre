package ar.edu.unsam.phm.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ar.edu.unsam.phm.exceptions.BadRequestException
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// La pregunta es si mudamos todas las reservas de modelo o si solo conservamos
// una versión reducida de las reservas con el mínimo indispensable para responder
// la disponibilidad del libro con aggregate
@Document(collection = "reservas")
class ReservaDocument(
    @Id
    var id: String? = null,

    val idLibro: String,    // Referencia al LibroDocument en Mongo

    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate
) {

    init {
        if (fechaHasta.isBefore(fechaDesde)) {
            throw BadRequestException("La fecha hasta no puede ser anterior a la fecha desde.")
        }
    }

    fun estaActiva(hoy: LocalDate = LocalDate.now()): Boolean =
        !hoy.isBefore(fechaDesde) && !hoy.isAfter(fechaHasta)

    fun seSuperponeCon(desde: LocalDate, hasta: LocalDate): Boolean =
        !(hasta.isBefore(fechaDesde) || desde.isAfter(fechaHasta))

    fun estaFinalizada(): Boolean = LocalDate.now().isAfter(this.fechaHasta)

    fun calcularEstado(): String {
        if (estaFinalizada()) return EstadoReserva.DEVUELTO.value
        val diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaHasta)
        return if (diasRestantes in 0..2) EstadoReserva.PROXIMO_A_VENCER.value else EstadoReserva.ACTIVO.value
    }
}