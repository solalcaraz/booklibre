package ar.edu.unsam.phm.dto

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
    val nombreCompleto: String,
    val imagen: String,
    val bibliokarmas: Int
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
    val imagen: String,
    val librosLeidos: Int,
    val librosPrestados: Int
)

class UsuarioPatch(
    val nombreCompleto: String,
    val descripcion: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val imagen: String,
    val tipoUsuario: String
)