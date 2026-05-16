package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.ClickLibroDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface ClickLibroRepository: MongoRepository<ClickLibroDocument, String> {
    // para consultar los clicks de los libros de un publicador
    fun findByLibroIdIn(libroIds: List<String>): List<ClickLibroDocument>
}