package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.LibroColeccionableDocument
import ar.edu.unsam.phm.domain.LibroComunDocument
import ar.edu.unsam.phm.domain.LibroDedicatoriaDocument
import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.LibroPopular
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
private fun obtenerTipo(libro: LibroDocument): String =
    when (libro) {
        is LibroComunDocument -> TipoDeLibroDTO.COMUN.label
        is LibroDedicatoriaDocument -> TipoDeLibroDTO.DEDICATORIA.label
        is LibroColeccionableDocument -> TipoDeLibroDTO.COLECCIONABLE.label
        else -> throw IllegalArgumentException("Tipo de libro desconocido")
    }

fun Reserva.toReservaLibroDTO(libro: LibroDocument): ReservaLibroDTO =
    ReservaLibroDTO(
        id = libro.id,
        titulo = libro.titulo,
        genero = libro.genero.label,
        autor = libro.autor,
        tipo = obtenerTipo(libro),
        isbn13 = libro.isbn13,
        idioma = libro.idioma.label,
        estado = libro.estado.label,
        dueno = libro.dueno.nombreCompleto,
        calificacionPromedio = libro.promedioCalificacion,
        biblioKarmasTotal = this.calcularBiblioKarmas(libro),
        portada = libro.portada
    )

fun Reserva.toReservaDTODesdeRedis(libro: LibroPopular): ReservaLibroDTO =
    ReservaLibroDTO(
        id = libro.libroId,
        titulo = libro.titulo,
        genero = libro.genero,
        autor = libro.autor,
        tipo = libro.tipo,
        isbn13 = libro.isbn13,
        idioma = libro.idioma,
        estado = libro.estado,
        dueno = libro.dueno,
        calificacionPromedio = libro.calificacionPromedio,
        biblioKarmasTotal = this.calcularBiblioKarmas(libro),
        portada = libro.portada
    )
