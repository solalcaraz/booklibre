package ar.edu.unsam.phm.dto

class UsuarioDTO(
    val email: String,
    val password: String
)

class UsuarioLogeadoDTO(
    val id: Int,
    val email: String,
    val nombreCompleto: String
)