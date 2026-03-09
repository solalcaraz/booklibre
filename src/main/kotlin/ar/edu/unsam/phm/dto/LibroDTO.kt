package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.exceptions.LibroException

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
data class LibroDetalleDTO(
    val id: Int?,
    val titulo: String,
    val tipo: TipoDeLibroDTO,
    val genero: Genero,
    val autor: String,
    val idioma: Idioma,
    val estado: Estado,
    val editorial: String,
    val fechaPublicacion: String,
    val paginas: Int,
    val isbn13: String,
    val descripcion: String
)

fun Libro.toDetalleDTO(): LibroDetalleDTO {
    val tipoDTO = when (this.tipo) {
        is Comun -> TipoDeLibroDTO.COMUN
        is Dedicatoria -> TipoDeLibroDTO.DEDICATORIA
        is Coleccionable -> TipoDeLibroDTO.COLECCIONABLE
        else -> throw LibroException.tipoInvalido(this)
    }

    return LibroDetalleDTO(
        id = this.id,
        titulo = this.titulo,
        tipo = tipoDTO,
        genero = this.genero,
        autor = this.autor,
        idioma = this.idioma,
        estado = this.estado,
        editorial = this.editorial,
        fechaPublicacion = this.fechaPublicacion.toString(),
        paginas = this.cantPaginas,
        isbn13 = this.isbn13,
        descripcion = this.descripcion
    )
}