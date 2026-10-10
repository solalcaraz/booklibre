package ar.edu.unsam.phm.repository

//import ar.edu.unsam.phm.domain.AuditoriaPuntajeLibro
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

//@Repository
//interface AuditoriaPuntajeLibroRepository : JpaRepository<AuditoriaPuntajeLibro, Int> {
//
//    // Todas las actualizaciones de puntaje de un libro
//    @Query("SELECT a FROM AuditoriaPuntajeLibro a WHERE a.libroId = :libroId ORDER BY a.fechaActualizacion DESC")
//    fun findByLibroId(@Param("libroId") libroId: Int): List<AuditoriaPuntajeLibro>
//}