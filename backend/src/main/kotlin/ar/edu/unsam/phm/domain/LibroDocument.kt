package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.env.Environment
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.Sharded
import java.time.LocalDate
import java.time.LocalDateTime

// En el cluster local la colección está particionada por dueño: sin la shard key,
// Mongo rechaza los save() que actualizan un libro. En Atlas (sin sharding) no molesta.
@Document(collection = "libros")
@Sharded(shardKey = ["dueno._id"])
abstract class LibroDocument : LibroCalculoBibliokarmas {
    @Id
    var id: String? = null

    var titulo: String = ""
    var descripcion: String = ""
    var genero: Genero = Genero.DRAMA
    var autor: String = ""
    override var cantPaginas: Int = 0
    var isbn13: String = ""
    var idioma: Idioma = Idioma.ESPANOL
    var editorial: String = ""
    var fechaPublicacion: LocalDate = LocalDate.now()
    var estado: Estado = Estado.BUENO
    var portada: String = Environment.API_URL + "/imagenes/libro-nuevo.jpg"
    lateinit var dueno: UsuarioLibroDocument
    override var cantidadReservas: Int = 0
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Field("calificacionPromedio")
    var promedioCalificacion: Double = 0.0
    var activo: Boolean = true

    var reservasPendientes: MutableList<ReservaPendiente> = mutableListOf()

    abstract fun tipo(): TipoDeLibroDTO

    override fun plusBiblioKarma(cantidadBiblioKarma: Int): Int =
        tipo().plusBiblioKarma(cantPaginas, cantidadReservas, cantidadBiblioKarma)

    fun estaDisponible(desde: LocalDate, hasta: LocalDate): Boolean {
        limpiarReservasVencidas()
        return reservasPendientes.none { it.seSuperponeCon(desde, hasta) }
    }

    private fun limpiarReservasVencidas() {
        val hoy = LocalDate.now()
        reservasPendientes.removeIf { it.fechaFin.isBefore(hoy) }
    }

    fun sumarReserva() { cantidadReservas++ }

    fun agregarReserva(reservaPendiente: ReservaPendiente) {
        reservasPendientes.add(reservaPendiente)
        // Se lleva un contador aparte porque las reservas vencidas se limpian de reservasPendientes
        sumarReserva()
    }
}

data class ReservaPendiente(
    val idReserva: Int,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate
) {
    fun seSuperponeCon(desde: LocalDate, hasta: LocalDate): Boolean {
        return !(hasta.isBefore(fechaInicio) || desde.isAfter(fechaFin))
    }
}

data class UsuarioLibroDocument(
    var id: Int?,
    var nombreCompleto: String,
    var imagen: String
)

@TypeAlias("COMUN")
class LibroComunDocument : LibroDocument() {
    override fun tipo() = TipoDeLibroDTO.COMUN
}

@TypeAlias("DEDICATORIA")
class LibroDedicatoriaDocument : LibroDocument() {
    override fun tipo() = TipoDeLibroDTO.DEDICATORIA
}

@TypeAlias("COLECCIONABLE")
class LibroColeccionableDocument : LibroDocument() {
    override fun tipo() = TipoDeLibroDTO.COLECCIONABLE
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
