package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Genero
import java.time.LocalDate

enum class TipoDeLibroDTO(val label: String) {
    COMUN("Común"),
    DEDICATORIA("Con dedicatoria"),
    COLECCIONABLE("Coleccionable")
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
    val portada: String

    // Acá podría traer también y que Denu use el mismo DTO
    //val promedioCalificacion: Double? = 0.0
)

// libro dentro de prestamos
data class LibroResumen(
    val titulo: String,
    val autor: String,
    val promedioCalificacion: Double?,
    val portada: String,
)
