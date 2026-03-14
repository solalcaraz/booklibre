package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.exceptions.LibroException
import java.time.LocalDate

enum class TipoDeLibroDTO {
    COMUN,
    DEDICATORIA,
    COLECCIONABLE
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
    val descripcion: String,
    val portada: String
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
        descripcion = this.descripcion,
        portada = this.portada
    )
}

fun LibroDetalleDTO.toEntity(usuarioDueno: Usuario): Libro {
    val tipoDominio = when (this.tipo) {
        TipoDeLibroDTO.COMUN -> Comun
        TipoDeLibroDTO.DEDICATORIA -> Dedicatoria
        TipoDeLibroDTO.COLECCIONABLE -> Coleccionable
    }

    val nuevoLibro = Libro(
        id = this.id,
        titulo = this.titulo,
        tipo = tipoDominio,
        descripcion = this.descripcion,
        genero = this.genero,
        autor = this.autor,
        cantPaginas = this.paginas,
        isbn13 = this.isbn13,
        idioma = this.idioma,
        editorial = this.editorial,
        fechaPublicacion = LocalDate.parse(this.fechaPublicacion),
        estado = this.estado,
        dueno = usuarioDueno
    )
    nuevoLibro.portada = this.portada

    return nuevoLibro
}

data class MiLibroDTO(
    val id: Int?,
    val titulo: String,
    val autor: String,
    val estadoReserva: String,
    val genero: String,
    val agregadoEl: LocalDate,
    val portada: String
)

// libro dentro de prestamos
data class LibroResumen(
    val titulo: String,
    val autor: String,
    val puntuacion: Number,
    val imgUrl: String,
)
