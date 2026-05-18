package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.helpers.FiltrosLibro
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import org.bson.Document
import java.time.LocalDate
import java.util.regex.Pattern

// Implementacion de un repo custom con MongoTemplate para poder aplicar a la busqueda de libros los filstros que son optativos
interface LibroDocumentRepositoryCustom {
    fun findDisponiblesParaReserva( excludeUsuarioId: Int, filtros: FiltrosLibro): Page<LibroDocument>
    fun findMisLibrosDe(duenoId: Int, hoy: LocalDate, filter: String, sort: String?, pageable: Pageable): Page<LibroDocument>
}
@Repository
class LibroDocumentRepositoryCustomImpl( private val mongoTemplate: MongoTemplate): LibroDocumentRepositoryCustom {
    override fun findDisponiblesParaReserva(excludeUsuarioId: Int, filtros: FiltrosLibro): Page<LibroDocument> {
        val criteria = Criteria.where("activo").`is`(true)

        // excluir libros del propio user
        criteria.and("dueno._id").ne(excludeUsuarioId)

        // disponible: ninguna reserva pendiente que se superponga con el rango del filter
        criteria.and("reservasPendientes").not().elemMatch(
            Criteria.where("fechaInicio").lte(filtros.fechaHasta)
                .and("fechaFin").gte(filtros.fechaDesde)
        )

        // filtro de los generos
        if(filtros.generos.isNotEmpty()){ criteria.and("genero").`in`(filtros.generos) }

        // filtro por el rango de paginas
        val rangoPaginas = mutableListOf<Criteria>()
        filtros.cantPaginasMin?.let { rangoPaginas.add(Criteria.where("cantPaginas").gte(it)) }
        filtros.cantPaginasMax?.let { rangoPaginas.add(Criteria.where("cantPaginas").lte(it)) }
        if (rangoPaginas.isNotEmpty()) { criteria.andOperator(*rangoPaginas.toTypedArray()) }

        // filtro del ISBN y el nombre del dueño del libro
        filtros.isbn?.takeIf { it.isNotBlank() }?.let { criteria.and("isbn13").regex(it, "i") }
        filtros.usuarioPrestador?.takeIf { it.isNotBlank() }?.let { criteria.and("dueno.nombreCompleto").regex(it, "d") }

        // filtro por busqueda: por titulo o por autor
        filtros.busqueda?.takeIf { it.isNotBlank() }?.let { busqueda ->
            val regex = Pattern.compile(Pattern.quote(busqueda.trim()), Pattern.CASE_INSENSITIVE)
            criteria.orOperator(
                Criteria.where("titulo").regex(regex),
                Criteria.where("autor").regex(regex)
            )
        }

        // paginado, total y libros dispo
        val pageable: Pageable = PageRequest.of(
            filtros.numeroPagina, filtros.tamanoPaginado, filtros.orden.toSort()
        )

        val queryPaginada = Query(criteria).with(pageable)

        val queryTotalPaginas = Query(criteria)

        val total = mongoTemplate.count(queryTotalPaginas, LibroDocument::class.java)
        val libros = mongoTemplate.find(queryPaginada, LibroDocument::class.java)

        return PageImpl(libros, pageable, total)
    }

    override fun findMisLibrosDe(
        duenoId: Int,
        hoy: LocalDate,
        filter: String,
        sort: String?,
        pageable: Pageable
    ): Page<LibroDocument> {
        val criteria = Criteria.where("dueno._id").`is`(duenoId)
            .and("activo").`is`(true)

        when (filter) {
            "Disponibles" -> criteria.and("reservasPendientes").not().elemMatch(reservaActivaCriteria(hoy))
            "Prestados" -> criteria.and("reservasPendientes").elemMatch(reservaActivaCriteria(hoy))
        }

        val total = mongoTemplate.count(Query(criteria), LibroDocument::class.java)

        val aggregation = Aggregation.newAggregation(
            Aggregation.match(criteria),
            estadoReservaOrdenOperation(hoy),
            Aggregation.sort(sortMisLibros(sort)),
            Aggregation.skip(pageable.offset),
            Aggregation.limit(pageable.pageSize.toLong())
        )

        val libros = mongoTemplate
            .aggregate(aggregation, "libros", LibroDocument::class.java)
            .mappedResults

        return PageImpl(libros, pageable, total)
    }

    private fun reservaActivaCriteria(hoy: LocalDate): Criteria =
        Criteria.where("fechaInicio").lte(hoy).and("fechaFin").gte(hoy)

    private fun estadoReservaOrdenOperation(hoy: LocalDate) = Aggregation.stage(
        Document(
            "\$addFields", Document(
                "estadoReservaOrden", Document(
                    "\$cond", listOf(
                        Document(
                            "\$gt", listOf(
                                Document(
                                    "\$size", Document(
                                        "\$filter", Document("input", "\$reservasPendientes")
                                            .append("as", "reserva")
                                            .append(
                                                "cond", Document(
                                                    "\$and", listOf(
                                                        Document("\$lte", listOf("\$\$reserva.fechaInicio", hoy)),
                                                        Document("\$gte", listOf("\$\$reserva.fechaFin", hoy))
                                                    )
                                                )
                                            )
                                    )
                                ),
                                0
                            )
                        ),
                        1,
                        0
                    )
                )
            )
        )
    )

    private fun sortMisLibros(sort: String?): Sort =
        when (sort) {
            "disponible,asc" -> Sort.by(Sort.Direction.ASC, "estadoReservaOrden").and(Sort.by("titulo"))
            "disponible,desc" -> Sort.by(Sort.Direction.DESC, "estadoReservaOrden").and(Sort.by("titulo"))
            "agregado,asc" -> Sort.by(Sort.Direction.ASC, "fechaPublicacion").and(Sort.by("titulo"))
            "agregado,desc" -> Sort.by(Sort.Direction.DESC, "fechaPublicacion").and(Sort.by("titulo"))
            else -> Sort.by(Sort.Direction.ASC, "_id")
        }
}
