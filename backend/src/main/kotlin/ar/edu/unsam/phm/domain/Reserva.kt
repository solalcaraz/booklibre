package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.LibroResumen
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
    @JoinColumn(name = "id_usuario", nullable = false)
    val lector: Usuario,

    @Column(name = "fecha_desde", nullable = false)
    val fechaDesde: LocalDate,

    @Column(name = "fecha_hasta", nullable = false)
    val fechaHasta: LocalDate,

    @Column(name = "id_libro")
    val libroMongoId: String? = null

) : EntidadConId {
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "titulo", column = Column(name = "libro_titulo", nullable = false)),
        AttributeOverride(name = "autor", column = Column(name = "libro_autor", nullable = false)),
        AttributeOverride(name = "puntuacion", column = Column(name = "libro_puntuacion")),
        AttributeOverride(name = "imgUrl", column = Column(name = "libro_img_url", nullable = false)),
        AttributeOverride(name = "duenoNombre", column = Column(name = "libro_duenoNombre", nullable = false)),
        AttributeOverride(name = "duenoId", column = Column(name = "libro_duenoId", nullable = false)),
    )
    lateinit var libroResumen: LibroResumen

    @Column(name = "bibliokarmas_ganados", nullable = false)
    var bibliokarmasGanados: Int = 0

    @Column(nullable = false)
    private var yaResenio: Boolean = false

    init {
        if (fechaHasta.isBefore(fechaDesde)) {
            throw BadRequestException("La fecha hasta no puede ser anterior a la fecha desde.")
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

    fun calcularBiblioKarmas(libro: LibroCalculoBibliokarmas): Int {
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

    fun calificar(puntaje: Int) {
        yaResenio = true
        libroResumen.puntuacion = puntaje.toDouble()
    }

    fun puedeCalificar(): Boolean = !yaResenio && estaFinalizada()
}
