package ar.edu.unsam.phm.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "clicks_libros")
data class ClickLibroDocument(
    @Id
    val id: String? = null,
    val fechaHora: LocalDateTime = LocalDateTime.now(),

    val usuarioId: Int,
    val usuarioNombre: String,

    val libroId: String,
    val libroTitulo: String
)