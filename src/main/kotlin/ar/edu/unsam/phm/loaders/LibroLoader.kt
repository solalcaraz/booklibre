package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.Date

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
            fechaPublicacion = Date(),
            estado = Estado.MUY_BUENO,
            dueno = usuario
        )

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
            fechaPublicacion = Date(),
            estado = Estado.BUENO,
            dueno = usuario
        )

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
            fechaPublicacion = Date(),
            estado = Estado.MUY_BUENO,
            dueno = usuario
        )

        val libro4 = Libro(
            titulo = "El Gran Gatsby",
            tipo = Dedicatoria,
            descripcion = "Retrato de la alta sociedad estadounidense en los años 20",
            genero = Genero.LITERATURA_CLASICA,
            autor = "F. Scott Fitzgerald",
            cantPaginas = 180,
            isbn13 = "9780743273565",
            idioma = Idioma.INGLES,
            editorial = "Scribner",
            fechaPublicacion = Date(),
            estado = Estado.BUENO,
            dueno = usuario
        )

        val libro5 = Libro(
            titulo = "Dune",
            tipo = Coleccionable,
            descripcion = "Épica de ciencia ficción sobre política, religión y ecología",
            genero = Genero.CIENCIA_FICCION,
            autor = "Frank Herbert",
            cantPaginas = 688,
            isbn13 = "9780441172719",
            idioma = Idioma.INGLES,
            editorial = "Ace",
            fechaPublicacion = Date(),
            estado = Estado.EXCELENTE,
            dueno = usuario
        )

        val libro6 = Libro(
            titulo = "Cien Años de Soledad",
            tipo = Dedicatoria,
            descripcion = "Obra maestra del realismo mágico latinoamericano",
            genero = Genero.LITERATURA_CLASICA,
            autor = "Gabriel García Márquez",
            cantPaginas = 417,
            isbn13 = "9780307474728",
            idioma = Idioma.ESPANOL,
            editorial = "Sudamericana",
            fechaPublicacion = Date(),
            estado = Estado.MUY_BUENO,
            dueno = usuario
        )

        val libro7 = Libro(
            titulo = "Hábitos Atómicos",
            tipo = Comun,
            descripcion = "Guía práctica para construir buenos hábitos y eliminar los malos",
            genero = Genero.AUTOAYUDA,
            autor = "James Clear",
            cantPaginas = 320,
            isbn13 = "9780735211292",
            idioma = Idioma.INGLES,
            editorial = "Avery",
            fechaPublicacion = Date(),
            estado = Estado.BUENO,
            dueno = usuario
        )

        val libro8 = Libro(
            titulo = "El Nombre del Viento",
            tipo = Coleccionable,
            descripcion = "Primera parte de la Crónica del Asesino de Reyes",
            genero = Genero.CIENCIA_FICCION,
            autor = "Patrick Rothfuss",
            cantPaginas = 662,
            isbn13 = "9780756404741",
            idioma = Idioma.INGLES,
            editorial = "DAW Books",
            fechaPublicacion = Date(),
            estado = Estado.EXCELENTE,
            dueno = usuario
        )
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