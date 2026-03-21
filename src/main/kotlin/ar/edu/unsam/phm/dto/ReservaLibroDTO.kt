package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.LibroColeccionable
import ar.edu.unsam.phm.domain.LibroComun
import ar.edu.unsam.phm.domain.LibroDedicatoria
import ar.edu.unsam.phm.domain.Reserva

data class ReservaLibroDTO(
    var id: Int?,
    var titulo: String,
    var genero: String,
    var autor: String,
    var tipo: TipoDeLibroDTO,
    var isbn13: String,
    var idioma: String,
    var estado: String,
    var dueno: String,
    var calificacionPromedio: Double,
    var biblioKarmasTotal: Int,
    var portada: String
)

fun Reserva.toReservaLibroDTO(cantidadReservas:Int, puntuaciones: List<Int>): ReservaLibroDTO {
    val tipoDTO = when (this.libro) {
        is LibroComun -> TipoDeLibroDTO.COMUN
        is LibroDedicatoria -> TipoDeLibroDTO.DEDICATORIA
        is LibroColeccionable -> TipoDeLibroDTO.COLECCIONABLE
        else -> TipoDeLibroDTO.COMUN //por default es comun
    }
    val libro = this.libro.data //armo un pequeño objeto libro.data para no tener tantos llamados

    return ReservaLibroDTO(
        id = this.libro.id!!,
        titulo = libro.titulo,
        genero = libro.genero.label,
        autor = libro.autor,
        tipo = tipoDTO,
        isbn13 = libro.isbn13,
        idioma = libro.idioma.label,
        estado = libro.estado.label,
        dueno = libro.dueno.nombreCompleto,
        calificacionPromedio = this.libro.calcularCalificacionPromedio(puntuaciones),
        biblioKarmasTotal = this.calcularBiblioKarmas(cantidadReservas),
        portada = libro.portada
    )
}