package ar.edu.unsam.phm.exceptions

import ar.edu.unsam.phm.domain.Libro

sealed class LibroException(mensaje: String) : Exception(mensaje) {
    class generalException(mensaje: String) : LibroException(mensaje)

    class tipoInvalido(libro: Libro) : LibroException("El tipo '${libro.tipo}' es desconocido")
}