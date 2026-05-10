package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Environment
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
import kotlin.math.ceil

@Document(collection = "libros")
abstract class LibroMongo {
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
    lateinit var dueno: UsuarioLibroMongo
    var cantidadReservas: Int = 0

    @Field("calificacionPromedio")
    var promedioCalificacion: Double = 0.0
    var activo: Boolean = true

    abstract fun plusBiblioKarma(cantidadBibliokarmas: Int): Int

}

data class UsuarioLibroMongo(
    val id: Int?,
    val nombreCompleto: String,
    val imagen: String
)

@TypeAlias("COMUN")
class LibroComunMongo : LibroMongo() {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int =
        if (cantidadBibliokarmas < 1000) cantPaginas * 5 else cantPaginas * 2
}

@TypeAlias("DEDICATORIA")
class LibroDedicatoriaMongo : LibroMongo() {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int =
        200 + 10 * cantidadReservas
}

@TypeAlias("COLECCIONABLE")
class LibroColeccionableMongo : LibroMongo() {
    override fun plusBiblioKarma(cantidadBibliokarmas: Int): Int {
        val quintaParte = ceil(cantidadBibliokarmas / 5.0).toInt()
        return quintaParte + cantPaginas
    }
}
