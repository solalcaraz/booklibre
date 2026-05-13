package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.LibroColeccionable
import ar.edu.unsam.phm.domain.LibroColeccionableDocument
import ar.edu.unsam.phm.domain.LibroComun
import ar.edu.unsam.phm.domain.LibroComunDocument
import ar.edu.unsam.phm.domain.LibroDedicatoria
import ar.edu.unsam.phm.domain.LibroDedicatoriaDocument
import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.Reserva

data class ReservaLibroDTO(
    var id: String?,
    var titulo: String,
    var genero: String,
    var autor: String,
    var tipo: String,
    var isbn13: String,
    var idioma: String,
    var estado: String,
    var dueno: String,
    var calificacionPromedio: Double,
    var biblioKarmasTotal: Int,
    var portada: String
)

fun Reserva.toReservaLibroDTO(libro: LibroDocument): ReservaLibroDTO {
    val tipoDTO = when (libro) {
        is LibroComunDocument -> TipoDeLibroDTO.COMUN
        is LibroDedicatoriaDocument -> TipoDeLibroDTO.DEDICATORIA
        is LibroColeccionableDocument -> TipoDeLibroDTO.COLECCIONABLE
        else -> throw IllegalArgumentException("Tipo de libro desconocido")
    }

    return ReservaLibroDTO(
        id = libro.id,
        titulo = libro.titulo,
        genero = libro.genero.label,
        autor = libro.autor,
        tipo = tipoDTO.label,
        isbn13 = libro.isbn13,
        idioma = libro.idioma.label,
        estado = libro.estado.label,
        dueno = libro.dueno.nombreCompleto,
        calificacionPromedio = libro.promedioCalificacion,
        biblioKarmasTotal = this.calcularBiblioKarmas(libro),
        portada = libro.portada
    )
}
