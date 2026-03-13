package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.repository.EntidadConId
import java.time.LocalDate

/* Yo entiendo que el tipo de usuario es un strategy
Capaz me equivoco y el tipo es fijo y tenemos que usar Herencia simple
Vemos XD, aún no tiene comportamiento dice el enunciado */

/*interface TipoUsuario {
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
}*/

enum class TipoUsuario(val label: String) {
    LECTOR("Lector"),
    PUBLICADOR("Publicador"),
    LECTOR_PUBLICADOR("Lector / Publicador"),
}

class Usuario(
    override var id: Int? = null, /* el id es nullable porque lo asigna el repo */
    /* estos 3 campos son necesarios para el sign-up por eso al constructor */
    var nombreCompleto: String,
    var email: String,
    private var password: String,
    val anoUnido: Int = LocalDate.now().year,
) : EntidadConId {
    var tipoUsuario: TipoUsuario = TipoUsuario.LECTOR_PUBLICADOR
    var descripcion: String = ""
    var ciudad: String = ""
    var telefono: String = ""
    var bibliokarmas: Int = 0
    var imagen: String = Environment.API_URL + "/imagenes/default_user.png"
    var librosLeidos: Int = 0
    var librosPrestados: Int = 0

    fun validarPassword(passwordIngresada: String): Boolean {
        return this.password == passwordIngresada
    }

    fun cambiarTipo(label: String){
        tipoUsuario = TipoUsuario.entries.find { it.label == label }
            ?: throw IllegalArgumentException("Tipo de usuario inválido: $label")
    }
}