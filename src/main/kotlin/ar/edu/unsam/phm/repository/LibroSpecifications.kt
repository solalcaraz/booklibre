package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.LibroData
import ar.edu.unsam.phm.domain.Usuario
import jakarta.persistence.criteria.CriteriaBuilder
import org.springframework.data.jpa.domain.Specification

object LibroSpecifications {

    // helper
    private fun siempreTrue(cb: CriteriaBuilder) = cb.conjunction()

    fun noEsDelUsuario(usuarioId: Int): Specification<Libro> =
        Specification { root, _, cb ->
            cb.notEqual(root.get<Usuario>("dueno").get<Int>("id"), usuarioId)
        }

    fun noEstaOcupado(idLibrosOcupados: Set<Int>): Specification<Libro> =
        Specification { root, _, cb ->
            if (idLibrosOcupados.isEmpty()) siempreTrue(cb)
            else root.get<Int>("id").`in`(idLibrosOcupados).not()
        }

    fun conGeneros(generos: List<Genero>): Specification<Libro> =
        Specification { root, _, cb ->
            generos.takeIf { it.isNotEmpty() }?.let {
                root.get<LibroData>("data").get<Genero>("genero").`in`(generos)
            } ?: siempreTrue(cb)
        }

    fun conPaginasMin(min: Int?): Specification<Libro> =
        Specification { root, _, cb ->
            min?.let {
                cb.greaterThanOrEqualTo(root.get<LibroData>("data").get("cantPaginas"), it)
            } ?: siempreTrue(cb)
        }

    fun conPaginasMax(max: Int?): Specification<Libro> =
        Specification { root, _, cb ->
            max?.let {
                cb.lessThanOrEqualTo(root.get<LibroData>("data").get("cantPaginas"), it)
            } ?: siempreTrue(cb)
        }

    fun conIsbn(isbn: String?): Specification<Libro> =
        Specification { root, _, cb ->
            isbn?.takeIf { it.isNotBlank() }?.let {
                cb.like(root.get<LibroData>("data").get("isbn13"), "%$it%")
            } ?: cb.conjunction()
        }

    fun conUsuarioPrestador(nombre: String?): Specification<Libro> =
        Specification { root, _, cb ->
            nombre?.takeIf { it.isNotBlank() }?.let {
                cb.like(
                    cb.lower(root.get<Usuario>("dueno").get("nombreCompleto")),
                    "%${nombre.lowercase()}%"
                )
            } ?: siempreTrue(cb)
        }

    fun conBusqueda(busqueda: String?): Specification<Libro> =
        Specification { root, _, cb ->
            busqueda?.takeIf { it.isNotBlank() }?.let {
                val b = "%${busqueda.trim().lowercase()}%"
                val data = root.get<LibroData>("data")
                cb.or(
                    cb.like(cb.lower(data.get("titulo")), b),
                    cb.like(cb.lower(data.get("autor")), b)
                )
            } ?: siempreTrue(cb)
        }
}