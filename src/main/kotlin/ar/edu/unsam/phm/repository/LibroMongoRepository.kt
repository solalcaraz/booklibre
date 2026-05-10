package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroMongo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.query.Param

interface LibroMongoRepository : MongoRepository<LibroMongo, String> {
    fun findByTitulo(titulo: String): LibroMongo?

    @Query("{ activo: true, titulo: {'\$regex': #{#valorABuscar}, '\$options': 'i'} }")
    fun getLibrosPrestables(@Param("valorABuscar") valorABuscar: String): Collection<LibroMongo>

    @Query("{ activo: true, '\$or': [ { titulo: {'\$regex': ?0, '\$options': 'i'} }, { autor: {'\$regex': ?0, '\$options': 'i'} } ] }")
    fun findActivosByTituloOrAutor(valorABuscar: String): List<LibroMongo>

    @Query("{ activo: true, 'dueno.id': ?0, '\$or': [ { titulo: {'\$regex': ?1, '\$options': 'i'} }, { autor: {'\$regex': ?1, '\$options': 'i'} } ] }")
    fun findActivosByDuenoIdAndTituloOrAutor(duenoId: Int, valorABuscar: String): List<LibroMongo>
}
