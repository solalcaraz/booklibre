package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import org.springframework.stereotype.Component

@Component
class LibroRepositorio : Repositorio<Libro>(LibroSearchStrategy()) {
}

class LibroSearchStrategy : SearchStrategy<Libro> {
    override fun matches(obj: Libro, value: String): Boolean {
        return obj.titulo.contains(value, ignoreCase = true)
    }
}
