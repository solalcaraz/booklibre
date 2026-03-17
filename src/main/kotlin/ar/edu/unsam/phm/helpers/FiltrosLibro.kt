package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import java.time.LocalDate

class FiltrosLibro (
    val generos: List<Genero> = emptyList(),
    val cantPaginasMin: Int? = null,
    val cantPaginasMax: Int? = null,
    val fechaDesde: LocalDate = LocalDate.now(),
    val fechaHasta: LocalDate = LocalDate.now(),
    val isbn: String? = null,
    val usuarioPrestador: String? = null,
    val orden: Ordenamiento = Ordenamiento.POPULARIDAD,
    val busqueda: String? = null,
    val numeroPagina: Int = 0,
    val tamanoPaginado: Int = 6
) {
    // no puede ver libros anterior a hoy
    init {
        require(fechaDesde >= LocalDate.now()) { "La fecha de inicio no puede ser en el pasado" }
        require(!fechaHasta.isBefore(fechaDesde)) { "La fecha de fin no puede ser anterior a la de inicio" }
        require(fechaHasta >= fechaDesde) { "La fecha de fin no puede ser anterior a la de inicio" }
        require(cantPaginasMin == null || cantPaginasMax == null || cantPaginasMin <= cantPaginasMax) {
            "El rango de páginas es inválido"
        }
    }

    fun aplicarA(libro: Libro): Boolean =
            cumpleGenero(libro) &&
            cumpleCantPaginas(libro) &&
            cumpleIsbn(libro) &&
            cumpleUsuario(libro)

    private fun cumpleGenero(libro: Libro): Boolean =
        generos.isEmpty() || libro.genero in generos

    private fun cumpleCantPaginas(libro: Libro): Boolean =
        (cantPaginasMin?.let { libro.cantPaginas >= it } ?: true) &&
                (cantPaginasMax?.let { libro.cantPaginas <= it } ?: true)

    private fun cumpleIsbn(libro: Libro): Boolean =
        isbn?.let { libro.isbn13 == it } ?: true

    private fun cumpleUsuario(libro: Libro): Boolean =
        usuarioPrestador?.let {
            libro.dueno.nombreCompleto.contains(it, ignoreCase = true)
        } ?: true
}

enum class Ordenamiento {
    POPULARIDAD, // del mas reservado al menos
    TITULO_ASC,
    TITULO_DESC,
    AUTOR_ASC,
    DUENO_ASC
}