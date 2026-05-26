package ar.edu.unsam.phm.controller

/*
Tests desactivados temporalmente por migracion de libros a MongoDB.
Contenido original:

package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.LibroComun
import ar.edu.unsam.phm.domain.LibroData
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.DetalleReservaDTO
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.LibroResumen
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.helpers.ReservaMapper
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.security.CustomUserDetailsService
import ar.edu.unsam.phm.security.TokenHelper
import ar.edu.unsam.phm.service.ReseniaService
import ar.edu.unsam.phm.service.ReservaService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

/*
PatrÃ³n de autenticaciÃ³n:
  - @WithMockUser(username = "1")  â†’  simula un JWT donde el subject es el userId "1"
  - SecurityContextHolder.getContext().authentication.name devuelve "1"
  - Los endpoints protegidos sin esta anotaciÃ³n deben retornar 401.

CSRF:
  El slice @WebMvcTest activa CSRF por defecto aunque en producciÃ³n estÃ© deshabilitado.
  Para mÃ©todos mutantes (POST/PATCH/DELETE) hay que agregar .with(csrf()).
*/
@WebMvcTest(ReservaController::class)
@AutoConfigureMockMvc
@DisplayName("Dado un controller de pre reservas disponibles")
class ReservaControllerTest(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean
    lateinit var reservaService: ReservaService
    @MockBean
    lateinit var reservaMapper: ReservaMapper
    @MockBean
    lateinit var reseniaService: ReseniaService
    @MockBean lateinit var usuarioRepository: UsuarioRepository
    @MockBean
    lateinit var customUserDetailsService: CustomUserDetailsService
    @MockBean
    lateinit var tokenHelper: TokenHelper

    private val dueno = Usuario(
        nombreCompleto = "Lionel Messi",
        email = "lionel@messi.com",
        password = "admin"
    ).apply { id = 2 }

    private val lector = Usuario(
        nombreCompleto = "Diego Maradona",
        email = "diego@maradona.com",
        password = "admin"
    ).apply { id = 1 }

    private val libroData = LibroData(
        titulo = "1984",
        descripcion = "DistopÃ­a",
        genero = Genero.CIENCIA_FICCION,
        autor = "George Orwell",
        cantPaginas = 300,
        isbn13 = "9780451524935",
        idioma = Idioma.INGLES,
        editorial = "Secker & Warburg",
        fechaPublicacion = LocalDate.of(1949, 6, 8),
        estado = Estado.BUENO
    )

    private val libroMock = LibroComun(libroData, dueno).apply { id = 10 }

    private val reservaMock = Reserva(
        libro = libroMock,
        lector = lector,
        fechaDesde = LocalDate.now().plusDays(10),
        fechaHasta = LocalDate.now().plusDays(15)
    ).apply { id = 1 }

    // region GET /librosDisponibles/{userId}
    @Test
    @WithMockUser(username = "1")
    @DisplayName("GET /librosDisponibles - autenticado devuelve 200 con la pÃ¡gina")
    fun obtener_libros_disponibles_ok() {
        val pageDeReservas = PageImpl(listOf(reservaMock), PageRequest.of(0, 6), 1)
        `when`(reservaService.getLibrosDisponiblesParaReserva(any(), any())).thenReturn(pageDeReservas)

        mockMvc.perform(get("/reserva/librosDisponibles"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items[0].titulo").value("1984"))
            .andExpect(jsonPath("$.items[0].autor").value("George Orwell"))
            .andExpect(jsonPath("$.items[0].dueno").value("Lionel Messi"))
            .andExpect(jsonPath("$.totalItems").value(1))
    }

    @Test
    @DisplayName("GET /librosDisponibles - sin autenticar devuelve 401")
    fun obtener_libros_disponibles_sin_autenticar() {
        mockMvc.perform(get("/reserva/librosDisponibles"))
            .andExpect(status().isUnauthorized)
    }
    // endregion

    // region GET /detalles/{libroId}}
    @Test
    @WithMockUser(username = "1")
    @DisplayName("GET /detalles/{libroId} - autenticado con fechas vÃ¡lidas devuelve 200")
    fun obtener_detalles_reserva_ok() {
        val fechaDesde = LocalDate.now().plusDays(10)
        val fechaHasta = LocalDate.now().plusDays(20)

        val detalleDTO = DetalleReservaDTO(
            libroDetalleDTO = LibroDetalleDTO(
                id = 1,
                titulo = "Rayuela",
                tipo = TipoDeLibroDTO.COMUN,
                genero = "Literatura ClÃ¡sica",
                autor = "Julio CortÃ¡zar",
                idioma = "EspaÃ±ol",
                estado = "Bueno",
                editorial = "CÃ¡tedra",
                fechaPublicacion = LocalDate.of(1963, 1, 1).toString(),
                paginas = 300,
                isbn13 = "9788437604947",
                descripcion = "Novela experimental",
                portada = "http://localhost:8080/imagenes/libro-nuevo.jpg"
            ),
            bibliokarmasTotales = 400,
            calificacionPromedio = 4.5,
            ultimasResenias = emptyList(),
            rangosFechasOcupadas = emptyList()
        )

        `when`(reservaService.getDetalleReserva(1, 1, fechaDesde, fechaHasta))
            .thenReturn(detalleDTO)

        mockMvc.perform(
            get("/reserva/detalles/1")
                .param("fechaDesde", fechaDesde.toString())
                .param("fechaHasta", fechaHasta.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.calificacionPromedio").value(4.5))
            .andExpect(jsonPath("$.bibliokarmasTotales").value(400))
            .andExpect(jsonPath("$.libroDetalleDTO.titulo").value("Rayuela"))
    }

    @Test
    @DisplayName("GET /detalles/{libroId} - sin autenticar devuelve 401")
    fun obtener_detalles_sin_autenticar() {
        mockMvc.perform(
            get("/reserva/detalles/1")
                .param("fechaDesde", LocalDate.now().plusDays(1).toString())
                .param("fechaHasta", LocalDate.now().plusDays(5).toString())
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("GET /detalles/{libroId} - fechas invertidas el service lanza BadRequest y devuelve 400")
    fun obtener_detalles_fechas_invertidas() {
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
    // endregion

    // region POST /crear
    @Test
    @WithMockUser(username = "1")
    @DisplayName("POST /crear - autenticado con datos vÃ¡lidos devuelve 200")
    fun crear_reserva_ok() {
        val reservaDTO = ReservaDTO(
            id = 1,
            libroResumen = LibroResumen(
                titulo = "1984",
                autor = "George Orwell",
                puntuacion = 3.0,
                imgUrl = "http://localhost:8080/imagenes/libro-nuevo.jpg"
            ),
            prestadoPor = "Lionel Messi",
            bibliokarma = 250,
            fechaDesde = LocalDate.now().plusDays(10),
            fechaHasta = LocalDate.now().plusDays(15),
            estado = "ACTIVO",
            puedeCalificar = false
        )

        `when`(reservaService.crearReserva(any(), any())).thenReturn(reservaDTO)

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

        verify(reservaService).crearReserva(any(), any())
    }

    @Test
    @DisplayName("POST /crear - sin autenticar devuelve 401")
    fun crear_reserva_sin_autenticar() {
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
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("POST /crear - fechas superpuestas el service lanza ConflictException y devuelve 409")
    fun crear_reserva_fechas_superpuestas() {
        `when`(reservaService.crearReserva(any(), any()))
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
    @DisplayName("POST /crear - ya tiene reserva activa del mismo libro devuelve 409")
    fun crear_reserva_ya_existe_activa() {
        `when`(reservaService.crearReserva(any(), any()))
            .thenThrow(ConflictException("Ya tenÃ©s una reserva activa para este libro. DejÃ¡ leer a los demÃ¡s chimwe"))

        mockMvc.perform(
            post("/reserva/crear")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "libroId": 1,
                      "fechaDesde": "${LocalDate.now().plusDays(30)}",
                      "fechaHasta": "${LocalDate.now().plusDays(40)}"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isConflict)
    }
    // endregion

    // POST /calificar
    // CALIFICAR
    @Test
    @WithMockUser(username = "1")
    @DisplayName("POST /calificar - autenticado con datos vÃ¡lidos devuelve 200")
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
    @DisplayName("POST /calificar - sin autenticar devuelve 401")
    fun calificar_reserva_sin_autenticar() {
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
            .andExpect(status().isUnauthorized)
    }

    // MIS RESERVAS
    // POST /obtenerMisReservas
    @Test
    @WithMockUser(username = "1")
    @DisplayName("POST /obtenerMisReservas - autenticado devuelve 200 con lista paginada")
    fun obtener_mis_reservas_ok() {
        // El service devuelve Page<Reserva>; el mapper lo convierte a PaginationResponseDTO<ReservaDTO>
        val pageDeReservas = PageImpl(listOf(reservaMock), PageRequest.of(0, 10), 1)

        val libroResumen = LibroResumen(
            titulo = "1984",
            autor = "George Orwell",
            puntuacion = 3,
            imgUrl = "http://localhost:8080/imagenes/libro-nuevo.jpg"
        )
        val reservaDTO = ReservaDTO(
            id = 1,
            libroResumen = libroResumen,
            prestadoPor = "Lionel Messi",
            bibliokarma = 250,
            fechaDesde = LocalDate.now().plusDays(10),
            fechaHasta = LocalDate.now().plusDays(15),
            estado = "ACTIVO",
            puedeCalificar = false
        )

        `when`(reservaService.getMisReservas(any())).thenReturn(pageDeReservas)
        `when`(reservaMapper.toDtoReservaPag(pageDeReservas)).thenReturn(
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
            .andExpect(jsonPath("$.items[0].prestadoPor").value("Lionel Messi"))
            .andExpect(jsonPath("$.items[0].libroResumen.titulo").value("1984"))
    }

    @Test
    @DisplayName("POST /obtenerMisReservas - sin autenticar devuelve 401")
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

    @Test
    @WithMockUser(username = "1")
    @DisplayName("POST /obtenerMisReservas - falta usuarioId el service lanza BadRequest y devuelve 400")
    fun obtener_mis_reservas_sin_usuario_id() {
        `when`(reservaService.getMisReservas(any()))
            .thenThrow(BadRequestException("Falta el ID del usuario en el payload"))

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
                        "inputSearch": ""
                      }
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
    }

    // POST /obtenerPrestadosPorMi
    @Test
    @WithMockUser(username = "1")
    @DisplayName("POST /obtenerPrestadosPorMi - autenticado devuelve 200")
    fun obtener_prestados_por_mi_ok() {
        // Page<Reserva> vacÃ­a â€” el controller/mapper la convierte a lista vacÃ­a de DTOs
        val pageVacia = PageImpl(listOf<Reserva>(), PageRequest.of(0, 10), 0)

        `when`(reservaService.getPrestadosPorMi(any())).thenReturn(pageVacia)
        `when`(reservaMapper.toDtoReservaPag(pageVacia)).thenReturn(
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
            .andExpect(jsonPath("$.items.length()").value(0))
    }

    @Test
    @DisplayName("POST /obtenerPrestadosPorMi - sin autenticar devuelve 401")
    fun obtener_prestados_por_mi_sin_autenticar() {
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
            .andExpect(status().isUnauthorized)
    }
}

/*
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
}*/
*/

