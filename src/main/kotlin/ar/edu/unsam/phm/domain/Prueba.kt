package ar.edu.unsam.phm.domain

import jakarta.persistence.*

@Entity
@Table(name = "prueba")
class PruebaUsuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var nombre: String = "",

    @Column(nullable = false, unique = true)
    var email: String = ""
)
