package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Environment
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
import kotlin.math.ceil

@Document(collection = "libros")
abstract class LibroDocument {
    @Id
    var id: String? = null

    var titulo: String = ""
    var descripcion: String = ""
    var genero: Genero = Genero.DRAMA
    var autor: String = ""
    var cantPaginas: Int = 0
    var isbn13: String = ""
    var idioma: Idioma = Idioma.ESPANOL
    var editorial: String = ""
    var fechaPublicacion: LocalDate = LocalDate.now()
    var estado: Estado = Estado.BUENO
    var portada: String = Environment.API_URL + "/imagenes/libro-nuevo.jpg"
    lateinit var dueno: UsuarioLibroDocument
    var cantidadReservas: Int = 0

    @Field("calificacionPromedio")
    var promedioCalificacion: Double = 0.0
    var activo: Boolean = true

    var reservasPendientes: MutableList<ReservaPendiente> = mutableListOf()

    abstract fun plusBiblioKarma(cantidadBibliokarmas: Int): Int

    // Ahora esto se calcularía en el momento de enviar una calificación,
    // directamente en el repo de reseñas, y se dispararía la actualización
    // del valor en el documento
//    fun calcularCalificacionPromedio(): Double {
//        if (resenias.isEmpty()) return 0.0
//        val sumaPuntajes = resenias.sumOf { it.puntuacion }
//        val promedio = sumaPuntajes.toDouble() / resenias.size
//        return String.format(Locale.US, "%.1f", promedio).toDouble()
//    }

    // Esta función creo que ya es redundante porque no son privadas las variables
//    fun getCalificacionPromedio(): Double =
//        String.format(Locale.US, "%.1f", promedioCalificacion).toDouble()

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
        sumarReserva()
        // sumamos en vez de hacer reservasPendientes.size porque las reservas
        // vencidas ya no van a estar acá
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
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int =
        if (cantidadBibliokarmas < 1000) cantPaginas * 5 else cantPaginas * 2
}

@TypeAlias("DEDICATORIA")
class LibroDedicatoriaDocument : LibroDocument() {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int =
        200 + 10 * cantidadReservas
}

@TypeAlias("COLECCIONABLE")
class LibroColeccionableDocument : LibroDocument() {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int {
        val quintaParte = ceil(cantidadBibliokarmas / 5.0).toInt()
        return quintaParte + cantPaginas
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