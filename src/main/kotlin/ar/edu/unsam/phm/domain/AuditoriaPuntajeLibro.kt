package ar.edu.unsam.phm.domain

import jakarta.annotation.PostConstruct
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.OffsetDateTime
import javax.sql.DataSource

@Entity
@Table(name = "auditoria_puntaje_libro")
class AuditoriaPuntajeLibro(
    @Column(name = "id_libro", nullable = false)
    val libroId: Int,

    @Column(name = "fecha_actualizacion", nullable = false)
    val fechaActualizacion: LocalDateTime = LocalDateTime.now(),

    @Column(name = "puntaje_anterior", nullable = false)
    val puntajeAnterior: Double,

    @Column(name = "puntaje_nuevo", nullable = false)
    val puntajeNuevo: Double,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)