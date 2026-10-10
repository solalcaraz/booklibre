package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.time.LocalDate

interface LibroDocumentRepository : MongoRepository<LibroDocument, String>, LibroDocumentRepositoryCustom {
    fun findByDuenoIdAndActivoTrue(duenoId: Int): List<LibroDocument>

    @Query("{ '_id': { '\$in': ?0 }, 'activo': true, 'reservasPendientes': { '\$not': { '\$elemMatch': { 'fechaInicio': { '\$lte': ?1 }, 'fechaFin': { '\$gte': ?2 } } } } }")
    fun findByIdInAndActivoTrueAndDisponible(ids: List<String>, desde: LocalDate, hasta: LocalDate): List<LibroDocument>

    fun findTop5ByOrderByCreatedAtDesc(): List<LibroDocument>
}
