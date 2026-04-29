package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.PerfilUsuarioDTO
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.security.CustomUserDetailsService
import ar.edu.unsam.phm.security.TokenHelper
import ar.edu.unsam.phm.service.UsuarioService
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

/* Ahora los tests cambiaron, ya no se testea NotFound porque no tiene sentido
solo hay dos posibilidades, que el usuario esté autenticado, o no
si está autenticado, el usuario existe por lo tanto nunca es NotFound, y si no está autenticado es 401 */

@WebMvcTest(UsuarioController::class)
@AutoConfigureMockMvc
class UsuarioControllerSpec(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean lateinit var usuarioService: UsuarioService
    @MockBean lateinit var usuarioMapper: UsuarioMapper
    @MockBean lateinit var customUserDetailsService: CustomUserDetailsService
    @MockBean lateinit var tokenHelper: TokenHelper
    @MockBean lateinit var usuarioRepository: UsuarioRepository

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando se busca un usuario existente por id, devuelve 200 con el perfil completo")
    fun get_usuario_ok() {
        val usuario = Usuario(
            id = 1,
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password = "admin"
        ).apply {
            descripcion = "Jugador histórico"
            telefono = "1166666666"
            ciudad = "Buenos Aires"
            bibliokarmas = 1986
            imagen = "imagenMock"
            librosLeidos = 10
            librosPrestados = 5
        }

        val perfilUsuarioDTO = PerfilUsuarioDTO(
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            descripcion = "Jugador histórico",
            telefono = "1166666666",
            anoUnido = 2024,
            ciudad = "Buenos Aires",
            bibliokarmas = 1986,
            tipoUsuario = "Lector",
            imagen = "imagenMock",
            librosLeidos = 10,
            librosPrestados = 5
        )

        `when`(usuarioService.getUsuarioById(1)).thenReturn(usuario)
        `when`(usuarioMapper.toFullDto(usuario)).thenReturn(perfilUsuarioDTO)

        mockMvc.perform(get("/usuario/me"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nombreCompleto").value("Diego Maradona"))
            .andExpect(jsonPath("$.email").value("diego@maradona.com"))
            .andExpect(jsonPath("$.descripcion").value("Jugador histórico"))
            .andExpect(jsonPath("$.telefono").value("1166666666"))
            .andExpect(jsonPath("$.anoUnido").value(2024))
            .andExpect(jsonPath("$.ciudad").value("Buenos Aires"))
            .andExpect(jsonPath("$.bibliokarmas").value(1986))
            .andExpect(jsonPath("$.tipoUsuario").value("Lector"))
            .andExpect(jsonPath("$.imagen").value("imagenMock"))
            .andExpect(jsonPath("$.librosLeidos").value(10))
            .andExpect(jsonPath("$.librosPrestados").value(5))

        verify(usuarioService).getUsuarioById(1)
    }

    @Test
    @DisplayName("Cuando se busca un usuario sin estar autenticado, devuelve 401")
    fun get_usuario_sin_autenticar() {
        mockMvc.perform(get("/usuario/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando se actualiza un usuario existente, devuelve 200")
    fun update_usuario_ok() {
        val patchRequest = UsuarioPatchRequest(
            nombreCompleto = "Diego Maradona",
            descripcion = "Actualizado",
            email = "diego@maradona.com",
            telefono = "1166666666",
            ciudad = "Buenos Aires",
            imagen = "imagenNueva",
            tipoUsuario = "Publicador"
        )
        val patchResponse = AuthenticationResponse(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
        )

        `when`(usuarioService.updateUsuario(patchRequest, 1)).thenReturn(patchResponse)

        mockMvc.perform(
            patch("/usuario/me")
                /* En tu app real, tu SecurityConfiguration desactiva CSRF (http.csrf { it.disable() }),
                pero en el test slice @WebMvcTest Spring te está levantando parte de la security auto-config
                por defecto (se nota por el “generated security password” en logs). En ese contexto, CSRF queda activo.
                Si no lo mandás, devuelve 403 antes de llegar al controller.
                Por eso with(csrf()) lo arregla solo para métodos de escritura. */
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "nombreCompleto" : "Diego Maradona",
                      "descripcion" : "Actualizado",
                      "email" : "diego@maradona.com",
                      "telefono" : "1166666666",
                      "ciudad" : "Buenos Aires",
                      "imagen" : "imagenNueva",
                      "tipoUsuario" : "Publicador"
                    }
                    """.trimIndent())
        )
            .andExpect(status().isOk)

        verify(usuarioService).updateUsuario(patchRequest, 1)
    }

    @Test
    @DisplayName("Cuando se intenta actualizar un usuario sin autenticarse, devuelve 401")
    fun update_usuario_sin_autenticar() {
        mockMvc.perform(
            patch("/usuario/me")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombreCompleto" : "Diego Maradona",
                      "descripcion" : "Actualizado",
                      "email" : "diego@maradona.com",
                      "telefono" : "1166666666",
                      "ciudad" : "Buenos Aires",
                      "imagen" : "imagenNueva",
                      "tipoUsuario" : "Publicador"
                    }
                    """.trimIndent())
        )
            .andExpect(status().isUnauthorized)
    }
}
