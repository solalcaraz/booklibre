package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.LibroColeccionable
import ar.edu.unsam.phm.domain.LibroComun
import ar.edu.unsam.phm.domain.LibroData
import ar.edu.unsam.phm.domain.LibroDedicatoria
import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Order(2)
class LibroLoader(
    private val libroRepositorio: LibroRepository,
    private val usuarioRepository: UsuarioRepository
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario = usuarioRepository.findById(1) // dueno de libro 1 2 3
            .orElseThrow { NotFoundException("Usuario no encontrado") }
        val usuario2 = usuarioRepository.findById(2) // dueno de libro 4 5 6 9 10 11 12 13 14
            .orElseThrow { NotFoundException("Usuario no encontrado") }
        val usuario3 = usuarioRepository.findById(3) // dueno de libro 7 8
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        val libro1 = LibroColeccionable(
            data = LibroData(
                titulo = "Orgullo y Prejuicio",
                descripcion = "Novela romántica clásica",
                genero = Genero.ROMANCE,
                autor = "Jane Austen",
                cantPaginas = 300,
                isbn13 = "9780141439518",
                idioma = Idioma.INGLES,
                editorial = "Penguin",
                fechaPublicacion = LocalDate.of(1800, 1, 1),
                estado = Estado.MUY_BUENO,
            ), usuario
        ).apply {
            data.portada = Environment.API_URL + "/imagenes/orgullo.webp"
        }

        val libro2 = LibroDedicatoria(
            data = LibroData(
                titulo = "Rayuela",
                descripcion = "Novela experimental latinoamericana",
                genero = Genero.LITERATURA_CLASICA,
                autor = "Julio Cortázar",
                cantPaginas = 736,
                isbn13 = "9788437604947",
                idioma = Idioma.ESPANOL,
                editorial = "Cátedra",
                fechaPublicacion = LocalDate.of(1960, 1, 1),
                estado = Estado.BUENO,
            ), usuario
        ).apply {
            data.portada = Environment.API_URL + "/imagenes/Rayuela.jpg"
        }

        val libro3 = LibroColeccionable(
            data = LibroData(
                titulo = "1984",
                descripcion = "Distopía política sobre vigilancia y control social",
                genero = Genero.CIENCIA_FICCION,
                autor = "George Orwell",
                cantPaginas = 328,
                isbn13 = "9780451524935",
                idioma = Idioma.INGLES,
                editorial = "Signet Classics",
                fechaPublicacion = LocalDate.of(1900, 1, 1),
                estado = Estado.MUY_BUENO,
            ), usuario
        ).apply {
            data.portada = Environment.API_URL + "/imagenes/1984.webp"
        }

        val libro4 = LibroColeccionable(
            data = LibroData(
                titulo = "Dune",
                descripcion = "Épica de ciencia ficción sobre política, religión y ecología",
                genero = Genero.CIENCIA_FICCION,
                autor = "Frank Herbert",
                cantPaginas = 688,
                isbn13 = "9780441172719",
                idioma = Idioma.INGLES,
                editorial = "Ace",
                fechaPublicacion = LocalDate.of(1900, 1, 1),
                estado = Estado.EXCELENTE,
            ), usuario2
        ).apply {
            data.portada = Environment.API_URL + "/imagenes/dune.jpg"
        }

        val libro5 = LibroDedicatoria(
            data = LibroData(
                titulo = "Cien Años de Soledad",
                descripcion = "Obra maestra del realismo mágico latinoamericano",
                genero = Genero.LITERATURA_CLASICA,
                autor = "Gabriel García Márquez",
                cantPaginas = 417,
                isbn13 = "9780307474728",
                idioma = Idioma.ESPANOL,
                editorial = "Sudamericana",
                fechaPublicacion = LocalDate.of(1900, 1, 1),
                estado = Estado.MUY_BUENO,
            ), usuario2
        ).apply {
            data.portada = Environment.API_URL + "/imagenes/100anos.webp"
        }

        val libro6 = LibroComun(
            data = LibroData(
                titulo = "Hábitos Atómicos",
                descripcion = "Guía práctica para construir buenos hábitos y eliminar los malos",
                genero = Genero.AUTOAYUDA,
                autor = "James Clear",
                cantPaginas = 320,
                isbn13 = "9780735211292",
                idioma = Idioma.INGLES,
                editorial = "Avery",
                fechaPublicacion = LocalDate.of(1900, 1, 1),
                estado = Estado.BUENO,
            ), usuario2
        ).apply {
            data.portada = Environment.API_URL + "/imagenes/habitos.webp"
        }

        val libro7 = LibroColeccionable(
            data = LibroData(
                titulo = "El Nombre del Viento",
                descripcion = "Primera parte de la Crónica del Asesino de Reyes",
                genero = Genero.CIENCIA_FICCION,
                autor = "Patrick Rothfuss",
                cantPaginas = 662,
                isbn13 = "9780756404741",
                idioma = Idioma.INGLES,
                editorial = "DAW Books",
                fechaPublicacion = LocalDate.of(1900, 1, 1),
                estado = Estado.EXCELENTE,
            ), usuario3
        ).apply {
            data.portada = Environment.API_URL + "/imagenes/viento.webp"
        }

        val libro8 = LibroDedicatoria(
            data = LibroData(
                titulo = "El Gran Gatsby",
                descripcion = "Retrato de la alta sociedad estadounidense en los años 20",
                genero = Genero.LITERATURA_CLASICA,
                autor = "F. Scott Fitzgerald",
                cantPaginas = 180,
                isbn13 = "9780743273565",
                idioma = Idioma.INGLES,
                editorial = "Scribner",
                fechaPublicacion = LocalDate.of(1900, 1, 1),
                estado = Estado.BUENO,
            ), usuario3
        ). apply {
            /*data.portada = Environment.API_URL + "/imagenes/gatsby.jpg"*/
        }

        val libro9 = LibroComun(
            data = LibroData(
                titulo = "El Principito",
                descripcion = "Clásico de la literatura sobre la amistad y la vida",
                genero = Genero.CIENCIA_FICCION,
                autor = "Antoine de Saint-Exupéry",
                cantPaginas = 96,
                isbn13 = "9780156013987",
                idioma = Idioma.FRANCES,
                editorial = "Reynal & Hitchcock",
                fechaPublicacion = LocalDate.of(1943, 4, 6),
                estado = Estado.BUENO,
            ), usuario2
        ).apply {
            data.portada = "https://image.cdn1.buscalibre.com/5b57fc1690f0b5295a8b4567.RS500x500.jpg"
        }

        val libro10 = LibroComun(
            data = LibroData(
                titulo = "1984",
                descripcion = "Distopía sobre un régimen totalitario",
                genero = Genero.CIENCIA_FICCION,
                autor = "George Orwell",
                cantPaginas = 328,
                isbn13 = "9780451524935",
                idioma = Idioma.INGLES,
                editorial = "Secker & Warburg",
                fechaPublicacion = LocalDate.of(1949, 6, 8),
                estado = Estado.BUENO,
            ), usuario2
        ).apply {
            data.portada = Environment.API_URL + "/imagenes/1984.webp"
        }

        val libro11 = LibroComun(
            data = LibroData(
                titulo = "Clean Code",
                descripcion = "Buenas prácticas para escribir código limpio",
                genero = Genero.DISENO,
                autor = "Robert C. Martin",
                cantPaginas = 464,
                isbn13 = "9780132350884",
                idioma = Idioma.INGLES,
                editorial = "Prentice Hall",
                fechaPublicacion = LocalDate.of(2008, 8, 1),
                estado = Estado.BUENO,
            ), usuario2
        ).apply {
            data.portada = "https://m.media-amazon.com/images/I/81Rnac2Fq+L._AC_UF1000,1000_QL80_.jpg"
        }

        val libro12 = LibroComun(
            data = LibroData(
                titulo = "El Señor de los Anillos",
                descripcion = "Épica aventura en la Tierra Media",
                genero = Genero.LITERATURA_CLASICA,
                autor = "J.R.R. Tolkien",
                cantPaginas = 1178,
                isbn13 = "9780618640157",
                idioma = Idioma.INGLES,
                editorial = "Allen & Unwin",
                fechaPublicacion = LocalDate.of(1954, 7, 29),
                estado = Estado.BUENO,
            ), usuario2
        ).apply {
            data.portada = "https://image.cdn0.buscalibre.com/5b5ae93e4463b568748b4567.RS500x500.jpg"
        }

        val libro13 = LibroComun(
            data = LibroData(
                titulo = "Padre Rico Padre Pobre",
                descripcion = "Educación financiera básica",
                genero = Genero.AUTOAYUDA,
                autor = "Robert Kiyosaki",
                cantPaginas = 336,
                isbn13 = "9781612680194",
                idioma = Idioma.INGLES,
                editorial = "Plata Publishing",
                fechaPublicacion = LocalDate.of(1997, 4, 1),
                estado = Estado.BUENO,
            ), usuario2
        ).apply {
            data.portada = "https://www.edicontinente.com.ar/image/titulos/9788466373005.jpg"
        }

        val libro14 = LibroComun(
            data = LibroData(
                titulo = "Sapiens",
                descripcion = "Historia breve de la humanidad",
                genero = Genero.LITERATURA_CLASICA,
                autor = "Yuval Noah Harari",
                cantPaginas = 443,
                isbn13 = "9780062316097",
                idioma = Idioma.INGLES,
                editorial = "Harper",
                fechaPublicacion = LocalDate.of(2011, 1, 1),
                estado = Estado.BUENO,
            ), usuario2
        ).apply {
            data.portada = "https://images.cdn1.buscalibre.com/fit-in/360x360/57/3d/573d2d38b86840ce6c919c61ac6d217f.jpg"
        }

        libroRepositorio.save(libro1)
        libroRepositorio.save(libro2)
        libroRepositorio.save(libro3)
        libroRepositorio.save(libro4)
        libroRepositorio.save(libro5)
        libroRepositorio.save(libro6)
        libroRepositorio.save(libro7)
        libroRepositorio.save(libro8)
        libroRepositorio.save(libro9)
        libroRepositorio.save(libro10)
        libroRepositorio.save(libro11)
        libroRepositorio.save(libro12)
        libroRepositorio.save(libro13)
        libroRepositorio.save(libro14)


        println("Libros cargados en Postgres")
    }
}