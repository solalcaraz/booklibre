package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.exceptions.LibroException
import java.time.LocalDate
import java.util.Date

enum class TipoDeLibroDTO {
    COMUN,
    DEDICATORIA,
    COLECCIONABLE
}

data class LibroHomeDTO(
    var id: Int?,
    var titulo: String,
    var genero: Genero,
    var autor: String,
    var tipo: TipoDeLibroDTO,
    var ISBN13: String,
    var idioma: Idioma,
    var estado: Estado,
    var dueno: String,
    var calificacionPromedio: Double,
    //var biblioKarmasTotal: Int
    // foto
)

fun Libro.toDTO(): LibroHomeDTO {
    val tipoDTO = when (this.tipo) {
        is Comun -> TipoDeLibroDTO.COMUN
        is Dedicatoria -> TipoDeLibroDTO.DEDICATORIA
        is Coleccionable -> TipoDeLibroDTO.COLECCIONABLE
        else -> throw LibroException.tipoInvalido(this)
    }
    return LibroHomeDTO(
        id = this.id!!,
        titulo = this.titulo,
        genero = this.genero,
        autor = this.autor,
        tipo = tipoDTO,
        ISBN13 = this.isbn13,
        idioma = this.idioma,
        estado = this.estado,
        dueno = this.dueno.nombreCompleto,
        calificacionPromedio = this.calcularCalificacionPromedio()
        //biblioKarmasTotal = this.calcularBiblioKarmas()
    )
}

data class MiLibroDTO(
    val id: Int?,
    val titulo: String,
    val autor: String,
    val estadoReserva: String,
    val genero: String,
    val agregadoEl: Date,
    val portada: String
)

// libro dentro de prestamos
data class LibroResumen(
    val titulo: String,
    val autor: String,
    val puntuacion: Number,
    val imgUrl: String,
)
