package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.repository.EntidadConId
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import kotlin.math.ceil

@Embeddable
data class LibroData(
    @Column(nullable = false)
    var titulo: String,

    @Column(nullable = false)
    var descripcion: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var genero: Genero,

    @Column(nullable = false)
    var autor: String,

    @Column(nullable = false)
    var cantPaginas: Int,

    @Column(nullable = false)
    var isbn13: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var idioma: Idioma,

    @Column(nullable = false)
    var editorial: String,

    @Column(nullable = false)
    var fechaPublicacion: LocalDate,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var estado: Estado,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    val dueno: Usuario,

    @Column(nullable = false)
    var portada: String = Environment.API_URL + "/imagenes/libro-nuevo.jpg"
)
@Entity
@Table(name = "libro")
abstract class Libro(
    @Embedded
    var data: LibroData,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null
) : EntidadConId {
    @Column(nullable = false)
    var calificacionPromedio: Double = 0.0

    @Column(nullable = false)
    var cantidadReservas: Int = 0

    abstract fun plusBiblioKarma(cantidadBibliokarmas: Int, cantidadReservas: Int): Int

    fun calcularCalificacionPromedio(puntuaciones: List<Int>) {
        if (puntuaciones.isEmpty()) calificacionPromedio=0.0 else calificacionPromedio=puntuaciones.average()
    }

    fun aumentarReserva() { cantidadReservas++ }
}

class LibroComun(data: LibroData) : Libro(data) {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int, cantidadReservas: Int): Int =
        if (cantidadBibliokarmas < 1000) data.cantPaginas * 5 else data.cantPaginas * 2
}

class LibroDedicatoria(data:LibroData) : Libro(data) {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int, cantidadReservas: Int): Int =
        200 + 10 * cantidadReservas
}

class LibroColeccionable(data:LibroData) : Libro(data) {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int, cantidadReservas: Int): Int {
        val quintaParte = ceil(cantidadBibliokarmas / 5.0).toInt()
        return quintaParte + data.cantPaginas
    }
}

enum class Genero(val label: String) {
    DRAMA("Drama"),
    CIENCIA_FICCION("Ciencia Ficción"),
    ROMANCE("Romance"),
    AUTOAYUDA("Autoayuda"),
    DISENO("Diseño"),
    LITERATURA_CLASICA("Literatura Clásica"),
}

enum class Idioma(val label: String) {
    ESPANOL("Español"),
    INGLES("Inglés"),
    FRANCES("Francés"),
    PORTUGUES("Portugués")
}

enum class Estado(val label: String) {
    EXCELENTE("Excelente"),
    MUY_BUENO("Muy bueno"),
    BUENO("Bueno"),
    MALO("Malo"),
    REGULAR("Regular")
}