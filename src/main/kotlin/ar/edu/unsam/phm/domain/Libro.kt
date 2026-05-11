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
import jakarta.persistence.OneToMany
import jakarta.persistence.CascadeType
import jakarta.persistence.Table
import org.hibernate.annotations.Check
import org.hibernate.annotations.Formula
import org.hibernate.annotations.SQLRestriction
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

    @Column(nullable = false, length = 500)
    var portada: String = Environment.API_URL + "/imagenes/libro-nuevo.jpg"
)
@Entity
@Table(name = "libros")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_libro", discriminatorType = DiscriminatorType.STRING)
/* Esta restriction hace que cuando doy baja logica de un libro, se ignoren también las reservas del mismo */
@SQLRestriction("activo = true")
abstract class Libro(
    @Embedded
    var data: LibroData,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    val dueno: Usuario,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null
) : EntidadConId {
    //Delego este calculo a la DB.
    @Formula("(SELECT COUNT(r.id) FROM reservas r WHERE r.id_libro = id)")
    val cantidadReservas: Int = 0

    @Formula("(SELECT COALESCE(AVG(r.puntuacion), 0.0) FROM resenias r WHERE r.id_libro = id)")
    private val calificacionPromedioDb: Double = 0.0

    /*
    @OneToMany(mappedBy = "libro", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private val resenias: MutableList<Resenia> = mutableListOf()
*/
    @Column(nullable = false)
    open var activo: Boolean = true

    abstract fun plusBiblioKarma(cantidadBibliokarmas: Int): Int

    /*
    fun calcularCalificacionPromedio(): Double {
        if (resenias.isEmpty()) return 0.0
        val sumaPuntajes = resenias.sumOf { it.puntuacion }
        val promedio = sumaPuntajes.toDouble() / resenias.size
        return String.format(Locale.US, "%.1f", promedio).toDouble()
        //Para que sea 4.5 y no 4,5.
    }

    fun getCalificacionPromedio(): Double{
        return String.format(Locale.US, "%.1f", calificacionPromedioDb).toDouble()
    }

    fun agregarResenia(resenia: Resenia) {
        resenias.add(resenia)
    }
*/
    //Delego esto al repo para no cargar toda la lista.
    /*fun getUltimasDosResenias(): List<Resenia> {
        return resenias.sortedByDescending { it.fecha }.take(2)
    }
     */
}

@Entity
@DiscriminatorValue("COMUN")
class LibroComun(data: LibroData, usuario: Usuario) : Libro(data, usuario) {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int =
        if (cantidadBibliokarmas < 1000) data.cantPaginas * 5 else data.cantPaginas * 2
}

@Entity
@DiscriminatorValue("DEDICATORIA")
class LibroDedicatoria(data:LibroData, usuario: Usuario) : Libro(data, usuario) {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int =
        200 + 10 * cantidadReservas
}

@Entity
@DiscriminatorValue("COLECCIONABLE")
class LibroColeccionable(data:LibroData, usuario: Usuario) : Libro(data, usuario) {
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