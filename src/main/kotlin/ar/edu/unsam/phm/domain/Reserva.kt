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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    val lector: Usuario,

    @Column(name = "fecha_desde", nullable = false)
    val fechaDesde: LocalDate,

    @Column(name = "fecha_hasta", nullable = false)
    val fechaHasta: LocalDate,

    //Una reseña no existe sin reserva, un usuario puede comentar sobre un libro que reservo.
    @OneToOne(mappedBy = "reserva", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var resenia: Resenia? = null,
    //Cascade all porque resenia depende de reserva, mismo ciclo de vida

) : EntidadConId {
    init {
        if (fechaHasta.isBefore(fechaDesde)) {
            throw BadRequestException("La fecha hasta no puede ser anterior a la fecha desde.")
        }
        if (lector.id == libro.data.dueno.id) {
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

    fun validarReseña(resenia: Resenia) {
        if (resenia.puntuacion !in 0..5) {
            throw BadRequestException("El puntaje debe estar entre 0 y 5")
        }
        if (resenia.comentario.isBlank()) {
            throw BadRequestException("El comentario no puede estar vacio")
        }
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

    fun puedeCalificar(): Boolean = resenia == null && estaFinalizada()
}
