package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.PerfilUsuarioDTO
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.service.UsuarioService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UsuarioController::class)
@DisplayName("Dado un UsuarioController")
class UsuarioControllerSpec(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean lateinit var usuarioService: UsuarioService
    @MockBean lateinit var usuarioMapper: UsuarioMapper

    @Test
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

        `when`(usuarioService.getUsuarioById("1")).thenReturn(usuario)
        `when`(usuarioMapper.toFullDto(usuario)).thenReturn(perfilUsuarioDTO)

        mockMvc.perform(get("/usuario/1"))
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
    }

    @Test
    @DisplayName("Cuando se busca un usuario inexistente por id, devuelve 404")
    fun get_usuario_not_found() {
        `when`(usuarioService.getUsuarioById("99"))
            .thenThrow(NotFoundException("No se encontró el objeto con ID 99."))

        mockMvc.perform(get("/usuario/99"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("Cuando se busca un usuario por un id invalido, devuelve 400")
    fun get_usuario_invalid_id() {
        `when`(usuarioService.getUsuarioById("falopa"))
            .thenThrow(BadRequestException("El ID brindado no es válido"))

        mockMvc.perform(get("/usuario/falopa"))
            .andExpect(status().isBadRequest)
    }

    @Test
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

        // No devuelve nada - doNothing()
        doNothing().`when`(usuarioService)
            .updateUsuario(patchRequest, "1")

        mockMvc.perform(
            patch("/usuario/1")
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
    }

    @Test
    @DisplayName("Cuando se intenta actualizar un usuario inexistente, devuelve 404")
    fun update_usuario_not_found() {
        val patchRequest = UsuarioPatchRequest(
            nombreCompleto = "Diego Maradona",
            descripcion = "Actualizado",
            email = "diego@maradona.com",
            telefono = "1166666666",
            ciudad = "Buenos Aires",
            imagen = "imagenNueva",
            tipoUsuario = "Publicador"
        )

        doThrow(NotFoundException("No se encontró el objeto con ID 99."))
            .`when`(usuarioService)
            .updateUsuario(patchRequest, "99")

        mockMvc.perform(
            patch("/usuarios/99")
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
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("Cuando se intenta actualizar un usuario con un id no valido, devuelve 400")
    fun update_usuario_invalid_id() {
        val patchRequest = UsuarioPatchRequest(
            nombreCompleto = "Diego Maradona",
            descripcion = "Actualizado",
            email = "diego@maradona.com",
            telefono = "1166666666",
            ciudad = "Buenos Aires",
            imagen = "imagenNueva",
            tipoUsuario = "Publicador"
        )

        doThrow(BadRequestException("El ID brindado no es válido"))
            .`when`(usuarioService)
            .updateUsuario(patchRequest, "falopa")

        mockMvc.perform(
            patch("/usuario/falopa")
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
            .andExpect(status().isBadRequest)
    }


}