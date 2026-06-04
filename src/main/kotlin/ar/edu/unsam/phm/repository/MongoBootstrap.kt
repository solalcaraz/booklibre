package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.LibroColeccionableDocument
import ar.edu.unsam.phm.domain.LibroComunDocument
import ar.edu.unsam.phm.domain.LibroDedicatoriaDocument
import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.ReservaPendiente
import ar.edu.unsam.phm.domain.UsuarioLibroDocument
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class LibrosBootstrap(
    private val libroDocumentRepository: LibroDocumentRepository,
    private val usuarioRepository: UsuarioRepository,
    private val reservaRepository: ReservaRepository
) : InitializingBean {

    fun LibroDocumentRepository.createIfNotExists(libro: LibroDocument): LibroDocument {
        val id = libro.id ?: return this.insert(libro)
        return this.findById(id).orElseGet { this.insert(libro) }
    }

    fun init() {
        val usuariosPostgres = usuarioRepository.findAll()

        if (usuariosPostgres.isEmpty()) {
            println("ATENCIÓN: No hay usuarios en Postgres. No se pueden cargar los libros en Mongo.")
            println("Asegurate de correr el Bootstrap de Usuarios (Postgres) antes que este.")
            return
        }

        val usuariosParaMongo = usuariosPostgres.map { usuario ->
            UsuarioLibroDocument(
                id = usuario.id,
                nombreCompleto = usuario.nombreCompleto,
                imagen = usuario.imagen
            )
        }

        val dueno1 = usuariosParaMongo.firstOrNull { it.id == 1 } ?: usuariosParaMongo.first() // Diego Maradona
        val dueno2 = usuariosParaMongo.firstOrNull { it.id == 2 } ?: usuariosParaMongo.first() // Max Verstappen
        val dueno3 = usuariosParaMongo.firstOrNull { it.id == 3 } ?: usuariosParaMongo.first() // Ye West
        //val dueno4 = usuariosParaMongo.getOrElse(4) { usuariosParaMongo.first() } // Yiya Murano

        val libros = listOf(
            // Libro 1: COLECCIONABLE, usuario 1
            LibroColeccionableDocument().apply {
                id = "libro_001"
                titulo = "Orgullo y Prejuicio"
                descripcion = "Novela romántica clásica"
                genero = Genero.ROMANCE
                autor = "Jane Austen"
                cantPaginas = 300
                isbn13 = "9780141439518"
                idioma = Idioma.INGLES
                editorial = "Penguin"
                fechaPublicacion = LocalDate.of(1800, 1, 1)
                estado = Estado.MUY_BUENO
                portada = "http://localhost:9000/imagenes/orgullo.webp"
                activo = true
                promedioCalificacion = 4.5
                dueno = dueno1  // Diego Maradona
                createdAt = LocalDateTime.now().minusDays(15)
            },
            // Libro 2: DEDICATORIA, usuario 1
            LibroDedicatoriaDocument().apply {
                id = "libro_002"
                titulo = "Rayuela"
                descripcion = "Novela experimental latinoamericana"
                genero = Genero.LITERATURA_CLASICA
                autor = "Julio Cortázar"
                cantPaginas = 736
                isbn13 = "9788437604947"
                idioma = Idioma.ESPANOL
                editorial = "Cátedra"
                fechaPublicacion = LocalDate.of(1960, 1, 1)
                estado = Estado.BUENO
                portada = "http://localhost:9000/imagenes/Rayuela.jpg"
                activo = true
                promedioCalificacion = 3.5
                dueno = dueno1
                createdAt = LocalDateTime.now().minusDays(12)
            },
            // Libro 3: COLECCIONABLE, usuario 1
            LibroColeccionableDocument().apply {
                id = "libro_003"
                titulo = "1984"
                descripcion = "Distopía política sobre vigilancia y control social"
                genero = Genero.CIENCIA_FICCION
                autor = "George Orwell"
                cantPaginas = 328
                isbn13 = "9780451524935"
                idioma = Idioma.INGLES
                editorial = "Signet Classics"
                fechaPublicacion = LocalDate.of(1900, 1, 1)
                estado = Estado.MUY_BUENO
                portada = "http://localhost:9000/imagenes/1984.webp"
                activo = true
                dueno = dueno1
                createdAt = LocalDateTime.now().minusDays(10)
            },
            // Libro 4: COLECCIONABLE, usuario 2
            LibroColeccionableDocument().apply {
                id = "libro_004"
                titulo = "Dune"
                descripcion = "Épica de ciencia ficción sobre política, religión y ecología"
                genero = Genero.CIENCIA_FICCION
                autor = "Frank Herbert"
                cantPaginas = 688
                isbn13 = "9780441172719"
                idioma = Idioma.INGLES
                editorial = "Ace"
                fechaPublicacion = LocalDate.of(1900, 1, 1)
                estado = Estado.EXCELENTE
                portada = "http://localhost:9000/imagenes/dune.jpg"
                activo = true
                dueno = dueno2  // Max Verstappen
                createdAt = LocalDateTime.now().minusDays(8)
            },
            // Libro 5: DEDICATORIA, usuario 2
            LibroDedicatoriaDocument().apply {
                id = "libro_005"
                titulo = "Cien Años de Soledad"
                descripcion = "Obra maestra del realismo mágico latinoamericano"
                genero = Genero.LITERATURA_CLASICA
                autor = "Gabriel García Márquez"
                cantPaginas = 417
                isbn13 = "9780307474728"
                idioma = Idioma.ESPANOL
                editorial = "Sudamericana"
                fechaPublicacion = LocalDate.of(1900, 1, 1)
                estado = Estado.MUY_BUENO
                portada = "http://localhost:9000/imagenes/100anos.webp"
                activo = true
                dueno = dueno2
                createdAt = LocalDateTime.now().minusDays(5)
            },
            // Libro 6: COMUN, usuario 2
            LibroComunDocument().apply {
                id = "libro_006"
                titulo = "Hábitos Atómicos"
                descripcion = "Guía práctica para construir buenos hábitos y eliminar los malos"
                genero = Genero.AUTOAYUDA
                autor = "James Clear"
                cantPaginas = 320
                isbn13 = "9780735211292"
                idioma = Idioma.INGLES
                editorial = "Avery"
                fechaPublicacion = LocalDate.of(1900, 1, 1)
                estado = Estado.BUENO
                portada = "http://localhost:9000/imagenes/habitos.webp"
                activo = true
                dueno = dueno2
                createdAt = LocalDateTime.now().minusDays(3)
            },
            // Libro 7: COLECCIONABLE, usuario 3
            LibroColeccionableDocument().apply {
                id = "libro_007"
                titulo = "El Nombre del Viento"
                descripcion = "Primera parte de la Crónica del Asesino de Reyes"
                genero = Genero.CIENCIA_FICCION
                autor = "Patrick Rothfuss"
                cantPaginas = 662
                isbn13 = "9780756404741"
                idioma = Idioma.INGLES
                editorial = "DAW Books"
                fechaPublicacion = LocalDate.of(1900, 1, 1)
                estado = Estado.EXCELENTE
                portada = "http://localhost:9000/imagenes/viento.webp"
                activo = true
                dueno = dueno3  // Ye West
                createdAt = LocalDateTime.now().minusDays(2)
            },
            // Libro 8: DEDICATORIA, usuario 3
            LibroDedicatoriaDocument().apply {
                id = "libro_008"
                titulo = "El Gran Gatsby"
                descripcion = "Retrato de la alta sociedad estadounidense en los años 20"
                genero = Genero.LITERATURA_CLASICA
                autor = "F. Scott Fitzgerald"
                cantPaginas = 180
                isbn13 = "9780743273565"
                idioma = Idioma.INGLES
                editorial = "Scribner"
                fechaPublicacion = LocalDate.of(1900, 1, 1)
                estado = Estado.BUENO
                portada = "http://localhost:9000/imagenes/libro-nuevo.jpg"
                activo = true
                dueno = dueno3
                createdAt = LocalDateTime.now().minusDays(1)
            },
            // Libro 9: COMUN, usuario 2
            LibroComunDocument().apply {
                id = "libro_009"
                titulo = "El Principito"
                descripcion = "Clásico de la literatura sobre la amistad y la vida"
                genero = Genero.CIENCIA_FICCION
                autor = "Antoine de Saint-Exupéry"
                cantPaginas = 96
                isbn13 = "9780156013987"
                idioma = Idioma.FRANCES
                editorial = "Reynal & Hitchcock"
                fechaPublicacion = LocalDate.of(1943, 4, 6)
                estado = Estado.BUENO
                portada = "https://image.cdn1.buscalibre.com/5b57fc1690f0b5295a8b4567.RS500x500.jpg"
                activo = true
                dueno = dueno2
                createdAt = LocalDateTime.now().minusHours(20)
            },
            // Libro 10: COMUN, usuario 2
            LibroComunDocument().apply {
                id = "libro_010"
                titulo = "1984"
                descripcion = "Distopía sobre un régimen totalitario"
                genero = Genero.CIENCIA_FICCION
                autor = "George Orwell"
                cantPaginas = 328
                isbn13 = "9780451524935"
                idioma = Idioma.INGLES
                editorial = "Secker & Warburg"
                fechaPublicacion = LocalDate.of(1949, 6, 8)
                estado = Estado.BUENO
                portada = "http://localhost:9000/imagenes/1984.webp"
                activo = true
                dueno = dueno2
                createdAt = LocalDateTime.now().minusHours(15)
            },
            // Libro 11: COMUN, usuario 2
            LibroComunDocument().apply {
                id = "libro_011"
                titulo = "Clean Code"
                descripcion = "Buenas prácticas para escribir código limpio"
                genero = Genero.DISENO
                autor = "Robert C. Martin"
                cantPaginas = 464
                isbn13 = "9780132350884"
                idioma = Idioma.INGLES
                editorial = "Prentice Hall"
                fechaPublicacion = LocalDate.of(2008, 8, 1)
                estado = Estado.BUENO
                portada = "https://m.media-amazon.com/images/I/81Rnac2Fq+L._AC_UF1000,1000_QL80_.jpg"
                activo = true
                dueno = dueno2
                createdAt = LocalDateTime.now().minusHours(10)
            },
            // Libro 12: COMUN, usuario 2
            LibroComunDocument().apply {
                id = "libro_012"
                titulo = "El Señor de los Anillos"
                descripcion = "Épica aventura en la Tierra Media"
                genero = Genero.LITERATURA_CLASICA
                autor = "J.R.R. Tolkien"
                cantPaginas = 1178
                isbn13 = "9780618640157"
                idioma = Idioma.INGLES
                editorial = "Allen & Unwin"
                fechaPublicacion = LocalDate.of(1954, 7, 29)
                estado = Estado.BUENO
                portada = "https://image.cdn0.buscalibre.com/5b5ae93e4463b568748b4567.RS500x500.jpg"
                activo = true
                dueno = dueno2
                createdAt = LocalDateTime.now().minusHours(5)
            },
            // Libro 13: COMUN, usuario 2
            LibroComunDocument().apply {
                id = "libro_013"
                titulo = "Padre Rico Padre Pobre"
                descripcion = "Educación financiera básica"
                genero = Genero.AUTOAYUDA
                autor = "Robert Kiyosaki"
                cantPaginas = 336
                isbn13 = "9781612680194"
                idioma = Idioma.INGLES
                editorial = "Plata Publishing"
                fechaPublicacion = LocalDate.of(1997, 4, 1)
                estado = Estado.BUENO
                portada = "https://www.edicontinente.com.ar/image/titulos/9788466373005.jpg"
                activo = true
                dueno = dueno2
                createdAt = LocalDateTime.now().minusHours(2)
            },
            // Libro 14: COMUN, usuario 2
            LibroComunDocument().apply {
                id = "libro_014"
                titulo = "Sapiens"
                descripcion = "Historia breve de la humanidad"
                genero = Genero.LITERATURA_CLASICA
                autor = "Yuval Noah Harari"
                cantPaginas = 443
                isbn13 = "9780062316097"
                idioma = Idioma.INGLES
                editorial = "Harper"
                fechaPublicacion = LocalDate.of(2011, 1, 1)
                estado = Estado.BUENO
                portada = "https://images.cdn1.buscalibre.com/fit-in/360x360/57/3d/573d2d38b86840ce6c919c61ac6d217f.jpg"
                activo = true
                dueno = dueno2
                createdAt = LocalDateTime.now().minusMinutes(15)
            }
        )

        //libros.forEach { libroDocumentRepository.createIfNotExists(it) }
        val reservasPostgres = reservaRepository.findAll()
        libros.forEach { libro ->
            val reservasDelLibro = reservasPostgres.filter { it.libroMongoId == libro.id }
            reservasDelLibro.forEach { reservaPostgres ->
                libro.agregarReserva(
                    ReservaPendiente(
                        idReserva = reservaPostgres.id!!,
                        fechaInicio = reservaPostgres.fechaDesde,
                        fechaFin = reservaPostgres.fechaHasta
                    )
                )
            }

            libroDocumentRepository.createIfNotExists(libro)
        }

        println("✅ Libros inicializados y sincronizados con las reservas de Postgres correctamente.")
    }

    override fun afterPropertiesSet() {
        println("************************************************************************")
        println("Running Mongo initialization")
        println("************************************************************************")
        init()
    }
}