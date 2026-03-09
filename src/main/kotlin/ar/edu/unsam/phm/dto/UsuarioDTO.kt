package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.TipoUsuario

class UsuarioDTO(
    val email: String,
    val password: String
)

class UsuarioRegister(
    val nombreCompleto: String,
    val email: String,
    val password: String
)

class UsuarioLogeadoDTO(
    val id: Int,
    val nombreCompleto: String
)

class UsuarioCompletoDTO(
    val nombreCompleto: String,
    val email: String,
    val descripcion: String,
    val telefono: String,
    val anoUnido: Int,
    val ciudad: String,
    val bibliokarmas: Int,
    val tipoUsuario: String,
)