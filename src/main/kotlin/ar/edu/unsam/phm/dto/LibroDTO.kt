package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.TipoDeLibro
import ar.edu.unsam.phm.exceptions.BusinessException

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
    val dueno: String
    // calificacion y foto
)

fun Libro.toDTO(): LibroHomeDTO {
    val tipoDTO = when (this.tipo) {
        is Comun -> TipoDeLibroDTO.COMUN
        is Dedicatoria -> TipoDeLibroDTO.DEDICATORIA
        is Coleccionable -> TipoDeLibroDTO.COLECCIONABLE
        else -> throw BusinessException("El tipo '${this.tipo}' es desconocido")
    }
    return LibroHomeDTO(
        id = this.id!!,
        titulo = this.titulo,
        genero = this.genero,
        autor = this.autor,
        tipo = tipoDTO,
        ISBN13 = this.ISBN13,
        idioma = this.idioma,
        estado = this.estado,
        dueno = this.dueno.nombreCompleto
    )
}