package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId

/* Yo entiendo que el tipo de usuario es un strategy
Capaz me equivoco y el tipo es fijo y tenemos que usar Herencia simple
Vemos XD, aún no tiene comportamiento dice el enunciado */

interface TipoUsuario {

}

class Lector : TipoUsuario {

}

class Publicador : TipoUsuario {

}

class LectorPublicador : TipoUsuario {
    val lector = Lector()
    val publicador = Publicador()
}

class Usuario(
    override var id: Int? = null, /* el id es nullable porque lo asigna el repo */
    /* estos 3 campos son necesarios para el sign-up por eso al constructor */
    val nombreCompleto: String,
    val email: String,
    private var password: String
) : EntidadConId {
    var tipoUsuario: TipoUsuario = Lector()
    var descripcion: String = ""
    var ciudad: String? = null
    val celular: Int? = null

    fun validarPassword(passwordIngresada: String): Boolean {
        return this.password == passwordIngresada
    }
}