package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.exceptions.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface LibroRepository : JpaRepository<Libro, Int>, JpaSpecificationExecutor<Libro> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE libro SET tipo_libro = :nuevoTipo WHERE id = :id", nativeQuery = true)
    fun cambiarTipoDeLibro(id: Int, nuevoTipo: String)
}

//TODO: borrar cuando se cambien las otras funciones del repo
class LibroSearchStrategy : SearchStrategy<Libro> {
    override fun matches(obj: Libro, value: String): Boolean = true
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
}