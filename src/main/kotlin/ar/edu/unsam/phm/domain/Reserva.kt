package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId
import ar.edu.unsam.phm.exceptions.BadRequestException
import jakarta.persistence.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class EstadoReserva(val value: String) {
    ACTIVO("ACTIVO"),
    DEVUELTO("DEVUELTO"),
    PROXIMO_A_VENCER("PROXIMO A VENCER")
}

@Entity
@Table(name = "reservas")
class Reserva(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false)
    val libro: Libro,

    //Todo: eliminar
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    val lector: Usuario,

    @Column(name = "fecha_desde", nullable = false)
    val fechaDesde: LocalDate,

    @Column(name = "fecha_hasta", nullable = false)
    val fechaHasta: LocalDate,

    @Column(name = "id_libro_mongo")
    val libroMongoId: String? = null

) : EntidadConId {
    @Column(nullable = false)
    private var yaResenio: Boolean = false

    init {
        if (fechaHasta.isBefore(fechaDesde)) {
            throw BadRequestException("La fecha hasta no puede ser anterior a la fecha desde.")
        }
        if (lector.id == libro.dueno.id) {
            throw BadRequestException("No puedo reservar mis propios libros.")
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

    fun calcularDias() = ChronoUnit.DAYS.between(fechaDesde, fechaHasta).toInt() + 1

    fun calcularBiblioKarmas(): Int {
        return 5 * calcularDias() + libro.plusBiblioKarma(lector.bibliokarmas)
    }

    fun calcularEstado(): String {
        if (estaFinalizada()) return EstadoReserva.DEVUELTO.value

        val diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaHasta)
        if (diasRestantes in 0..2) {
            return EstadoReserva.PROXIMO_A_VENCER.value
        }
        return EstadoReserva.ACTIVO.value
    }

    fun calificar() {
        yaResenio = true
    }

    fun puedeCalificar(): Boolean = !yaResenio && estaFinalizada()
}
