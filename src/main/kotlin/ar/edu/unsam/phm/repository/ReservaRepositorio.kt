package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import org.springframework.stereotype.Component

class ReservaSearchStrategy : SearchStrategy<Reserva> {
    override fun matches(obj: Reserva, value: String): Boolean {
        return obj.libro.titulo.contains(value, ignoreCase = true) ||
                obj.usuario.nombreCompleto.contains(value, ignoreCase = true) ||
                obj.libro.isbn13.contains(value, ignoreCase = true)
    }
}

@Component
class ReservaRepositorio : Repositorio<Reserva>(searchStrategy = ReservaSearchStrategy()) {
    /* Trae todas las reservas de un libro */
    fun buscarPorLibro(libro: Libro): List<Reserva> {
        return objetos.values.filter { it.libro == libro }
    }

    /* Trae la reserva activa del libro si hay */
    fun reservaActivaDeLibro(libro: Libro): Reserva? {
        return buscarPorLibro(libro).find { it.estaActiva() }
    }

    /* Devuelve true si el libro está prestado actualmente*/
    fun libroEstaPrestado(libro: Libro): Boolean {
        return reservaActivaDeLibro(libro) != null
    }
}