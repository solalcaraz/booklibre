package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.service.UsuarioService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(LibroController::class)
@DisplayName("Dado un LibroController")
class MisLibrosSpec(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean lateinit var usuarioService: UsuarioService
    @MockBean lateinit var libroService: LibroService

    @Test
    @DisplayName("Cuando se elimina un libro existente, devuelve 200")
    fun delete_libro_ok() {
        doNothing().`when`(libroService).borrarLibro("1")

        mockMvc.perform(delete("/misLibros/1"))
            .andExpect(status().isOk)

        verify(libroService).borrarLibro("1")
    }

    @Test
    @DisplayName("Cuando se intenta eliminar un libro inexistente, devuelve 404")
    fun delete_libro_not_found() {
        doThrow(NotFoundException("No se encontró el objeto con ID 99."))
            .`when`(libroService)
            .borrarLibro("99")

        mockMvc.perform(delete("/misLibros/99"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("Cuando se intenta eliminar un libro mediante un id invalido, devuelve 400")
    fun delete_libro_invalid_id() {
        doThrow(BadRequestException("El ID brindado no es válido"))
            .`when`(libroService)
            .borrarLibro("falopa")

        mockMvc.perform(delete("/misLibros/falopa"))
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Cuando se listan los libros de un usuario existente, devuelve 200 con la página correspondiente")
    fun listar_mis_libros_ok() {
        val usuario = Usuario(
            id = 1,
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password = "admin"
        )

        val libro1 = MiLibroDTO(
            id = 1,
            titulo = "1984",
            autor = "George Orwell",
            genero = "Ciencia Ficción",
            estadoReserva = "DISPONIBLE",
            agregadoEl = LocalDate.now(),
            portada = "portadaMock"
        )

        val libro2 = MiLibroDTO(
            id = 2,
            titulo = "Dune",
            autor = "Frank Herbert",
            genero = "Ciencia Ficción",
            estadoReserva = "DISPONIBLE",
            agregadoEl = LocalDate.now(),
            portada = "portadaMock"
        )

        `when`(usuarioService.getUsuarioById("1")).thenReturn(usuario)
        `when`(libroService.misLibros(usuario, "Todos")).thenReturn(listOf(libro1, libro2))

        mockMvc.perform(
            get("/misLibros/1")
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
    @DisplayName("Cuando se listan los libros de un usuario no existente, devuelve 404")
    fun listar_mis_libros_usuario_not_found() {
        `when`(usuarioService.getUsuarioById("99"))
            .thenThrow(NotFoundException("Usuario no encontrado"))

        mockMvc.perform(
            get("/misLibros/99")
                .param("page", "0")
                .param("size", "3")
                .param("filter", "Todos")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("Cuando se listan los libros de un usuario no existente, devuelve 404")
    fun listar_mis_libros_usuario_invalid_id() {
        `when`(usuarioService.getUsuarioById("falopa"))
            .thenThrow(BadRequestException("Usuario con ID invalido"))

        mockMvc.perform(
            get("/misLibros/falopa")
                .param("page", "0")
                .param("size", "3")
                .param("filter", "Todos")
        )
            .andExpect(status().isBadRequest)
    }
}