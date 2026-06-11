package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("LibroPopular")
data class LibroPopular (
    @Id
    var libroId: String,

    var titulo: String,
    var genero: String,
    var autor: String,
    var tipo: String,
    var isbn13: String,
    var idioma: String,
    var estado: String,
    var dueno: String,
    var calificacionPromedio: Double,
    //var biblioKarmasTotal: Int,
    var portada: String,
)