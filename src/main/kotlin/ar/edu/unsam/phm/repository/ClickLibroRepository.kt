package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.ClickLibroDocument
import ar.edu.unsam.phm.dto.LibroMasClickeadoDTO
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Aggregation

interface ClickLibroRepository: MongoRepository<ClickLibroDocument, String> {
    // para consultar los clicks de los libros de un publicador
    fun findByLibroIdIn(libroIds: List<String>): List<ClickLibroDocument>

    @Aggregation(pipeline = [
        "{ '\$group': { '_id': { 'libroId': '\$libroId', 'libroTitulo': '\$libroTitulo' }, 'clicks': { '\$sum': 1 } } }",
        "{ '\$sort': { 'clicks': -1, '_id.libroTitulo': 1 } }",
        "{ '\$limit': 1 }",
        "{ '\$project': { '_id': 0, 'libroId': '\$_id.libroId', 'libroTitulo': '\$_id.libroTitulo', 'clicks': 1 } }"
    ])
    fun libroMasClickeado(): LibroMasClickeadoDTO?
}
