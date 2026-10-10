package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.LoginRequest
import ar.edu.unsam.phm.dto.RefreshTokenRequest
import ar.edu.unsam.phm.dto.RefreshTokenResponse
import ar.edu.unsam.phm.dto.RegisterRequest
import ar.edu.unsam.phm.exceptions.ForbiddenException
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
    val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody usuario: LoginRequest): AuthenticationResponse =
        authService.login(usuario)

    @PostMapping("/register")
    fun register(@RequestBody usuarioRegister: RegisterRequest) {
        authService.register(usuarioRegister)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): RefreshTokenResponse {
        return authService.refreshToken(request.token)?.maptoTokenResponse()?: throw ForbiddenException("Refresh token invalido")
    }

    private fun String.maptoTokenResponse(): RefreshTokenResponse =
        RefreshTokenResponse(token = this)
}