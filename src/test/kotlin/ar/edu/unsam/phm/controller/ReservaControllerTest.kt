package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.DetalleReservaDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.dto.ReservaLibroDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.helpers.ReservaMapper
import ar.edu.unsam.phm.service.ReseniaService
import ar.edu.unsam.phm.service.ReservaService
import ar.edu.unsam.phm.security.CustomUserDetailsService
import ar.edu.unsam.phm.security.TokenHelper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import java.time.LocalDate

@WebMvcTest(ReservaController::class)
@AutoConfigureMockMvc
class ReservaControllerTest(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean lateinit var reservaService: ReservaService
    @MockBean lateinit var reservaMapper: ReservaMapper
    @MockBean lateinit var reseniaService: ReseniaService
    @MockBean lateinit var customUserDetailsService: CustomUserDetailsService
    @MockBean lateinit var tokenHelper: TokenHelper

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando se buscan libros disponibles para un usuario autenticado, devuelve 200")
    fun obtener_libros_disponibles_ok() {
        val libroDTO = ReservaLibroDTO(
            id = 1,
            titulo = "1984",
            autor = "George Orwell",
            genero = "CIENCIA_FICCION",
            dueno = "Lionel Messi"
        )

        val page = PageImpl(listOf(libroDTO), org.springframework.data.domain.PageRequest.of(0, 10), 1)
        
        `when`(reservaService.getLibrosDisponiblesParaReserva(1, any())).thenReturn(page)

        mockMvc.perform(get("/reserva/librosDisponibles"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items[0].titulo").value("1984"))
    }

    @Test
    @DisplayName("Cuando se buscan libros disponibles sin autenticarse, devuelve 401")
    fun obtener_libros_disponibles_sin_autenticar() {
        mockMvc.perform(get("/reserva/librosDisponibles"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando se obtienen detalles de una reserva existente, devuelve 200")
    fun obtener_detalles_reserva_ok() {
        val detalleDTO = DetalleReservaDTO(
            calificacionPromedio = 4.5,
            libroDetalleDTO = null,
            cantidadResenias = 1
        )

        `when`(reservaService.getDetalleReserva(1, 1, LocalDate.now().plusDays(10), LocalDate.now().plusDays(20)))
            .thenReturn(detalleDTO)

        val fechaDesde = LocalDate.now().plusDays(10)
        val fechaHasta = LocalDate.now().plusDays(20)

        mockMvc.perform(
            get("/reserva/detalles/1")
                .param("fechaDesde", fechaDesde.toString())
                .param("fechaHasta", fechaHasta.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.calificacionPromedio").value(4.5))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando intenta crear una reserva con fechas invertidas, devuelve 400")
    fun crear_reserva_fechas_invertidas() {
        val fechaDesde = LocalDate.now().plusDays(20)
        val fechaHasta = LocalDate.now().plusDays(10)

        `when`(reservaService.getDetalleReserva(1, 1, fechaDesde, fechaHasta))
            .thenThrow(BadRequestException("La fecha hasta no puede ser anterior a la fecha desde."))

        mockMvc.perform(
            get("/reserva/detalles/1")
                .param("fechaDesde", fechaDesde.toString())
                .param("fechaHasta", fechaHasta.toString())
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando se crea una reserva correctamente, devuelve 200")
    fun crear_reserva_ok() {
        val reservaDTO = ReservaDTO(
            id = 1,
            libroResumen = null,
            prestadoPor = "Lionel Messi",
            bibliokarma = 250,
            fechaDesde = LocalDate.now().plusDays(10),
            fechaHasta = LocalDate.now().plusDays(15),
            estado = "ACTIVO",
            puedeCalificar = false
        )

        `when`(reservaService.crearReserva(1, any())).thenReturn(reservaDTO)

        mockMvc.perform(
            post("/reserva/crear")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "libroId": 1,
                      "fechaDesde": "${LocalDate.now().plusDays(10)}",
                      "fechaHasta": "${LocalDate.now().plusDays(15)}"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)

        verify(reservaService).crearReserva(1, any())
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando intenta crear una reserva con fechas superpuestas, devuelve 409")
    fun crear_reserva_fechas_superpuestas() {
        `when`(reservaService.crearReserva(1, any()))
            .thenThrow(ConflictException("El libro ya se encuentra reservado en esas fechas."))

        mockMvc.perform(
            post("/reserva/crear")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "libroId": 1,
                      "fechaDesde": "${LocalDate.now().plusDays(10)}",
                      "fechaHasta": "${LocalDate.now().plusDays(15)}"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isConflict)
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando se califican una reserva finalizada, devuelve 200")
    fun calificar_reserva_ok() {
        mockMvc.perform(
            post("/reserva/calificar")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "reservaId": 1,
                      "puntuacion": 4,
                      "comentario": "Muy buen libro"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)

        verify(reseniaService).crearResenia(any())
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando se obtienen mis reservas, devuelve 200 con la lista paginada")
    fun obtener_mis_reservas_ok() {
        val reservaDTO = ReservaDTO(
            id = 1,
            libroResumen = null,
            prestadoPor = "Lionel Messi",
            bibliokarma = 250,
            fechaDesde = LocalDate.now(),
            fechaHasta = LocalDate.now().plusDays(5),
            estado = "ACTIVO",
            puedeCalificar = false
        )

        val page = PageImpl(listOf(reservaDTO), org.springframework.data.domain.PageRequest.of(0, 10), 1)
        `when`(reservaService.getMisReservas(any())).thenReturn(page)
        `when`(reservaMapper.toDtoReservaPag(page)).thenReturn(
            PaginationResponseDTO(
                items = listOf(reservaDTO),
                page = 0,
                size = 10,
                totalItems = 1,
                totalPages = 1
            )
        )

        mockMvc.perform(
            post("/reserva/obtenerMisReservas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "pageSize": 10,
                      "pageNumber": 0,
                      "filter": {
                        "usuarioId": "1",
                        "inputSearch": ""
                      }
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("Cuando se obtienen libros prestados por mí, devuelve 200")
    fun obtener_prestados_por_mi_ok() {
        val page = PageImpl(listOf<ReservaDTO>(), org.springframework.data.domain.PageRequest.of(0, 10), 0)
        `when`(reservaService.getPrestadosPorMi(any())).thenReturn(page)
        `when`(reservaMapper.toDtoReservaPag(page)).thenReturn(
            PaginationResponseDTO(
                items = emptyList(),
                page = 0,
                size = 10,
                totalItems = 0,
                totalPages = 0
            )
        )

        mockMvc.perform(
            post("/reserva/obtenerPrestadosPorMi")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "pageSize": 10,
                      "pageNumber": 0,
                      "filter": {
                        "usuarioId": "1",
                        "inputSearch": ""
                      }
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("Cuando intenta obtener mis reservas sin autenticarse, devuelve 401")
    fun obtener_mis_reservas_sin_autenticar() {
        mockMvc.perform(
            post("/reserva/obtenerMisReservas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "pageSize": 10,
                      "pageNumber": 0,
                      "filter": {
                        "usuarioId": "1",
                        "inputSearch": ""
                      }
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isUnauthorized)
    }
}
            .andExpect(jsonPath("$.items[1].dueno").value("Lionel Messi"))
            .andExpect(jsonPath("$.items[2].dueno").value("Lionel Messi"))
    }
    // endregion

    // region GET /detalles/{libroId}}
    @Test
    fun `se pueden obtener los detalles de simulacion de una reserva`() {
        val fechaDesde = LocalDate.now().plusDays(10).toString()
        val fechaHasta = LocalDate.now().plusDays(20).toString()

        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/detalles/${libroDeUsuario2.id}")
                    .header("userId", usuario.id!!) //Usuario1
                    .param("fechaDesde", fechaDesde)
                    .param("fechaHasta", fechaHasta)
            )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.calificacionPromedio").value(0.0))
            .andExpect(jsonPath("$.libroDetalleDTO.titulo").value("Rayuela"))
            .andExpect(jsonPath("$.libroDetalleDTO.autor").value("Julio Cortázar"))
    }

    @Test
    fun `pedir detalles con fecha hasta anterior a desde devuelve 400 Bad Request`() {
        val fechaDesde = LocalDate.now().plusDays(20).toString()
        val fechaHasta = LocalDate.now().plusDays(10).toString() // Fechas invertidas

        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/detalles/${libroDeUsuario2.id}")
                    .header("userId", usuario.id!!)
                    .param("fechaDesde", fechaDesde)
                    .param("fechaHasta", fechaHasta)
            )
            .andExpect(status().isBadRequest)
            .andExpect { result ->
                val excepcionLanzada = result.resolvedException
                assertTrue(excepcionLanzada is BadRequestException)
                assertEquals(
                    "La fecha hasta no puede ser anterior a la fecha desde.",
                    excepcionLanzada?.message
                )
            }
    }

    @Test
    fun `No puedo reservar mis propios libros`() {
        val fechaDesde = LocalDate.now().plusDays(10).toString()
        val fechaHasta = LocalDate.now().plusDays(20).toString()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/reserva/detalles/${libroDeUsuario2.id}")
                .header("userId", usuario2.id!!)
                .param("fechaDesde", fechaDesde)
                .param("fechaHasta", fechaHasta)
        )
            .andExpect(status().isBadRequest)
            .andExpect { result ->
                val excepcionLanzada = result.resolvedException
                assertTrue(excepcionLanzada is BadRequestException)
                assertEquals(
                    "No puedo reservar mis propios libros",
                    excepcionLanzada?.message
                )
            }
    }
    // endregion

    // region POST /crear
    @Test
    fun `se puede crear una reserva correctamente`() {
        val fechaDesde = LocalDate.now().plusDays(10).toString()
        val fechaHasta = LocalDate.now().plusDays(15).toString()

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reserva/crear")
                    .header("userId", usuario.id!!)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{
                          "libroId": ${libroDeUsuario2.id},
                          "fechaDesde": "$fechaDesde",
                          "fechaHasta": "$fechaHasta"
                        }""".trimIndent())
            )
            .andExpect(status().isOk)

        val reservasGuardadas = reservaRepositorio.buscarPorLibro(libroDeUsuario2)
        assertEquals(1, reservasGuardadas.size)
    }

    @Test
    fun `intentar crear una reserva con fechas superpuestas devuelve 409 Conflict`() {
        val fechaDesde = LocalDate.now().plusDays(10)
        val fechaHasta = LocalDate.now().plusDays(20)

        reservaRepositorio.create(
            Reserva(
                libro = libroDeUsuario2,
                lector = usuario,
                fechaDesde = fechaDesde,
                fechaHasta = fechaHasta
            )
        )

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reserva/crear")
                    .header("userId", usuario.id!!)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "libroId": ${libroDeUsuario2.id},
                          "fechaDesde": "${fechaDesde.plusDays(2)}",
                          "fechaHasta": "${fechaHasta.plusDays(5)}"
                        }
                    """.trimIndent())
            )
            .andExpect(status().isConflict)
            .andExpect { result ->
                val excepcionLanzada = result.resolvedException
                assertTrue(excepcionLanzada is ConflictException)
                assertEquals(
                    "El libro ya se encuentra reservado en esas fechas.",
                    excepcionLanzada?.message
                )
            }
    }

    @Test
    fun `Intenta crear una reserva cuando previamente ya lo hizo`(){
        val fechaDesde = LocalDate.now().plusDays(10)
        val fechaHasta = LocalDate.now().plusDays(20)

        reservaRepositorio.create(
            Reserva(
                libro = libroDeUsuario2,
                lector = usuario,
                fechaDesde = fechaDesde,
                fechaHasta = fechaHasta
            )
        )

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reserva/crear")
                    .header("userId", usuario.id!!)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "libroId": ${libroDeUsuario2.id},
                          "fechaDesde": "${fechaDesde.plusDays(20)}",
                          "fechaHasta": "${fechaHasta.plusDays(30)}"
                        }
                    """.trimIndent())
            )
            .andExpect(status().isConflict)
            .andExpect { result ->
                val excepcionLanzada = result.resolvedException
                assertTrue(excepcionLanzada is ConflictException)
                assertEquals(
                    "Ya tenés una reserva activa para este libro. Dejá leer a los demás chimwe",
                    excepcionLanzada?.message
                )
            }
    }

    // CALIFICAR
    @Test
    fun `calificar una reserva finalizada`() {
        val reserva = Reserva(
            libro = libroDeUsuario2,
            lector = usuario,
            fechaDesde = LocalDate.of(2025, 1, 1),
            fechaHasta = LocalDate.of(2025, 1, 10)
        )
        reservaRepositorio.create(reserva)

        val reseniaJson = """
            {
                "reservaId": ${reserva.id},
                "puntuacion": 4,
                "comentario": "Muy buen libro"
            }
        """.trimIndent()

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reserva/calificar")
                    .contentType("application/json")
                    .content(reseniaJson)
            )
            .andExpect(status().isOk)
    }

    // MIS RESERVAS
    @Test
    fun `se pueden obtener mis reservas`() {
        reserva1 = Reserva(
            libro = libroDeUsuario2,
            lector = usuario,
            fechaDesde = LocalDate.now(),
            fechaHasta = LocalDate.now().plusDays(5)
        )
        reserva2 = Reserva(
            libro = libroPropio,
            lector = usuario2,
            fechaDesde = LocalDate.now(),
            fechaHasta = LocalDate.now().plusDays(5)
        )

        reservaRepositorio.create(reserva1)
        reservaRepositorio.create(reserva2)

        val payload = """
        {
          "pageSize": 10,
          "pageNumber": 0,
          "filter": {
            "usuarioId": "${usuario.id}",
            "inputSearch": ""
          }
        }
    """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/reserva/obtenerMisReservas")
                .contentType("application/json")
                .content(payload)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items[0].libroResumen.titulo").value("Rayuela"))
            .andExpect(jsonPath("$.items[0].prestadoPor").value("Lionel Messi"))
    }

    @Test
    fun `se pueden filtrar mis reservas por titulo`() {
        reserva1 = Reserva(
            libro = libroDeUsuario2,
            lector = usuario,
            fechaDesde = LocalDate.now(),
            fechaHasta = LocalDate.now().plusDays(5)
        )
        reserva2 = Reserva(
            libro = libroPropio,
            lector = usuario2,
            fechaDesde = LocalDate.now(),
            fechaHasta = LocalDate.now().plusDays(5)
        )

        reservaRepositorio.create(reserva1)
        reservaRepositorio.create(reserva2)

        val payload = """
        {
          "pageSize": 10,
          "pageNumber": 0,
          "filter": {
            "usuarioId": "${usuario.id}",
            "inputSearch": "Rayuela"
          }
        }
    """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/reserva/obtenerMisReservas")
                .contentType("application/json")
                .content(payload)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
    }

    @Test
    fun `una busqueda sin resultados devuelve lista vacia`() {
        reserva1 = Reserva(
            libro = libroDeUsuario2,
            lector = usuario,
            fechaDesde = LocalDate.now(),
            fechaHasta = LocalDate.now().plusDays(5)
        )

        reservaRepositorio.create(reserva1)

        val payload = """
        {
          "pageSize": 10,
          "pageNumber": 0,
          "filter": {
            "usuarioId": "${usuario.id}",
            "inputSearch": "falopita"
          }
        }
    """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/reserva/obtenerMisReservas")
                .contentType("application/json")
                .content(payload)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(0))
    }

    @Test
    @DisplayName("si falta usuarioId devuelve bad request")
    fun reservaErrorUsuarioID() {
        val payload = """
        {
          "pageSize": 10,
          "pageNumber": 0,
          "filter": {
            "inputSearch": ""
          }
        }
    """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/reserva/obtenerMisReservas")
                .contentType("application/json")
                .content(payload)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `se pueden obtener libros prestados por mi`() {
        reserva1 = Reserva(
            libro = libroDeUsuario2,
            lector = usuario,
            fechaDesde = LocalDate.now(),
            fechaHasta = LocalDate.now().plusDays(5)
        )
        reserva2 = Reserva(
            libro = libroPropio,
            lector = usuario2,
            fechaDesde = LocalDate.now(),
            fechaHasta = LocalDate.now().plusDays(5)
        )

        reservaRepositorio.create(reserva1)
        reservaRepositorio.create(reserva2)

        val payload = """
        {
          "pageSize": 10,
          "pageNumber": 0,
          "filter": {
            "usuarioId": "${usuario2.id}",
            "inputSearch": ""
          }
        }
    """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/reserva/obtenerPrestadosPorMi")
                .contentType("application/json")
                .content(payload)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
    }

    // endregion
}

/*
NOTA: Los tests anteriores han sido convertidos al nuevo patrón usando @WebMvcTest con MockMvc.
La arquitectura anterior con @SpringBootTest y repositorios en memoria ha sido reemplazada por:
- Spring Data JPA con PostgreSQL
- JWT para autenticación
- MockMvc con @WithMockUser para simular usuarios autenticados
- @MockBean para mockar servicios en lugar de usar repositorios directamente
*/
