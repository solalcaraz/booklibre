package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import kotlin.math.ceil

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
    override fun plusBiblioKarma(cantidadBiblioKarma: Int): Int {
        return when(tipo) {
            TipoDeLibroDTO.COMUN.label ->
                if (cantidadBiblioKarma < 1000) cantPaginas * 5 else cantPaginas * 2

            TipoDeLibroDTO.DEDICATORIA.label ->
                200 + 10 * cantidadReservas

            TipoDeLibroDTO.COLECCIONABLE.label ->
                ceil(cantidadBiblioKarma / 5.0).toInt() + cantPaginas
            else -> 0
        }
    }
}