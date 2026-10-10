package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.dto.dashboard.PromedioCalificacionPorTipoMongo
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

// Repo custom con MongoTemplate porque los filtros del home son opcionales y se combinan en tiempo de ejecución
interface LibroDocumentRepositoryCustom {
    fun findDisponiblesParaReserva(excludeUsuarioId: Int, filtros: FiltrosLibro): Page<LibroDocument>
    fun findMisLibrosDe(duenoId: Int, hoy: LocalDate, filter: String, sort: String?, pageable: Pageable): Page<LibroDocument>
    fun promedioCalificacionPorTipo(): List<PromedioCalificacionPorTipoMongo>
}
@Repository
class LibroDocumentRepositoryCustomImpl( private val mongoTemplate: MongoTemplate): LibroDocumentRepositoryCustom {
    private fun buildCriteria(excludeUsuarioId: Int, filtros: FiltrosLibro): Criteria {
        val criteria = Criteria.where("activo").`is`(true)

        criteria.and("dueno._id").ne(excludeUsuarioId)

        // Disponible = ninguna reserva pendiente se superpone con el rango pedido
        criteria.and("reservasPendientes").not().elemMatch(
            Criteria.where("fechaInicio").lte(filtros.fechaHasta)
                .and("fechaFin").gte(filtros.fechaDesde)
        )

        if (filtros.generos.isNotEmpty()) { criteria.and("genero").`in`(filtros.generos) }

        val rangoPaginas = mutableListOf<Criteria>()
        filtros.cantPaginasMin?.let { rangoPaginas.add(Criteria.where("cantPaginas").gte(it)) }
        filtros.cantPaginasMax?.let { rangoPaginas.add(Criteria.where("cantPaginas").lte(it)) }
        if (rangoPaginas.isNotEmpty()) { criteria.andOperator(*rangoPaginas.toTypedArray()) }

        filtros.isbn?.takeIf { it.isNotBlank() }?.let { criteria.and("isbn13").regex(it, "i") }
        filtros.usuarioPrestador?.takeIf { it.isNotBlank() }?.let { criteria.and("dueno.nombreCompleto").regex(it, "i") }

        // Pattern.quote para que lo que escribe el usuario no se interprete como regex
        filtros.busqueda?.takeIf { it.isNotBlank() }?.let { busqueda ->
            val regex = Pattern.compile(Pattern.quote(busqueda.trim()), Pattern.CASE_INSENSITIVE)
            criteria.orOperator(
                Criteria.where("titulo").regex(regex),
                Criteria.where("autor").regex(regex)
            )
        }
        return criteria
    }
    override fun findDisponiblesParaReserva(excludeUsuarioId: Int, filtros: FiltrosLibro): Page<LibroDocument> {
        val criteria = buildCriteria(excludeUsuarioId, filtros)
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

        // Ordenar por disponibilidad exige calcularla en la base: se agrega un campo
        // estadoReservaOrden (1 = prestado hoy, 0 = disponible) antes de ordenar y paginar
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
            "\$addFields",
            Document("estadoReservaOrden", estadoReservaOrdenExpression(hoy))
        )
    )

    private fun estadoReservaOrdenExpression(hoy: LocalDate) =
        Document("\$cond", listOf(tieneReservaActivaExpression(hoy), 1, 0))

    private fun tieneReservaActivaExpression(hoy: LocalDate) =
        Document("\$gt", listOf(cantidadReservasActivasExpression(hoy), 0))

    private fun cantidadReservasActivasExpression(hoy: LocalDate) =
        Document("\$size", reservasActivasExpression(hoy))

    private fun reservasActivasExpression(hoy: LocalDate) =
        Document(
            "\$filter",
            Document("input", "\$reservasPendientes")
                .append("as", "reserva")
                .append("cond", reservaActivaExpression(hoy))
        )

    private fun reservaActivaExpression(hoy: LocalDate) =
        Document(
            "\$and", listOf(
                Document("\$lte", listOf("\$\$reserva.fechaInicio", hoy)),
                Document("\$gte", listOf("\$\$reserva.fechaFin", hoy))
            )
        )

    private fun sortMisLibros(sort: String?): Sort =
        when (sort) {
            "disponible,asc" -> Sort.by(Sort.Direction.ASC, "estadoReservaOrden").and(Sort.by("titulo"))
            "disponible,desc" -> Sort.by(Sort.Direction.DESC, "estadoReservaOrden").and(Sort.by("titulo"))
            "agregado,asc" -> Sort.by(Sort.Direction.ASC, "fechaPublicacion").and(Sort.by("titulo"))
            "agregado,desc" -> Sort.by(Sort.Direction.DESC, "fechaPublicacion").and(Sort.by("titulo"))
            "titulo,asc" -> Sort.by(Sort.Direction.ASC, "titulo")
            "titulo,desc" -> Sort.by(Sort.Direction.DESC, "titulo")
            else -> Sort.by(Sort.Direction.ASC, "_id")
        }

    // Promedio de promedios por tipo; los libros sin reseñas (promedio 0) no cuentan
    override fun promedioCalificacionPorTipo(): List<PromedioCalificacionPorTipoMongo> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(
                Criteria.where("activo").`is`(true)
                    .and("calificacionPromedio").gt(0.0)
            ),
            Aggregation.group("_class")
                .avg("calificacionPromedio").`as`("puntajePromedio"),
            Aggregation.project("puntajePromedio")
                .and("_id").`as`("tipo")
        )

        return mongoTemplate
            .aggregate(aggregation, "libros", PromedioCalificacionPorTipoMongo::class.java)
            .mappedResults
    }
}
