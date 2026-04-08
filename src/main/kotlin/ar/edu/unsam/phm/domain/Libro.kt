package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.repository.EntidadConId
import com.fasterxml.jackson.annotation.JsonValue
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Check
import java.time.LocalDate
import java.util.Locale
import kotlin.math.ceil

@Embeddable
data class LibroData(
    @Column(nullable = false)
    var titulo: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var descripcion: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var genero: Genero,

    @Column(nullable = false)
    var autor: String,

    @Column(nullable = false)
    var cantPaginas: Int,

    @Column(nullable = false, length = 20)
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

    @Column(nullable = false, length = 500)
    var portada: String = Environment.API_URL + "/imagenes/libro-nuevo.jpg"
)
@Entity
@Table(name = "libro")
/* CHECK a modo documentacion
cuando se agregue el script con flyway, esta linea seria redundante
por el momento, cada vez que se genera el ddl, se lee la anotacion CHECK y se agrega como validacion
 */
@Check(constraints = "tipo_libro IN ('COMUN', 'DEDICATORIA', 'COLECCIONABLE')")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_libro", discriminatorType = DiscriminatorType.STRING)

abstract class Libro(
    @Embedded
    var data: LibroData,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null
) : EntidadConId {
    @Column(nullable = false)
    var sumaPuntajes: Int = 0

    @Column(nullable = false)
    var cantidadResenias: Int = 0

    @Column(nullable = false)
    var cantidadReservas: Int = 0

    @Column(nullable = false)
    var activo = true

    abstract fun plusBiblioKarma(cantidadBibliokarmas: Int): Int

    fun calcularCalificacionPromedio(): Double {
        if (cantidadResenias == 0) return 0.0
        val promedio = sumaPuntajes.toDouble() / cantidadResenias
        return String.format(Locale.US, "%.1f", promedio).toDouble()
        //Para que sea 4.5 y no 4,5.
    }

    fun aumentarReserva() { cantidadReservas++ }

    fun agregarPuntaje(puntaje: Int) {
        sumaPuntajes += puntaje
        cantidadResenias += 1
    }
}

@Entity
@DiscriminatorValue("COMUN")
class LibroComun(data: LibroData) : Libro(data) {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int =
        if (cantidadBibliokarmas < 1000) data.cantPaginas * 5 else data.cantPaginas * 2
}

@Entity
@DiscriminatorValue("DEDICATORIA")
class LibroDedicatoria(data:LibroData) : Libro(data) {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int =
        200 + 10 * cantidadReservas
}

@Entity
@DiscriminatorValue("COLECCIONABLE")
class LibroColeccionable(data:LibroData) : Libro(data) {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int,): Int {
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
    LITERATURA_CLASICA("Literatura Clásica");

    @JsonValue
    fun toJson() = label
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