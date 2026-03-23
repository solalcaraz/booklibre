package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.LibroComun
import ar.edu.unsam.phm.domain.LibroData
import ar.edu.unsam.phm.domain.LibroDedicatoria
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un controller de pre reservas disponibles")
class ReservaControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var libroRepositorio: LibroRepositorio

    @Autowired
    lateinit var usuarioRepositorio: UsuarioRepositorio

    @Autowired
    lateinit var reservaRepositorio: ReservaRepositorio

    lateinit var usuario: Usuario
    lateinit var usuario2: Usuario
    lateinit var libroDeUsuario2: Libro
    lateinit var libro2DeUsuario2: Libro
    lateinit var libroPropio: Libro

    @BeforeEach
    fun init() {
        reservaRepositorio.clearInit()
        libroRepositorio.clearInit()
        usuarioRepositorio.clearInit()

        usuario = Usuario(
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password = "admin"
        )
        usuario2 = Usuario(
            nombreCompleto = "Lionel Messi",
            email = "lionel@messi.com",
            password = "admin"
        )
        usuarioRepositorio.create(usuario)
        usuarioRepositorio.create(usuario2)

        libroPropio = LibroComun(
            LibroData(
                titulo = "Libro Propio", descripcion = "Este libro es del usuario que busca",
                genero = Genero.DRAMA, autor = "Autor Cualquiera", cantPaginas = 100,
                isbn13 = "1111111111111", idioma = Idioma.ESPANOL, editorial = "Editorial",
                fechaPublicacion = LocalDate.of(2000, 1, 1), estado = Estado.BUENO, dueno = usuario
            )
        )

        libroDeUsuario2 = LibroDedicatoria(
            LibroData(
                titulo = "Rayuela", descripcion = "Novela experimental",
                genero = Genero.LITERATURA_CLASICA, autor = "Julio Cortázar", cantPaginas = 736,
                isbn13 = "9788437604947", idioma = Idioma.ESPANOL, editorial = "Cátedra",
                fechaPublicacion = LocalDate.of(1963, 1, 1), estado = Estado.BUENO, dueno = usuario2
            )
        )

        libro2DeUsuario2 = LibroComun(
            LibroData(
                titulo = "1984", descripcion = "Distopía",
                genero = Genero.CIENCIA_FICCION, autor = "George Orwell", cantPaginas = 328,
                isbn13 = "9780451524935", idioma = Idioma.INGLES, editorial = "Signet",
                fechaPublicacion = LocalDate.of(1949, 1, 1), estado = Estado.MUY_BUENO, dueno = usuario2
            )
        )

        libroRepositorio.create(libroPropio)
        libroRepositorio.create(libroDeUsuario2)
        libroRepositorio.create(libro2DeUsuario2)
    }

    // region GET /librosDisponibles/{userId}

    @Test
    fun `se pueden obtener libros disponibles para un usuario`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.items.length()").value(2))
            .andExpect(jsonPath("$.items.[0].titulo").value("1984"))
    }

    @Test
    fun `no se muestran los libros propios del usuario`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario2.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items.[0].titulo").value("Libro Propio"))
    }

    @Test
    fun `si no hay libros disponibles se devuelve lista vacia`() {
        // reserva del unico libro disponible para que no aparezca
        reservaRepositorio.create(
            Reserva(
                libro = libroDeUsuario2,
                lector = usuario,
                fechaDesde = LocalDate.now().plusDays(1),
                fechaHasta = LocalDate.now().plusDays(7)
            )
        )
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("fechaDesde", LocalDate.now().plusDays(1).toString())
                    .param("fechaHasta", LocalDate.now().plusDays(7).toString())
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items.[0].titulo").value("1984"))
    }

    @Test
    fun `se pueden filtrar libros por busqueda de titulo`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("busqueda", "Rayuela")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items[0].titulo").value("Rayuela"))
    }

    @Test
    fun `se pueden filtrar libros por busqueda de autor`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("busqueda", "Cortázar")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items.[0].autor").value("Julio Cortázar"))
    }

    @Test
    fun `una busqueda que no matchea devuelve lista vacia`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("busqueda", "Tolkien")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(0))
    }

    @Test
    fun `un usuario inexistente devuelve 404`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/reserva/librosDisponibles/9999"))
            .andExpect(status().isNotFound)
    }

    // PAGINADO
    @Test
    fun `la primera pagina devuelve el tamanio correcto`() {
        // agregue otro libro para usuario2 , muestra dos libros
        // pagino para un solo libro asi me aparece uno en cada pagina
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("numeroPagina", "0")
                    .param("tamanoPaginado", "1")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.totalItems").value(2))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.page").value(0))
    }

    @Test
    fun `la segunda pagina devuelve el resto`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("numeroPagina", "1")
                    .param("tamanoPaginado", "1")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.totalItems").value(2))
            .andExpect(jsonPath("$.page").value(1))
    }

    @Test
    fun `una pagina fuera de rango devuelve items vacio`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("numeroPagina", "99")
                    .param("tamanoPaginado", "10")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(0))
            .andExpect(jsonPath("$.totalItems").value(2))
    }

    // ORDENAMIENTO
    @Test
    fun `orden TITULO_ASC devuelve libros ordenados de la A a la Z`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("orden", "TITULO_ASC")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].titulo").value("1984"))
            .andExpect(jsonPath("$.items[1].titulo").value("Rayuela"))
    }

    @Test
    fun `orden TITULO_DESC devuelve libros ordenados de la Z a la A`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("orden", "TITULO_DESC")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].titulo").value("Rayuela"))
            .andExpect(jsonPath("$.items[1].titulo").value("1984"))
    }

    @Test
    fun `orden AUTOR_ASC devuelve libros ordenados por autor alfabeticamente`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("orden", "AUTOR_ASC")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].autor").value("George Orwell"))
            .andExpect(jsonPath("$.items[1].autor").value("Julio Cortázar"))
    }

    @Test
    fun `orden DUENO_ASC devuelve libros ordenados por nombre del dueno alfabeticamente`() {
        // agrego otro usuario y un libro asociadop para ver que el filtrado funcione
        val usuario3 = Usuario(
            nombreCompleto = "Ana Belgrano",
            email = "ana@belgrano.com",
            password = "admin"
        )
        usuarioRepositorio.create(usuario3)
        libroRepositorio.create(
            LibroComun(
                LibroData(
                    titulo = "El Aleph", descripcion = "Cuentos",
                    genero = Genero.LITERATURA_CLASICA, autor = "Jorge Luis Borges", cantPaginas = 200,
                    isbn13 = "9780142437889", idioma = Idioma.ESPANOL, editorial = "Penguin",
                    fechaPublicacion = LocalDate.of(1949, 1, 1), estado = Estado.BUENO, dueno = usuario3
                )
            )
        )

        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reserva/librosDisponibles/${usuario.id}")
                    .param("orden", "DUENO_ASC")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].dueno").value("Ana Belgrano"))
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
    // endregion
}