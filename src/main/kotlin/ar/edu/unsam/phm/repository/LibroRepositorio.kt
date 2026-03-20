package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.exceptions.NotFoundException
import org.springframework.stereotype.Component

class LibroSearchStrategy : SearchStrategy<Libro> {
    override fun matches(obj: Libro, value: String): Boolean {
        val busqueda = value.trim().lowercase()
        val data = obj.data
        return data.titulo.contains(value, ignoreCase = true) ||
                data.autor.split(" ").any { palabra ->
                    palabra.lowercase().startsWith(busqueda)
                }
    }
}

@Component
class LibroRepositorio : Repositorio<Libro>(LibroSearchStrategy()) {

    fun librosDe(usuario: Usuario): List<Libro> {
        return objetos.values.filter { it.data.dueno == usuario }
    }
    /* Este es el contrato de implementación para cuando migremos a DB */
    /*fun librosDisponiblesDe(usuario: Usuario): List<Libro> {}
    fun librosPrestadosDe(usuario: Usuario): List<Libro> {}*/

    fun deleteById(libroId: Int) {
        objetos.remove(libroId)?: throw NotFoundException("No se encontró el objeto con ID $libroId.")
    }
}