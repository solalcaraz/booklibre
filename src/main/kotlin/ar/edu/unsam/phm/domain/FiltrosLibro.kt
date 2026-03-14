package ar.edu.unsam.phm.domain

import java.time.LocalDate

class FiltrosLibro (
    val generos: List<Genero> = emptyList(),
    val cantPaginasMin: Int? = null,
    val cantPaginasMax: Int? = null,
    val fechaDesde: LocalDate = LocalDate.now(),
    val fechaHasta: LocalDate = LocalDate.now(),
    val isbn: String? = null,
    val usuarioPrestador: String? = null,
    val orden: Ordenamiento = Ordenamiento.RELEVANCIA
) {
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
    RELEVANCIA,
    TITULO_ASC,
    TITULO_DESC,
    FECHA_ASC,
    FECHA_DESC,
    POPULARIDAD // del mas reservado al menos
}