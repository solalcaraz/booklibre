package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Reserva
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ReservaRepository : JpaRepository<Reserva, Int> {
    /* Cantidad de libros leídos de un usuario = todas sus reservas que ya finalizaron */
    @Query("""
        select count(r)
        from Reserva r
        where r.lector.id = :usuarioId
        and r.fechaHasta < current_date
    """)
    fun cantidadLeidosDe(@Param("usuarioId") usuarioId: Int): Int

    /* Cantidad de libros prestados de un usuario = todas las reservas de cuyo libro es dueño */
    fun countByLibroMongoIdIn(libroMongoIds: Collection<String>): Int

    @Query("""
        select r.libroMongoId as libroId,
        count(r) as reservas
        from Reserva r
        where r.libroMongoId in :libroIds
        group by r.libroMongoId
    """)
    fun contarReservasPorLibroMongoIdIn(@Param("libroIds") libroIds: Collection<String>): List<ReservasPorLibroProjection>

    @Query("""
        SELECT r
        FROM Reserva r
        WHERE r.libroResumen.duenoId = :usuarioId
        AND (
            :inputSearch = ''
            OR LOWER(r.libroResumen.titulo) LIKE LOWER(CONCAT('%', :inputSearch, '%'))
            OR LOWER(r.libroResumen.autor) LIKE LOWER(CONCAT('%', :inputSearch, '%'))
        )
    """)
    fun findPrestadosPorMi(
        @Param("usuarioId") usuarioId: Int,
        @Param("inputSearch") inputSearch: String,
        pageable: Pageable
    ): Page<Reserva>

    @Query("""
        SELECT r
        FROM Reserva r
        WHERE r.lector.id = :usuarioId
        AND (
            :inputSearch = ''
            OR LOWER(r.libroResumen.titulo) LIKE LOWER(CONCAT('%', :inputSearch, '%'))
            OR LOWER(r.libroResumen.autor) LIKE LOWER(CONCAT('%', :inputSearch, '%'))
        )
    """)
    fun findPrestadosAMi(
        @Param("usuarioId") usuarioId: Int,
        @Param("inputSearch") inputSearch: String,
        pageable: Pageable
    ): Page<Reserva>

    // Trae las reservas de un libro que aún no finalizaron (fechaHasta >= HOY)
    @Query("SELECT r FROM Reserva r WHERE r.libroMongoId = :libroId AND r.fechaHasta >= CURRENT_DATE")
    fun findReservasOcupadas(@Param("libroId") libroId: String): List<Reserva>

    // Verifica si hay reservas que se superponen con las fechas solicitadas
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r WHERE r.libroMongoId = :libroId AND r.fechaDesde <= :hasta AND r.fechaHasta >= :desde AND r.fechaHasta >= CURRENT_DATE")
    fun existReservaSuperpuesta(
        @Param("libroId") libroId: String,
        @Param("desde") desde: LocalDate,
        @Param("hasta") hasta: LocalDate
    ): Boolean

    fun findTop5ByOrderByFechaDesdeDesc(): List<Reserva>
}

interface ReservasPorLibroProjection {
    val libroId: String
    val reservas: Long
}
