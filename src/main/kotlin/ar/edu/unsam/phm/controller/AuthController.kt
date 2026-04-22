package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.LoginRequest
import ar.edu.unsam.phm.dto.LoginResponseDTO
import ar.edu.unsam.phm.dto.RegisterRequest
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
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
    fun login(
        @RequestBody usuario: LoginRequest,
        response: HttpServletResponse
        ): ResponseEntity<Map<String, String>> {

        val token = authService.login(usuario)

        val cookie = ResponseCookie.from("ACCESS_TOKEN", token)
            .httpOnly(true)
            .secure(false) // true en producción (HTTPS)
            .sameSite("Lax")
            .path("/")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

        return ResponseEntity.ok(mapOf("message" to "Login exitoso"))
    }

    @PostMapping("/register")
    fun register(@RequestBody usuarioRegister: RegisterRequest): LoginResponseDTO {
        val usuario = authService.register(usuarioRegister)
        return usuarioMapper.toDto(usuario)
    }
}