package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "resenias")
data class Resenia(
    @Column(name = "nombre_usuario", nullable = false)
    val nombreUsuario: String,

    @Column(nullable = false)
    val puntuacion: Int,

    @Column(nullable = false)
    val comentario: String,

    @Column(name = "id_libro")
    val libroMongoId: String?,

    @Column(nullable = false)
    val fecha: Date = Date(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null
): EntidadConId {

}
