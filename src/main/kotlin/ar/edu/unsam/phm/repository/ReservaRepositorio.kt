package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Reserva
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ReservaRepository : JpaRepository<Reserva, Int> {
    /*@Modifying
    @Query("delete from Reserva r where r.libro.id = :libroId")
    fun deleteByLibroId(@Param("libroId") libroId: Int): Int*/

    /* Cantidad de libros leídos de un usuario = todas sus reservas que ya finalizaron */
    @Query("""
        select count(r)
        from Reserva r
        where r.lector.id = :usuarioId
        and r.fechaHasta < current_date
    """)
    fun cantidadLeidosDe(@Param("usuarioId") usuarioId: Int): Int

    /* Cantidad de libros prestados de un usuario = todas las reservas de cuyo libro es dueño */
    @Query("""
        select count(r)
        from Reserva r
        where r.libro.dueno.id = :usuarioId
    """)
    fun cantidadPrestadosDe(@Param("usuarioId") usuarioId: Int): Int

    @Query("SELECT r FROM Reserva r WHERE r.libroMongoId IN :libroMongoIds AND r.libro.dueno.id = :usuarioId")
    fun findPrestadosPorMiConLibrosMongo(
        @Param("usuarioId") usuarioId: Int,
        @Param("libroMongoIds") libroMongoIds: Collection<String>,
        pageable: Pageable
    ): Page<Reserva>

    @Query("SELECT r FROM Reserva r WHERE r.lector.id = :usuarioId AND r.libroMongoId IN :libroMongoIds")
    fun findPrestadosAMiConLibrosMongo(
        @Param("usuarioId") usuarioId: Int,
        @Param("libroMongoIds") libroMongoIds: Collection<String>,
        pageable: Pageable
    ): Page<Reserva>

    fun countByLibroId(libroId: String): Int

    // Trae solo las puntuaciones de las reseñas de un libro específico
//    @Query("SELECT r.resenia.puntuacion FROM Reserva r WHERE r.libro.id = :libroId AND r.resenia IS NOT NULL")
//    fun findPuntuacionesByLibroId(@Param("libroId") libroId: Int): List<Int>

    //Ultimas dos reservas
//    @Query("SELECT r FROM Reserva r WHERE r.libro.id = :libroId AND r.fechaHasta < :hoy AND r.resenia IS NOT NULL ORDER BY r.fechaHasta DESC")
//    fun findTop2UltimasReservas(
//        @Param("libroId") libroId: Int,
//        @Param("hoy") hoy: LocalDate = LocalDate.now()
//    ): List<Reserva>

    // Trae las reservas de un libro que aún no finalizaron (fechaHasta >= HOY)
    @Query("SELECT r FROM Reserva r WHERE r.libro.id = :libroId AND r.fechaHasta >= CURRENT_DATE")
    fun findReservasOcupadas(@Param("libroId") libroId: String): List<Reserva>

    // Verifica si hay reservas que se superponen con las fechas solicitadas
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r WHERE r.libro.id = :libroId AND r.fechaDesde <= :hasta AND r.fechaHasta >= :desde AND r.fechaHasta >= CURRENT_DATE")
    fun existReservaSuperpuesta(
        @Param("libroId") libroId: String,
        @Param("desde") desde: LocalDate,
        @Param("hasta") hasta: LocalDate
    ): Boolean

    // Verifica si hay reservas que se superponen con las fechas solicitadas
    // Una superposición ocurre si: reserva.fechaDesde <= hasta_solicitado AND reserva.fechaHasta >= desde_solicitado
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r WHERE r.libro.id = :libroId AND r.fechaDesde <= :hasta AND r.fechaHasta >= :desde AND r.fechaHasta >= CURRENT_DATE")
    fun existsReservaSuperpuesta(
        @Param("libroId") libroId: String,
        @Param("desde") desde: LocalDate,
        @Param("hasta") hasta: LocalDate
    ): Boolean
}
