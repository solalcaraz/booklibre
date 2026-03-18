package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.exceptions.NotFoundException
import org.springframework.stereotype.Component

class LibroSearchStrategy : SearchStrategy<Libro> {
    override fun matches(obj: Libro, value: String): Boolean {
        return obj.titulo.contains(value, ignoreCase = true) ||
                obj.autor.contains(value, ignoreCase = true)
    }
}

@Component
class LibroRepositorio : Repositorio<Libro>(LibroSearchStrategy()) {

    fun librosDe(usuario: Usuario): List<Libro> {
        return objetos.values.filter { it.dueno == usuario }
    }

    fun deleteById(libroId: Int) {
        objetos.remove(libroId)?: throw NotFoundException("No se encontró el objeto con ID $libroId.")
    }
}