package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("LibroPopular", timeToLive = 30)
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
    override val cantPaginas: Int,
    override val cantidadReservas: Int,
    var portada: String,
) : LibroCalculoBibliokarmas {
    // En Redis el tipo se guarda como label; si no coincide con ninguno no suma plus
    override fun plusBiblioKarma(cantidadBiblioKarma: Int): Int =
        TipoDeLibroDTO.entries.find { it.label == tipo }
            ?.plusBiblioKarma(cantPaginas, cantidadReservas, cantidadBiblioKarma)
            ?: 0
}