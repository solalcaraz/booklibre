package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.LoginRequest
import ar.edu.unsam.phm.dto.LoginResponseDTO
import ar.edu.unsam.phm.dto.RegisterRequest
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.exceptions.UnauthorizedException
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.service.AuthService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
@DisplayName("Dado un AuthController")
class AuthSpec(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean lateinit var authService: AuthService
    @MockBean lateinit var usuarioMapper: UsuarioMapper

    @Test
    @DisplayName("Cuando un usuario se logea con los datos correctos, devuelve 200 con el DTO esperado")
    fun login_ok() {
        val usuarioReal = Usuario(
            id = 1,
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password = "admin"
        ).apply {
            bibliokarmas = 1986
            imagen = "imagenMock"
        }

        val loginRequest = LoginRequest(email = "diego@maradona.com", password = "admin")

        val loginResponseDTO = LoginResponseDTO(
            id = 1,
            nombreCompleto = "Diego Maradona",
            imagen = "imagenMock",
            bibliokarmas = 1986
        )

        `when`(authService.login(loginRequest)).thenReturn(usuarioReal)
        `when`(usuarioMapper.toDto(usuarioReal)).thenReturn(loginResponseDTO)

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
            .andExpect(jsonPath("$.nombreCompleto").value("Diego Maradona"))
            .andExpect(jsonPath("$.bibliokarmas").value(1986))
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
    @DisplayName("Cuando un usuario intenta logearse con un email inexistente, devuelve 404")
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
    @DisplayName("Cuando un usuario se registra con los datos correctos, devuelve 200 con el DTO esperado")
    fun register_ok() {
        val usuarioReal = Usuario(
            id = 1,
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password = "admin"
        ).apply {
            bibliokarmas = 1986
            imagen = "imagenMock"
        }

        val registerRequest = RegisterRequest(
            email = "diego@maradona.com",
            nombreCompleto = "Diego Maradona",
            password = "admin"
        )

        val registerResponseDTO = LoginResponseDTO(
            id = 1,
            nombreCompleto = "Diego Maradona",
            imagen = "imagenMock",
            bibliokarmas = 1986
        )

        `when`(authService.register(registerRequest)).thenReturn(usuarioReal)
        `when`(usuarioMapper.toDto(usuarioReal)).thenReturn(registerResponseDTO)

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
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nombreCompleto").value("Diego Maradona"))
            .andExpect(jsonPath("$.bibliokarmas").value(1986))
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