package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.dto.toEntity
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.repository.ReservaRepositorio

@Service
class LibroService(
    val libroRepositorio: LibroRepositorio,
    val reservaRepositorio: ReservaRepositorio,
    val usuarioRepositorio: UsuarioRepositorio
) {
    fun getAll(): List<Libro> = libroRepositorio.getAll()

    fun getById(id: Int): Libro = libroRepositorio.getById(id)

    fun create(libro: LibroDetalleDTO, usuarioId: Int) {
        val duenio = usuarioRepositorio.getById(usuarioId)
        val nuevoLibro = libro.toEntity(duenio)
        libroRepositorio.create(nuevoLibro)
    }

    fun update(id: Int, libro: LibroDetalleDTO) {
        val libroExistente = libroRepositorio.getById(id)

        val nuevoTipo = when (libro.tipo) {
            TipoDeLibroDTO.COMUN -> Comun
            TipoDeLibroDTO.DEDICATORIA -> Dedicatoria
            TipoDeLibroDTO.COLECCIONABLE -> Coleccionable
        }

        libroExistente.apply {
            titulo = libro.titulo
            autor = libro.autor
            genero = libro.genero
            idioma = libro.idioma
            estado = libro.estado
            editorial = libro.editorial
            descripcion = libro.descripcion
            cantPaginas = libro.paginas
            isbn13 = libro.isbn13
            fechaPublicacion = LocalDate.parse(libro.fechaPublicacion)
            portada = libro.portada
            tipo = nuevoTipo
        }

        libroRepositorio.update(libroExistente)
    }

    /* Un primer approach a algunos de los filtros que tiene el home */
    /* Sol sentite libre de borrar todo o dejarlo y agregar los filtros que faltan como genero, isbn y tal*/
    fun buscarLibrosDisponibles(
        titulo: String? = null,
        genero: String? = null,
        desde: LocalDate? = null,
        hasta: LocalDate? = null
    ): List<Libro> {
        val librosDisponibles = libroRepositorio.getAll().filter { libro ->
            val reservasDelLibro = reservaRepositorio.buscarPorLibro(libro)

            if (desde != null && hasta != null) {
                reservasDelLibro.none { it.seSuperponeCon(desde, hasta) }
            } else {
                reservasDelLibro.none { it.estaActiva() }
            }
        }

        return librosDisponibles.filter { libro ->
            (titulo == null || libro.titulo.contains(titulo, ignoreCase = true)) &&
                    (genero == null || libro.genero.toString().contains(genero, ignoreCase = true))
        }
    }

    fun misLibros(usuario: Usuario): List<MiLibroDTO> {
        return libroRepositorio.librosDe(usuario).map { libro ->
            val estadoReserva = if (reservaRepositorio.libroEstaPrestado(libro)) {
                "PRESTADO"
            } else {
                "DISPONIBLE"
            }

            MiLibroDTO(
                id = libro.id,
                titulo = libro.titulo,
                autor = libro.autor,
                genero = libro.genero.label,
                estadoReserva = estadoReserva,
                agregadoEl = libro.fechaPublicacion,
                portada = libro.portada
            )
        }
    }
    fun borrarLibro(id: String) {
        libroRepositorio.deleteById(id)
    }
}