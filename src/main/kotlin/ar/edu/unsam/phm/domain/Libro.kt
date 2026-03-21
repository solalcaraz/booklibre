package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.repository.EntidadConId
import java.time.LocalDate
import kotlin.math.ceil

data class LibroData(
    var titulo: String,
    var descripcion: String,
    var genero: Genero,
    var autor: String,
    var cantPaginas: Int,
    var isbn13: String,
    var idioma: Idioma,
    var editorial: String,
    var fechaPublicacion: LocalDate,
    var estado: Estado,
    val dueno: Usuario,
    var portada: String = Environment.API_URL + "/imagenes/libro-nuevo.jpg"
)
sealed class Libro(
    var data: LibroData,
    override var id: Int? = null
) : EntidadConId {
    abstract fun plusBiblioKarma(cantidadBibliokarmas: Int, cantidadReservas: Int): Int

    fun calcularCalificacionPromedio(puntuaciones: List<Int>): Double =
        if (puntuaciones.isEmpty()) 0.0 else puntuaciones.average()
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