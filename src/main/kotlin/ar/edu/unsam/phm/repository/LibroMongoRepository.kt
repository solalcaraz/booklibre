package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroMongo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.query.Param

interface LibroMongoRepository : MongoRepository<LibroMongo, String> {
    fun findByTitulo(titulo: String): LibroMongo?

    @Query("{ activo: true, titulo: {'\$regex': #{#valorABuscar}, '\$options': 'i'} }")
    fun getLibrosPrestables(@Param("valorABuscar") valorABuscar: String): Collection<LibroMongo>
}