package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.MiLibroProjection
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.LibroAdapter
import ar.edu.unsam.phm.security.JwtAuthenticationFilter
import ar.edu.unsam.phm.security.SecurityConfiguration
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.service.UsuarioService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import org.springframework.test.web.servlet.MockMvc
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.mockito.ArgumentMatchers.any

@WebMvcTest(LibroController::class)
@AutoConfigureMockMvc
@Import(SecurityConfiguration::class)
class MisLibrosSpec(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean lateinit var usuarioService: UsuarioService
    @MockBean lateinit var libroService: LibroService
    @MockBean lateinit var authenticationProvider: AuthenticationProvider
    @MockBean lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    /* No lo uso, pero tengo que mockearlo porque está en el constructor del controller */
    @MockBean lateinit var libroAdapter: LibroAdapter

    /* Jwt filter bypass, no “saltea seguridad”; solo hace que el mock se comporte como filtro
    pasante para que el resto de Spring Security + MVC se ejecute normal en el test, ya que
    por defecto, no llama a filterChain.doFilter(request, response) */
    @BeforeEach
    fun allowJwtFilterChain() {
        doAnswer { invocation ->
            val request = invocation.getArgument<ServletRequest>(0)
            val response = invocation.getArgument<ServletResponse>(1)
            val chain = invocation.getArgument<FilterChain>(2)
            chain.doFilter(request, response)
            null
        }.`when`(jwtAuthenticationFilter).doFilter(any(), any(), any())
    }

    @Test
    @WithMockUser(username = "1", roles = ["PUBLICADOR"])
    @DisplayName("Cuando el usuario tiene rol PUBLICADOR, puede eliminar")
    fun delete_libro_ok() {
        doNothing().`when`(libroService).despublicarLibro("1")

        mockMvc.perform(delete("/misLibros/1").with(csrf()))
            .andExpect(status().isOk)

        verify(libroService).despublicarLibro("1")
    }

    @Test
    @WithMockUser(username = "1", roles = ["LECTOR_PUBLICADOR"])
    @DisplayName("Cuando el usuario tiene rol LECTOR_PUBLICADOR, puede eliminar")
    fun delete_libro_ok_lector_publicador() {
        doNothing().`when`(libroService).despublicarLibro("1")

        mockMvc.perform(delete("/misLibros/1").with(csrf()))
            .andExpect(status().isOk)

        verify(libroService).despublicarLibro("1")
    }

    @Test
    @WithMockUser(username = "1", roles = ["LECTOR"])
    @DisplayName("Cuando el usuario no tiene rol PUBLICADOR ni LECTOR_PUBLICADOR, devuelve 403 al eliminar")
    fun delete_libro_forbidden_for_user_role() {
        mockMvc.perform(delete("/misLibros/1").with(csrf()))
            .andExpect(status().isForbidden)

        verify(libroService, never()).despublicarLibro("1")
    }

    @Test
    @WithMockUser(username = "1", roles = ["PUBLICADOR"])
    @DisplayName("Cuando se intenta eliminar un libro inexistente, devuelve 404")
    fun delete_libro_not_found() {
        doThrow(NotFoundException("No se encontró el objeto con ID 99."))
            .`when`(libroService)
            .despublicarLibro("99")

        mockMvc.perform(delete("/misLibros/99").with(csrf()))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("Cuando el usuario tiene rol PUBLICADOR, puede ver sus libros")
    @WithMockUser(username = "1", roles = ["PUBLICADOR"])
    fun listar_mis_libros_ok() {
        val usuario = Usuario(
            id = 1,
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password = "admin"
        )

        val libro1 = MiLibroProjection(
            id = 1,
            titulo = "1984",
            autor = "George Orwell",
            genero = Genero.CIENCIA_FICCION,
            estadoReserva = "DISPONIBLE",
            agregadoEl = LocalDate.now(),
            portada = "portadaMock"
        )

        val libro2 = MiLibroProjection(
            id = 2,
            titulo = "Dune",
            autor = "Frank Herbert",
            genero = Genero.CIENCIA_FICCION,
            estadoReserva = "DISPONIBLE",
            agregadoEl = LocalDate.now(),
            portada = "portadaMock"
        )

        val paginaMock = PaginationResponseDTO(
            items = listOf(libro1, libro2),
            page = 0,
            size = 3,
            totalItems = 2,
            totalPages = 1
        )

        `when`(usuarioService.getUsuarioById(1)).thenReturn(usuario)
        `when`(libroService.buscarMisLibros(1, "Todos", "", 0, 3)).thenReturn(paginaMock)

        mockMvc.perform(
            get("/misLibros/me")
                .param("page", "0")
                .param("size", "3")
                .param("filter", "Todos")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect { jsonPath("$.items.length()").value(2) }
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(3))
            .andExpect(jsonPath("$.totalItems").value(2))
            .andExpect(jsonPath("$.totalPages").value(1))
    }

    @Test
    @DisplayName("Cuando el usuario tiene rol LECTOR, no puede ver sus libros y devuelve 403")
    @WithMockUser(username = "1", roles = ["LECTOR"])
    fun listar_mis_libros_forbidden_for_user_role() {
        mockMvc.perform(
            get("/misLibros/me")
                .param("page", "0")
                .param("size", "3")
                .param("filter", "Todos")
        )
            .andDo(print())
            .andExpect(status().isForbidden)
    }
}
