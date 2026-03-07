package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.TipoDeLibro

enum class TipoDeLibroDTO {
    COMUN,
    DEDICATORIA,
    COLECCIONABLE
}

data class LibroHomeDTO(
    var id: Int,
    var titulo: String,
    var genero: Genero,
    var autor: String,
    //var tipo: TipoDeLibroDTO,
    var ISBN13: String,
    var idioma: Idioma,
    var estado: Estado,
    // val dueno: UsuarioHomeDTO ¿?
    // calificacion y foto
)

fun Libro.toDTO(): LibroHomeDTO {
    return LibroHomeDTO(
        id = this.id!!,
        titulo = this.titulo,
        genero = this.genero,
        autor = this.autor,
        ISBN13 = this.ISBN13,
        idioma = this.idioma,
        estado = this.estado,
    )
}