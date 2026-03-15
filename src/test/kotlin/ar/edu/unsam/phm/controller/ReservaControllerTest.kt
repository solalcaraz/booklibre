package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
    @Autowired lateinit var libroRepositorio: LibroRepositorio
    @Autowired lateinit var usuarioRepositorio: UsuarioRepositorio
    @Autowired lateinit var reservaRepositorio: ReservaRepositorio

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

        libroPropio = Libro(
            titulo = "Libro Propio",
            tipo = Comun,
            descripcion = "Este libro es del usuario que busca",
            genero = Genero.DRAMA,
            autor = "Autor Cualquiera",
            cantPaginas = 100,
            isbn13 = "1111111111111",
            idioma = Idioma.ESPANOL,
            editorial = "Editorial",
            fechaPublicacion = LocalDate.of(2000, 1, 1),
            estado = Estado.BUENO,
            dueno = usuario
        )

        libroDeUsuario2 = Libro(
            titulo = "Rayuela",
            tipo = Dedicatoria,
            descripcion = "Novela experimental",
            genero = Genero.LITERATURA_CLASICA,
            autor = "Julio Cortázar",
            cantPaginas = 736,
            isbn13 = "9788437604947",
            idioma = Idioma.ESPANOL,
            editorial = "Cátedra",
            fechaPublicacion = LocalDate.of(1963, 1, 1),
            estado = Estado.BUENO,
            dueno = usuario2
        )

        libro2DeUsuario2 = Libro(
            titulo = "1984",
            tipo = Comun,
            descripcion = "Distopía",
            genero = Genero.CIENCIA_FICCION,
            autor = "George Orwell",
            cantPaginas = 328,
            isbn13 = "9780451524935",
            idioma = Idioma.INGLES,
            editorial = "Signet",
            fechaPublicacion = LocalDate.of(1949, 1, 1),
            estado = Estado.MUY_BUENO,
            dueno = usuario2
        )

        libroRepositorio.create(libroPropio)
        libroRepositorio.create(libroDeUsuario2)
        libroRepositorio.create(libro2DeUsuario2)
    }

    // region GET /librosDisponibles/{userId}

    @Test
    fun `se pueden obtener libros disponibles para un usuario`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/librosDisponibles/${usuario.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.items.length()").value(2))
            .andExpect(jsonPath("$.items.[0].titulo").value("Rayuela"))
    }

    @Test
    fun `no se muestran los libros propios del usuario`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/librosDisponibles/${usuario2.id}"))
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
                fechaDesde = LocalDate.now(),
                fechaHasta = LocalDate.now().plusDays(7)
            )
        )
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/librosDisponibles/${usuario.id}")
                    .param("fechaDesde", LocalDate.now().toString())
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
                MockMvcRequestBuilders.get("/librosDisponibles/${usuario.id}")
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
                MockMvcRequestBuilders.get("/librosDisponibles/${usuario.id}")
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
                MockMvcRequestBuilders.get("/librosDisponibles/${usuario.id}")
                    .param("busqueda", "Tolkien")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(0))
    }

    @Test
    fun `un usuario inexistente devuelve 404`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/librosDisponibles/9999"))
            .andExpect(status().isNotFound)
    }

// PAGINADO
    @Test
    fun `la primera pagina devuelve el tamanio correcto`() {
        // agregue otro libro para usuario2 , muestra dos libros
        // pagino para un solo libro asi me aparece uno en cada pagina
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/librosDisponibles/${usuario.id}")
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
                MockMvcRequestBuilders.get("/librosDisponibles/${usuario.id}")
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
                MockMvcRequestBuilders.get("/librosDisponibles/${usuario.id}")
                    .param("numeroPagina", "99")
                    .param("tamanoPaginado", "10")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(0))
            .andExpect(jsonPath("$.totalItems").value(2))
    }

    // endregion
}