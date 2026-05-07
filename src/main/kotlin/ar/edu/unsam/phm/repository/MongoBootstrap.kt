package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.LibroColeccionableMongo
import ar.edu.unsam.phm.domain.LibroComunMongo
import ar.edu.unsam.phm.domain.LibroDedicatoriaMongo
import ar.edu.unsam.phm.domain.LibroMongo
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibrosBootstrap(
    private val libroMongoRepository: LibroMongoRepository
) : InitializingBean {

    fun LibroMongoRepository.createIfNotExists(libro: LibroMongo): LibroMongo {
        val bdLibro = this.findByTitulo(libro.titulo)
        return bdLibro ?: this.save(libro)
    }

    fun init() {
        val duenoBootstrap = Usuario(
            id = 1,
            nombreCompleto = "Usuario Bootstrap",
            email = "bootstrap@booklibre.com",
            password = "bootstrap"
        ).apply {
            tipoUsuario = TipoUsuario.PUBLICADOR
        }

        val libros = listOf(
            LibroComunMongo().apply {
                cargarDatosBase(
                    titulo = "El Aleph",
                    autor = "Jorge Luis Borges",
                    genero = Genero.LITERATURA_CLASICA,
                    isbn13 = "9789875666481",
                    duenoBootstrap = duenoBootstrap
                )
            },
            LibroDedicatoriaMongo().apply {
                cargarDatosBase(
                    titulo = "No habra mas penas ni olvido",
                    autor = "Osvaldo Soriano",
                    genero = Genero.DRAMA,
                    isbn13 = "9789507313004",
                    duenoBootstrap = duenoBootstrap
                )
            },
            LibroColeccionableMongo().apply {
                cargarDatosBase(
                    titulo = "La novela de Peron",
                    autor = "Tomas Eloy Martinez",
                    genero = Genero.LITERATURA_CLASICA,
                    isbn13 = "9789500732079",
                    duenoBootstrap = duenoBootstrap
                )
            },
            LibroComunMongo().apply {
                cargarDatosBase(
                    titulo = "100 anios de soledad",
                    autor = "Gabriel Garcia Marquez",
                    genero = Genero.LITERATURA_CLASICA,
                    isbn13 = "9780307474728",
                    duenoBootstrap = duenoBootstrap
                )
            },
            LibroDedicatoriaMongo().apply {
                cargarDatosBase(
                    titulo = "Por quien doblan las campanas",
                    autor = "Ernest Hemingway",
                    genero = Genero.DRAMA,
                    isbn13 = "9780684803357",
                    duenoBootstrap = duenoBootstrap
                )
            }
        )

        libros.forEach { libroMongoRepository.createIfNotExists(it) }
    }

    private fun LibroMongo.cargarDatosBase(
        titulo: String,
        autor: String,
        genero: Genero,
        isbn13: String,
        duenoBootstrap: Usuario
    ) {
        this.titulo = titulo
        this.descripcion = "Libro cargado inicialmente en MongoDB"
        this.genero = genero
        this.autor = autor
        this.cantPaginas = 200
        this.isbn13 = isbn13
        this.idioma = Idioma.ESPANOL
        this.editorial = "BookLibre"
        this.fechaPublicacion = LocalDate.of(2020, 1, 1)
        this.estado = Estado.BUENO
        this.dueno = duenoBootstrap
    }

    override fun afterPropertiesSet() {
        println("************************************************************************")
        println("Running Mongo initialization")
        println("************************************************************************")
        init()
    }
}
