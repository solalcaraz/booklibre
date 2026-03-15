package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import org.springframework.stereotype.Component
import java.time.LocalDate

class ReservaSearchStrategy : SearchStrategy<Reserva> {
    override fun matches(obj: Reserva, value: String): Boolean {
        return obj.libro.titulo.contains(value, ignoreCase = true) ||
                obj.lector.nombreCompleto.contains(value, ignoreCase = true) ||
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

    /* Los libros leídos de un usuario son todas sus reservas que ya finalizaron */
    fun librosLeidosDe(usuario: Usuario): Int {
        return objetos.values
            .filter { it.lector.id == usuario.id }
            .count { it.estaFinalizada() }
    }

    /* Los libros prestados de un usuario son todas las reservas de cuyo libro es dueño */
    fun librosPrestadosDe(usuario: Usuario): Int {
        return objetos.values
            .count { it.libro.dueno.id == usuario.id }
    }

    // Los libros disponibles en un rango de fechas
    fun libroEstaDisponibleEntre(libro: Libro, desde: LocalDate, hasta: LocalDate): Boolean =
        buscarPorLibro(libro).none { it.seSuperponeCon(desde, hasta)
        }

    fun getPuntacionPromedio(libro:Libro): Double{
        val resenias = buscarPorLibro(libro).mapNotNull { it.resenia }
        if( resenias.isEmpty()) return 0.0
        return resenias.sumOf { it.puntuacion } / resenias.size.toDouble()
    }

    fun getUltimasReservas(libro:Libro): List<Reserva>{
        return buscarPorLibro(libro)
            .filter { it.estaFinalizada() }
            .filter { it.resenia != null }
            .sortedByDescending { it.resenia!!.fechaCreacion }
            .take(2)
    }

    fun getReservasOcupadas(libro:Libro): List<Reserva>{
        return buscarPorLibro(libro)
            .filter { !it.estaFinalizada() }
    }
}
