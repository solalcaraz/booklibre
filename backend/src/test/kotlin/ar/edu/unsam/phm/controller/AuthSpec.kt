package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.LoginRequest
import ar.edu.unsam.phm.dto.RegisterRequest
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.exceptions.UnauthorizedException
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.security.CustomUserDetailsService
import ar.edu.unsam.phm.security.JwtAuthenticationFilter
import ar.edu.unsam.phm.security.TokenHelper
import ar.edu.unsam.phm.service.AuthService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthSpec(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean lateinit var authService: AuthService
    @MockBean lateinit var usuarioMapper: UsuarioMapper
    @MockBean lateinit var customUserDetailsService: CustomUserDetailsService
    @MockBean lateinit var tokenHelper: TokenHelper
    @MockBean lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Test
    @DisplayName("Cuando un usuario se logea con los datos correctos, devuelve 200 con los tokens")
    fun login_ok() {
        val loginRequest = LoginRequest(email = "diego@maradona.com", password = "admin")

        val loginResponseDTO = AuthenticationResponse(
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

        `when`(authService.login(loginRequest)).thenReturn(loginResponseDTO)

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "diego@maradona.com",
                      "password": "admin"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").value("accessToken"))
            .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
    }

    @Test
    @DisplayName("Cuando un usuario se logea con credenciales inválidas, devuelve 401")
    fun login_invalid() {
        val loginRequest = LoginRequest(
            email = "diego@maradona.com",
            password = "wrong-password"
        )

        `when`(authService.login(loginRequest))
            .thenThrow(UnauthorizedException("Usuario o contraseña incorrecto."))

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "diego@maradona.com",
                      "password": "wrong-password"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("Cuando un usuario intenta loguearse con un email inexistente, devuelve 404")
    fun login_not_found() {
        val loginRequest = LoginRequest(
            email = "noexiste@maradona.com",
            password = "admin"
        )

        `when`(authService.login(loginRequest))
            .thenThrow(NotFoundException("Usuario no registrado."))

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "noexiste@maradona.com",
                      "password": "admin"
                    }
                    """.trimIndent())
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("Cuando un usuario se registra con los datos correctos, devuelve 200")
    fun register_ok() {
        val registerRequest = RegisterRequest(
            email = "jorge@maradona.com",
            nombreCompleto = "Jorge Maradona",
            password = "admin"
        )

        doNothing().`when`(authService).register(registerRequest)

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "nombreCompleto": "Jorge Maradona",
                      "email": "jorge@maradona.com",
                      "password": "admin"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("Cuando un usuario se registra con credenciales ya existentes, devuelve 409")
    fun register_conflict() {
        val registerRequest = RegisterRequest(
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password = "admin"
        )

        `when`(authService.register(registerRequest))
            .thenThrow(ConflictException("Este usuario ya está registrado"))

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "nombreCompleto": "Diego Maradona",
                      "email": "diego@maradona.com",
                      "password": "admin"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isConflict)
    }
}