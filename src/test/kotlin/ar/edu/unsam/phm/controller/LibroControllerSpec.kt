/*
package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.helpers.LibroMapper
import ar.edu.unsam.phm.service.LibroService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(LibroController::class)
class LibroControllerSpec (@Autowired val mockMvc: MockMvc) {
    @MockBean lateinit var libroMapper: LibroMapper
    @MockBean lateinit var libroService: LibroService

    private fun generarJsonLibro(idDueno: Int, tipo: String = "COMUN") = """
        {
          "id": null,
          "titulo": "Rayuela",
          "tipo": "$tipo",
          "genero": "DRAMA",
          "autor": "Cortázar",
          "idioma": "ESPANOL",
          "estado": "BUENO",
          "editorial": "Alfaguara",
          "fechaPublicacion": "1963-01-01",
          "paginas": 600,
          "isbn13": "9788437604947",
          "descripcion": "Novela",
          "portada": "url",
          "idDueno": $idDueno
        }
    """.trimIndent()

    // Esta función engaña a Kotlin diciendo que el nulo de Mockito es un T no nulo
    private fun <T> anyKotlin(): T {
        org.mockito.ArgumentMatchers.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T

    @Test
    @DisplayName("GET /libro/{id} -> Devuelve el detalle del libro usando el mapper")
    fun obtener_libro_ok() {
        val idLibro = 1
        val dueno = Usuario(10, "Julio", "j@c.com", "1")
        val libroExistente = LibroComun(
            LibroData("Rayuela", "Desc", Genero.DRAMA, "Cortazar", 100, "1", Idioma.ESPANOL, "Ed", LocalDate.now(), Estado.BUENO, dueno)
        )

        val dtoEsperado = LibroDetalleDTO(
            id = 1,
            titulo = "Rayuela",
            tipo = TipoDeLibroDTO.COMUN,
            genero = "DRAMA",
            autor = "Cortazar",
            idioma = "ESPANOL",
            estado = "BUENO",
            editorial = "Ed",
            fechaPublicacion = "1963-01-01",
            paginas = 100,
            isbn13 = "1",
            descripcion = "Desc",
            portada = "url",
            idDueno = 10
        )

        `when`(libroService.getById(idLibro)).thenReturn(libroExistente)

        `when`(libroMapper.toDetalleDto(libroExistente)).thenReturn(dtoEsperado)

        mockMvc.perform(
            get("/libro/$idLibro")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.titulo").value("Rayuela"))
            .andExpect(jsonPath("$.idDueno").value(10))
            .andExpect(jsonPath("$.tipo").value("COMUN"))
    }

    @Test
    @DisplayName("POST /libro/nuevo")
    fun crear_libro_ok() {
        val json = generarJsonLibro(idDueno = 1, tipo = "COMUN")

        mockMvc.perform(
            post("/libro/nuevo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk)

        // anyKotlin() para que no tire NullPointerException
        verify(libroService).create(anyKotlin())
    }

    @Test
    @DisplayName("PUT /libro/{id} -> Permite actualizar si el dueño coincide")
    fun actualizar_libro_propio_ok() {
        val idLibro = 1
        val idDueno = 10
        val dueno = Usuario(idDueno,"Julio", "j@c.com", "1")
        val libroExistente = LibroComun(LibroData("Tit", "Desc", Genero.DRAMA, "Aut", 100, "1", Idioma.ESPANOL, "Ed", LocalDate.now(), Estado.BUENO, dueno))

        `when`(libroService.getById(idLibro)).thenReturn(libroExistente)

        mockMvc.perform(
            put("/libro/$idLibro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(generarJsonLibro(idDueno = idDueno))
        ).andExpect(status().isOk)

        // eq() para el ID y anyKotlin() para el DTO
        verify(libroService).update(eq(idLibro), anyKotlin())
    }

    @Test
    @DisplayName("PUT /libro/{id} -> Devuelve 403 si el ID del dueño en el JSON no es el del dueño real")
    fun actualizar_libro_ajeno_error() {
        val idLibro = 1
        val idDuenoReal = 10
        val idImpostor = 99

        val duenoReal = Usuario(idDuenoReal,"Julio", "j@c.com", "1")
        val libroExistente = LibroComun(
            LibroData("Tit", "Desc", Genero.DRAMA, "Aut", 100, "1", Idioma.ESPANOL, "Ed", LocalDate.now(), Estado.BUENO, duenoReal)
        )

        `when`(libroService.getById(idLibro)).thenReturn(libroExistente)

        mockMvc.perform(
            put("/libro/$idLibro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(generarJsonLibro(idDueno = idImpostor))
        ).andExpect(status().isForbidden)

        // times(0) para asegurar que el flujo se cortó y NUNCA se intentó actualizar
        verify(libroService, times(0)).update(anyInt(), anyKotlin())
    }

    @Test
    @DisplayName("PUT /libro/{id} -> Devuelve 404 si el libro no existe")
    fun actualizar_libro_inexistente_error() {
        val idInexistente = 999

        `when`(libroService.getById(idInexistente)).thenThrow(ar.edu.unsam.phm.exceptions.NotFoundException("No se encontró"))

        mockMvc.perform(
            put("/libro/$idInexistente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(generarJsonLibro(idDueno = 1))
        ).andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("DELETE /misLibros/{id} -> Borra el libro y devuelve 200")
    fun borrar_libro_ok() {
        val idLibro = "1"

        doNothing().`when`(libroService).borrarLibro(idLibro)

        mockMvc.perform(
            delete("/misLibros/$idLibro")
        ).andExpect(status().isOk)

        verify(libroService).borrarLibro(idLibro)
    }

    @Test
    @DisplayName("DELETE /misLibros/{id} -> Devuelve 404 si el libro a borrar no existe")
    fun borrar_libro_inexistente_error() {
        val idInexistente = "999"

        // Forzamos al service a lanzar la excepción: como borrarLibro devuelve Unit, usamos doThrow
        doThrow(ar.edu.unsam.phm.exceptions.NotFoundException("No se encontró el libro"))
            .`when`(libroService).borrarLibro(idInexistente)

        mockMvc.perform(
            delete("/misLibros/$idInexistente")
        ).andExpect(status().isNotFound)

        verify(libroService).borrarLibro(idInexistente)
    }
}*/
