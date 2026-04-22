package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.LoginRequest
import ar.edu.unsam.phm.dto.LoginResponseDTO
import ar.edu.unsam.phm.dto.RegisterRequest
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.service.AuthService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
class AuthController(
    val authService: AuthService,
    val usuarioMapper: UsuarioMapper
) {
    @PostMapping("/login")
    fun login(@RequestBody usuario: LoginRequest): AuthenticationResponse {
        return authService.login(usuario)
    }

    @PostMapping("/register")
    fun register(@RequestBody usuarioRegister: RegisterRequest): LoginResponseDTO {
        val usuario = authService.register(usuarioRegister)
        return usuarioMapper.toDto(usuario)
    }
}