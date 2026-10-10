package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Genero
import jakarta.persistence.Embeddable
import java.time.LocalDate
import kotlin.math.ceil

// Cada tipo suma 5 bibliokarmas por día de reserva más este plus (reglas de la consigna)
enum class TipoDeLibroDTO(val label: String) {
    COMUN("Común") {
        override fun plusBiblioKarma(cantPaginas: Int, cantidadReservas: Int, bibliokarmasLector: Int) =
            if (bibliokarmasLector < 1000) cantPaginas * 5 else cantPaginas * 2
    },
    DEDICATORIA("Con dedicatoria") {
        override fun plusBiblioKarma(cantPaginas: Int, cantidadReservas: Int, bibliokarmasLector: Int) =
            200 + 10 * cantidadReservas
    },
    COLECCIONABLE("Coleccionable") {
        override fun plusBiblioKarma(cantPaginas: Int, cantidadReservas: Int, bibliokarmasLector: Int) =
            ceil(bibliokarmasLector / 5.0).toInt() + cantPaginas
    };

    abstract fun plusBiblioKarma(cantPaginas: Int, cantidadReservas: Int, bibliokarmasLector: Int): Int
}

data class LibroDetalleDTO(
    val id: String?,
    val titulo: String,
    val tipo: TipoDeLibroDTO,
    val genero: String,
    val autor: String,
    val idioma: String,
    val estado: String,
    val editorial: String,
    val fechaPublicacion: String,
    val paginas: Int,
    val isbn13: String,
    val descripcion: String,
    val portada: String,
    val duenoId: Int?,
    val duenoNombre: String,
    val duenoImagen: String,

    val promedioCalificacion: Double? = 0.0,
    val cantidadReservas: Int? = 0
)

data class MiLibroProjection(
    val id: String,
    val titulo: String,
    val autor: String,
    val genero: Genero,
    val estadoReserva: String,
    val agregadoEl: LocalDate,
    val portada: String,
    val clicks: Int = 0
)

data class LibroMasClickeadoDTO(
    val libroId: String,
    val libroTitulo: String,
    val clicks: Int
)

// Copia de los datos del libro que se guarda con la reserva, para listar préstamos sin ir a Mongo
@Embeddable
data class LibroResumen(
    val titulo: String = "",
    val autor: String = "",
    val imgUrl: String = "",
    val duenoNombre: String = "",
    val duenoId: Int?,
    var puntuacion: Double? = 0.0,
)
