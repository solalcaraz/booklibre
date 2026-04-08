package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.repository.EntidadConId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import jakarta.persistence.Transient

/* ¿Qué se supone que tiene que hacer cada tipo de usuario? Consultar, viene de la mano con JWT */

/* interface TipoUsuario {
    fun nombre(): String
}

class Lector : TipoUsuario {
    override fun nombre() = "Lector"
}

class Publicador : TipoUsuario {
    override fun nombre() = "Publicador"
}

class LectorPublicador : TipoUsuario {
    override fun nombre() = "Lector / Publicador"
} */

enum class TipoUsuario(val label: String) {
    LECTOR("Lector"),
    PUBLICADOR("Publicador"),
    LECTOR_PUBLICADOR("Lector / Publicador"),
}

@Entity
@Table(name = "usuarios")
class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null, /* el id es nullable porque lo asigna el repo */

    @Column(nullable = false)
    var nombreCompleto: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    private var password: String,

    @Column(nullable = false)
    val anoUnido: Int = LocalDate.now().year,
) : EntidadConId {
    @Column(nullable = false)
    var tipoUsuario: TipoUsuario = TipoUsuario.LECTOR_PUBLICADOR

    @Column
    var descripcion: String = ""

    @Column
    var ciudad: String = ""

    @Column
    var telefono: String = ""

    @Column(nullable = false)
    var bibliokarmas: Int = 0

    @Column
    var imagen: String = Environment.API_URL + "/imagenes/default_user.png"

    @Transient
    var librosLeidos: Int = 0
    @Transient
    var librosPrestados: Int = 0

    fun validarPassword(passwordIngresada: String): Boolean {
        return this.password == passwordIngresada
    }

    fun cambiarTipo(label: String){
        tipoUsuario = TipoUsuario.entries.find { it.label == label }
            ?: throw IllegalArgumentException("Tipo de usuario inválido: $label")
    }
    fun aumentarBiblioKarmas(bibliokarmas: Int){
        this.bibliokarmas += bibliokarmas
    }
}