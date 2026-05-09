package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.query.Param

interface LibroDocumentRepository : MongoRepository<LibroDocument, String> {
    fun findByTitulo(titulo: String): LibroDocument?

    @Query("{ activo: true, titulo: {'\$regex': #{#valorABuscar}, '\$options': 'i'} }")
    fun getLibrosPrestables(@Param("valorABuscar") valorABuscar: String): Collection<LibroDocument>
}