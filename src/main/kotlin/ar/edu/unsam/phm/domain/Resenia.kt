package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId

data class Resenia(
    val libroId: Int,
    val nombreUsuario: String,
    val puntuacion: Int,
    val comentario: String,
    override var id: Int? = null
): EntidadConId {

}
