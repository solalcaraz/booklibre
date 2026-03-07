package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId
import java.util.Date

class Libro(
    override var id: Int? = null,
    var titulo: String,
    val tipo: TipoDeLibro,
    var descripcion: String,
    var genero: Genero,
    var autor: String,
    val cantPaginas: Int,
    val ISBN13: String,
    var idioma: Idioma,
    var editorial: String,
    val fechaPublicacion: Date,
    var estado: Estado,
    val dueno: Usuario
) : EntidadConId {
    fun calcularBiblioKarmas(diasTotal: Int, usuario: Usuario): Int {
        return 5 * diasTotal + tipo.plusBiblioKarma(this, usuario)
    }

    fun calcularCalificacionPromedio(): Double {
        // TODO: definir como queda la reserva{puntaje, comentario}
        return 0.0
    }
}

enum class Genero {
    DRAMA,
    CIENCIA_FICCION,
    ROMANCE,
    AUTOAYUDA,
    DISENO,
    LITERATURA_CLASICA
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