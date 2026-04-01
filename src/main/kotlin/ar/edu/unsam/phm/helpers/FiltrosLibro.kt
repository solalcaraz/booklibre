package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
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

fun Libro.cumpleFiltros(filtros: FiltrosLibro): Boolean =
    (filtros.generos.isEmpty() || data.genero in filtros.generos) &&
    (filtros.cantPaginasMin?.let { data.cantPaginas >= it } ?: true) &&
    (filtros.cantPaginasMax?.let { data.cantPaginas <= it } ?: true) &&
    (filtros.isbn?.let { data.isbn13.contains(it) } ?: true) &&
    (filtros.usuarioPrestador?.let { data.dueno.nombreCompleto.contains(it, ignoreCase = true) } ?: true)


enum class Ordenamiento {
    TITULO_ASC,
    TITULO_DESC,
    AUTOR_ASC,
    DUENO_ASC;

    fun criterio(): Comparator<Libro> = when (this) {
        TITULO_ASC -> compareBy { it.data.titulo.lowercase() }
        TITULO_DESC -> compareByDescending { it.data.titulo.lowercase() }
        AUTOR_ASC -> compareBy { it.data.autor.lowercase() }
        DUENO_ASC -> compareBy { it.data.dueno.nombreCompleto.lowercase() }
    }
}