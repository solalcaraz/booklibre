package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import java.util.Date

//LocalDate es moderno y solo trabaja con la fecha.
//Date incluye hora, lo cual no usamos.

data class DetalleLibroDTO(
    val id: Int?,
    val titulo: String,
    val tipo: String,
    val descripcion: String,
    val genero: String,
    val autor: String,
    val cantPaginas: Int,
    val isbn13: String,
    var idioma: String,
    val editorial: String,
    val fechaPublicacion: Date,
    val urlImagen: String,
    val calificacionPromedio: Double,
    //val ultimasResenias: List<ReseniaDTO>,
    //val rangosFechasOcupadas: List<RangoFechaDTO>,
    val estado: String,
    val biblioKarmasBase: Int
)

fun Libro.toDetalleDTO(biblioKarmasBase: Int) = DetalleLibroDTO(
    id = this.id,
    titulo = this.titulo,
    //Ver que onda esto, es reflexion para evitar usar when.
    tipo = this.tipo::class.simpleName?.uppercase()   // "COMUN", "COLECCIONABLE", "DEDICATORIA"
        ?: throw IllegalArgumentException("Tipo desconocido"),
    descripcion = this.descripcion,
    genero = this.genero.name,
    autor = this.autor,
    cantPaginas = this.cantPaginas,
    isbn13 = this.isbn13,
    idioma = this.idioma.name,
    editorial = this.editorial,
    fechaPublicacion = this.fechaPublicacion,
    urlImagen = this.getImagenUrl(),
    calificacionPromedio = this.calcularCalificacionPromedio(),
    estado = this.estado.name,
    biblioKarmasBase = biblioKarmasBase
)

/*
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
*/