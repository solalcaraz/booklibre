package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.Optional

interface LibroDocumentRepository : MongoRepository<LibroDocument, String> {
    fun findByTitulo(titulo: String): LibroDocument?

    @Query("{ activo: true, titulo: {'\$regex': #{#valorABuscar}, '\$options': 'i'} }")
    fun getLibrosPrestables(@Param("valorABuscar") valorABuscar: String): Collection<LibroDocument>

    @Query("{ activo: true, '\$or': [ { titulo: {'\$regex': ?0, '\$options': 'i'} }, { autor: {'\$regex': ?0, '\$options': 'i'} } ] }")
    fun findActivosByTituloOrAutor(valorABuscar: String): List<LibroDocument>

    @Query("{ activo: true, 'duenoId': ?0, '\$or': [ { titulo: {'\$regex': ?1, '\$options': 'i'} }, { autor: {'\$regex': ?1, '\$options': 'i'} } ] }")
    fun findActivosByDuenoIdAndTituloOrAutor(duenoId: Int, valorABuscar: String): List<LibroDocument>

    // Todos los libros activos de un dueño
    fun findByDuenoIdAndActivoTrue(duenoId: Int, pageable: Pageable): Page<LibroDocument>

    // Libros prestados: tienen al menos una reserva que solapa con "hoy"
    @Query("{ 'dueno.id': ?0, 'activo': true, 'reservasPendientes': { '\$elemMatch': { 'fechaInicio': { '\$lte': ?1 }, 'fechaFin': { '\$gte': ?1 } } } }")
    fun findPrestadosByDuenoId(duenoId: Int, hoy: LocalDate, pageable: Pageable): Page<LibroDocument>

    // Libros disponibles: NO tienen ninguna reserva que solape con "hoy"
    @Query("{ 'dueno.id': ?0, 'activo': true, 'reservasPendientes': { '\$not': { '\$elemMatch': { 'fechaInicio': { '\$lte': ?1 }, 'fechaFin': ?1 } } } }")
    fun findDisponiblesByDuenoId(duenoId: Int, hoy: LocalDate, pageable: Pageable): Page<LibroDocument>

    // REHACER --> No entiendo qué debería hacer el método para corregirlo
//    @EntityGraph(attributePaths = ["dueno"])
//    fun findConDuenoById(id:Int): Optional<Libro>

    // REHACER
//    @EntityGraph(attributePaths = ["dueno"])
//    fun findAll(spec: Specification<Libro>, pageable: Pageable): Page<Libro>
}
