package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import org.springframework.stereotype.Component

@Component
class ReservaRepositorio : Repositorio<Reserva>(searchStrategy = ReservaSearchStrategy()) {

    fun buscarPorLibro(libro: Libro): List<Reserva> {
        return objetos.values.filter { it.libro == libro }
    }
}