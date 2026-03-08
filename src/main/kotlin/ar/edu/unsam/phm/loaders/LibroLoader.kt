package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.Date

@Component
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
        libroRepositorio.create(libro1)
        libroRepositorio.create(libro2)
    }
}