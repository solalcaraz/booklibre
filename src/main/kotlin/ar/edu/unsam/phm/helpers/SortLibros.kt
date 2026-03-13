package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.dto.MiLibroDTO

fun ordenarLibros(libros: List<MiLibroDTO>, sort: String?): List<MiLibroDTO> {
    return when (sort) {
        "disponible,asc" -> libros.sortedBy { it.estadoReserva == "DISPONIBLE" }
        "disponible,desc" -> libros.sortedByDescending { it.estadoReserva == "DISPONIBLE" }

        "agregado,asc" -> libros.sortedBy { it.agregadoEl }
        "agregado,desc" -> libros.sortedByDescending { it.agregadoEl }

        else -> libros
    }
}
