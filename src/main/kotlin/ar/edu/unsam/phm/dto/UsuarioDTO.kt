package ar.edu.unsam.phm.dto

class LoginRequest(
    val email: String,
    val password: String
)

class RegisterRequest(
    val nombreCompleto: String,
    val email: String,
    val password: String
)

class LoginResponseDTO(
    val id: Int,
    val nombreCompleto: String,
    val imagen: String,
    val bibliokarmas: Int
)

class PerfilUsuarioDTO(
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

class UsuarioPatchRequest(
    val nombreCompleto: String,
    val descripcion: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val imagen: String,
    val tipoUsuario: String
)