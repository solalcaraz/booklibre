package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Environment
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
import java.util.Locale
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
    var idDueno: Int = 0
    //lateinit var dueno: Usuario
    var cantidadReservas: Int = 0
    @Field("calificacionPromedio")
    var promedioCalificacion: Double = 0.0
    var activo: Boolean = true

//    @Transient
//    private val resenias: MutableList<Resenia> = mutableListOf()

    abstract fun plusBiblioKarma(cantidadBibliokarmas: Int): Int

    // Ahora esto se calcularía en el momento de enviar una calificación,
    // directamente en el repo de reseñas, y se dispararía la actualización del valor en el documento
//    fun calcularCalificacionPromedio(): Double {
//        if (resenias.isEmpty()) return 0.0
//        val sumaPuntajes = resenias.sumOf { it.puntuacion }
//        val promedio = sumaPuntajes.toDouble() / resenias.size
//        return String.format(Locale.US, "%.1f", promedio).toDouble()
//    }

    fun getCalificacionPromedio(): Double =
        String.format(Locale.US, "%.1f", promedioCalificacion).toDouble()

//    fun agregarResenia(resenia: Resenia) {
//        resenias.add(resenia)
//        promedioCalificacion = calcularCalificacionPromedio()
//    }
}

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
