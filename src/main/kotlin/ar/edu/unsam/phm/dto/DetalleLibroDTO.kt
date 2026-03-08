package ar.edu.unsam.phm.dto

import java.time.LocalDate
//LocalDate es moderno y solo trabaja con la fecha.
//Date incluye hora, lo cual no usamos.

//Estoy teniendo en cuenta que la reseña va a estar en el libro
data class DetalleLibroDTO(
    val id: Int,
    val titulo: String,
    val tipo: String,
    val descripcion: String,
    val genero: String,
    val autor: String,
    val cantPaginas: Int,
    val isbn13: String,
    var idioma: String,
    val editorial: String,
    val fechaPublicacion: LocalDate,
    val urlImagen: String,
    val puntaje: Double,
    val ultimasResenias: List<ReseniaDTO>,
    val rangosFechasOcupadas: List<RangoFechaDTO>,
    val estado: String,
    val plusBiblioKarms: Int
) {
}
data class ReseniaDTO(
    val id: Int,
    val nombreUsuario: String,
    val puntaje: Double,
    val comentario: String,
){
}

data class RangoFechaDTO(
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate
){}

