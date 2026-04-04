package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId
import jakarta.persistence.*

@Entity
@Table(name = "resenias")
data class Resenia(
    @Column(name = "nombre_usuario", nullable = false)
    val nombreUsuario: String,

    @Column(nullable = false)
    val puntuacion: Int,

    @Column(nullable = false)
    val comentario: String,

    @OneToOne
    @JoinColumn(name = "id_reserva", nullable = false, unique = true)
    var reserva: Reserva,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null
): EntidadConId
