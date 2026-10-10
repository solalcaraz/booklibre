package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Resenia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReseniaRepository : JpaRepository<Resenia, Int>{
    fun findTop2ByLibroMongoIdOrderByFechaDesc(libroMongoId: String): List<Resenia>

    @Query("select coalesce(avg(r.puntuacion), 0.0) from Resenia r where r.libroMongoId = :libroMongoId")
    fun promedioCalificacionDe(@Param("libroMongoId") libroMongoId: String): Double
}
