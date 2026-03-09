package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import org.springframework.stereotype.Component

@Component
class LibroRepositorio : Repositorio<Libro>(LibroSearchStrategy()) {
    fun librosDe(usuario: Usuario): List<Libro> {
        return objetos.values.filter { it.dueno == usuario }
    }
}

class LibroSearchStrategy : SearchStrategy<Libro> {
    override fun matches(obj: Libro, value: String): Boolean {
        return obj.titulo.contains(value, ignoreCase = true)
    }
}
