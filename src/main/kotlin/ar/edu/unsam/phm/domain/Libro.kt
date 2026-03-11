package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Enviroment
import ar.edu.unsam.phm.repository.EntidadConId
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.Date

class Libro(
    override var id: Int? = null,
    var titulo: String,
    val tipo: TipoDeLibro,
    var descripcion: String,
    var genero: Genero,
    var autor: String,
    val cantPaginas: Int,
    val isbn13: String,
    var idioma: Idioma,
    var editorial: String,
    val fechaPublicacion: Date,
    var estado: Estado,
    val dueno: Usuario
) : EntidadConId {
    var imagenNombre: String = Enviroment.API_URL + "/imagenes/libro-nuevo.jpg"
    var portada: String = ""

    // Metodo para obtener la URL completa de la imagen, se provee desde el back
    @JsonIgnore
    fun getImagenUrl(): String = "images/$imagenNombre"

    fun calcularBiblioKarmas(diasTotal: Int, usuario: Usuario): Int {
        return 5 * diasTotal + tipo.plusBiblioKarma(this, usuario)
    }

    fun calcularCalificacionPromedio(): Double {
        // TODO: definir como queda la reserva{puntaje, comentario}
        return 0.0
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

enum class Idioma {
    ESPANOL,
    INGLES,
    FRANCES,
    PORTUGUES
}

enum class Estado {
    EXCELENTE,
    MUY_BUENO,
    BUENO,
    MALO,
    REGULAR
}