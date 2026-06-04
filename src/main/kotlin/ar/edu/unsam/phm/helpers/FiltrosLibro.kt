package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Genero
import org.springframework.data.domain.Sort
import java.time.LocalDate

data class FiltrosLibro (
    val generos: List<Genero> = emptyList(),
    val cantPaginasMin: Int? = null,
    val cantPaginasMax: Int? = null,
    val fechaDesde: LocalDate = LocalDate.now(),
    val fechaHasta: LocalDate = LocalDate.now(),
    val isbn: String? = null,
    val usuarioPrestador: String? = null,
    val orden: Ordenamiento = Ordenamiento.TITULO_ASC,
    val busqueda: String? = null,
    val numeroPagina: Int = 0,
    val tamanoPaginado: Int = 6
) {
    // validaciones propias de este objetito
    init {
        require(fechaDesde >= LocalDate.now()) { "La fecha de inicio no puede ser en el pasado" }
        require(!fechaHasta.isBefore(fechaDesde)) { "La fecha de fin no puede ser anterior a la de inicio" }
        require(cantPaginasMin == null || cantPaginasMax == null || cantPaginasMin <= cantPaginasMax) {
            "El rango de páginas es inválido"
        }
    }
}

enum class Ordenamiento {
    TITULO_ASC,
    TITULO_DESC,
    AUTOR_ASC,
    DUENO_ASC,
    POPULARIDAD;

    fun toSort(): Sort = when (this) {
        TITULO_ASC -> Sort.by(Sort.Direction.ASC, "titulo")
        TITULO_DESC -> Sort.by(Sort.Direction.DESC, "titulo")
        AUTOR_ASC -> Sort.by(Sort.Direction.ASC, "autor")
        DUENO_ASC -> Sort.by(Sort.Direction.ASC, "dueno.nombreCompleto")
        POPULARIDAD -> Sort.unsorted()
    }
}