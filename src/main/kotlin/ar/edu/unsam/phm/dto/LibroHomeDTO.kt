package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.exceptions.LibroException

data class ReservaLibroHomeDTO(
    var id: Int?,
    var titulo: String,
    var genero: Genero,
    var autor: String,
    var tipo: TipoDeLibroDTO,
    var isbn13: String,
    var idioma: Idioma,
    var estado: Estado,
    var dueno: String,
    var calificacionPromedio: Double,
    var biblioKarmasTotal: Int,
    var portada: String
)

fun Reserva.toReservaLibroHomeDTO(cantidadReservas:Int): ReservaLibroHomeDTO {
    val tipoDTO = when (this.libro.tipo) {
        is Comun -> TipoDeLibroDTO.COMUN
        is Dedicatoria -> TipoDeLibroDTO.DEDICATORIA
        is Coleccionable -> TipoDeLibroDTO.COLECCIONABLE
        else -> throw LibroException.tipoInvalido(this.libro)
    }
    return ReservaLibroHomeDTO(
        id = this.libro.id!!,
        titulo = this.libro.titulo,
        genero = this.libro.genero,
        autor = this.libro.autor,
        tipo = tipoDTO,
        isbn13 = this.libro.isbn13,
        idioma = this.libro.idioma,
        estado = this.libro.estado,
        dueno = this.libro.dueno.nombreCompleto,
        calificacionPromedio = this.libro.calcularCalificacionPromedio(),
        biblioKarmasTotal = this.calcularBiblioKarmas(cantidadReservas),
        portada = this.libro.portada
    )
}