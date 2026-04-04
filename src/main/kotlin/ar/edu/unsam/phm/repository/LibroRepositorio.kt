package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.helpers.cumpleFiltros
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface LibroRepository : JpaRepository<Libro, Int> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE libro SET tipo_libro = :nuevoTipo WHERE id = :id", nativeQuery = true)
    fun cambiarTipoDeLibro(id: Int, nuevoTipo: String)
}

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
        return objetos.values.filter { it.data.dueno.id == usuario.id }
    }
    /* Este es el contrato de implementación para cuando migremos a DB */
    /*fun librosDisponiblesDe(usuario: Usuario): List<Libro> {}
    fun librosPrestadosDe(usuario: Usuario): List<Libro> {}*/

    fun deleteById(libroId: Int) {
        objetos.remove(libroId)?: throw NotFoundException("No se encontró el objeto con ID $libroId.")
    }

    fun getLibrosDisponibles(filtros: FiltrosLibro, usuarioId: Int, idLibrosOcupados: Set<Int>): List<Libro> =
        // primero busca si hay una busqueda por teclado con el metodo search
        (if (filtros.busqueda.isNullOrBlank()) objetos.values.toList() else search(filtros.busqueda))
            // que no sean sus propios libros, ni que esten en la lista de libros reservados y que cumpla con los filtros
            .filter {  it.data.dueno.id != usuarioId && it.id !in idLibrosOcupados && it.cumpleFiltros(filtros) }
            .sortedWith(filtros.orden.criterio())
}