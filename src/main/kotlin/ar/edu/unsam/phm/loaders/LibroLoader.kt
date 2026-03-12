package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Order(2)
class LibroLoader(private val libroRepositorio: LibroRepositorio, private val usuarioRepositorio: UsuarioRepositorio) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario = usuarioRepositorio.getById(1)

        val libro1 = Libro(
            titulo = "Orgullo y Prejuicio",
            tipo = Coleccionable,
            descripcion = "Novela romántica clásica",
            genero = Genero.ROMANCE,
            autor = "Jane Austen",
            cantPaginas = 300,
            isbn13 = "9780141439518",
            idioma = Idioma.INGLES,
            editorial = "Penguin",
            fechaPublicacion = LocalDate.of(1800, 1, 1),
            estado = Estado.MUY_BUENO,
            dueno = usuario
        ).apply {
            portada = Environment.API_URL + "/imagenes/orgullo.webp"
        }

        val libro2 = Libro(
            titulo = "Rayuela",
            tipo = Dedicatoria,
            descripcion = "Novela experimental latinoamericana",
            genero = Genero.LITERATURA_CLASICA,
            autor = "Julio Cortázar",
            cantPaginas = 736,
            isbn13 = "9788437604947",
            idioma = Idioma.ESPANOL,
            editorial = "Cátedra",
            fechaPublicacion = LocalDate.of(1960, 1, 1),
            estado = Estado.BUENO,
            dueno = usuario
        ).apply {
            portada = Environment.API_URL + "/imagenes/Rayuela.jpg"
        }

        val libro3 = Libro(
            titulo = "1984",
            tipo = Coleccionable,
            descripcion = "Distopía política sobre vigilancia y control social",
            genero = Genero.CIENCIA_FICCION,
            autor = "George Orwell",
            cantPaginas = 328,
            isbn13 = "9780451524935",
            idioma = Idioma.INGLES,
            editorial = "Signet Classics",
            fechaPublicacion = LocalDate.of(1900, 1, 1),
            estado = Estado.MUY_BUENO,
            dueno = usuario
        ).apply {
            portada = Environment.API_URL + "/imagenes/1984.webp"
        }

        val libro4 = Libro(
            titulo = "Dune",
            tipo = Coleccionable,
            descripcion = "Épica de ciencia ficción sobre política, religión y ecología",
            genero = Genero.CIENCIA_FICCION,
            autor = "Frank Herbert",
            cantPaginas = 688,
            isbn13 = "9780441172719",
            idioma = Idioma.INGLES,
            editorial = "Ace",
            fechaPublicacion = LocalDate.of(1900, 1, 1),
            estado = Estado.EXCELENTE,
            dueno = usuario
        ).apply {
            portada = Environment.API_URL + "/imagenes/dune.jpg"
        }

        val libro5 = Libro(
            titulo = "Cien Años de Soledad",
            tipo = Dedicatoria,
            descripcion = "Obra maestra del realismo mágico latinoamericano",
            genero = Genero.LITERATURA_CLASICA,
            autor = "Gabriel García Márquez",
            cantPaginas = 417,
            isbn13 = "9780307474728",
            idioma = Idioma.ESPANOL,
            editorial = "Sudamericana",
            fechaPublicacion = LocalDate.of(1900, 1, 1),
            estado = Estado.MUY_BUENO,
            dueno = usuario
        ).apply {
            portada = Environment.API_URL + "/imagenes/100anos.webp"
        }

        val libro6 = Libro(
            titulo = "Hábitos Atómicos",
            tipo = Comun,
            descripcion = "Guía práctica para construir buenos hábitos y eliminar los malos",
            genero = Genero.AUTOAYUDA,
            autor = "James Clear",
            cantPaginas = 320,
            isbn13 = "9780735211292",
            idioma = Idioma.INGLES,
            editorial = "Avery",
            fechaPublicacion = LocalDate.of(1900, 1, 1),
            estado = Estado.BUENO,
            dueno = usuario
        ).apply {
            portada = Environment.API_URL + "/imagenes/habitos.webp"
        }

        val libro7 = Libro(
            titulo = "El Nombre del Viento",
            tipo = Coleccionable,
            descripcion = "Primera parte de la Crónica del Asesino de Reyes",
            genero = Genero.CIENCIA_FICCION,
            autor = "Patrick Rothfuss",
            cantPaginas = 662,
            isbn13 = "9780756404741",
            idioma = Idioma.INGLES,
            editorial = "DAW Books",
            fechaPublicacion = LocalDate.of(1900, 1, 1),
            estado = Estado.EXCELENTE,
            dueno = usuario
        ).apply {
            portada = Environment.API_URL + "/imagenes/viento.webp"
        }

        val libro8 = Libro(
            titulo = "El Gran Gatsby",
            tipo = Dedicatoria,
            descripcion = "Retrato de la alta sociedad estadounidense en los años 20",
            genero = Genero.LITERATURA_CLASICA,
            autor = "F. Scott Fitzgerald",
            cantPaginas = 180,
            isbn13 = "9780743273565",
            idioma = Idioma.INGLES,
            editorial = "Scribner",
            fechaPublicacion = LocalDate.of(1900, 1, 1),
            estado = Estado.BUENO,
            dueno = usuario
        ). apply {
            /*portada = Environment.API_URL + "/imagenes/gatsby.jpg"*/
        }

        libroRepositorio.create(libro1)
        libroRepositorio.create(libro2)
        libroRepositorio.create(libro3)
        libroRepositorio.create(libro4)
        libroRepositorio.create(libro5)
        libroRepositorio.create(libro6)
        libroRepositorio.create(libro7)
        libroRepositorio.create(libro8)

        println("Libros cargados en memoria")
    }
}