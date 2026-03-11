package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.DetalleLibroDTO
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.toDetalleDTO
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.Int

@Service
class LibroService(
    val libroRepositorio: LibroRepositorio,
    val reservaRepositorio: ReservaRepositorio,
    val usuarioRepository: UsuarioRepositorio
) {
    fun getAll(): List<Libro> = libroRepositorio.getAll()

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
                titulo = libro.titulo,
                autor = libro.autor,
                estadoReserva = estadoReserva,
                genero = libro.genero.toString(),
                agregadoEl = LocalDate.now(),
            )
        }
    }
    fun getDetalle(libroId: Int, usuarioLogueadoId: Int): DetalleLibroDTO{
        //TODO: Ver de enviar todas las fechas que el libro está reservado (usar repoReservas) -> UI
        val libro = libroRepositorio.getById(libroId)
        val usuario = usuarioRepository.getById(usuarioLogueadoId)
        val bibliokarmasBase = libro.calcularBiblioKarmas(0, usuario)
        //TODO: Por el momento todos, falta agregar orden por fecha y solo tomar 2.
        val resenias = reservaRepositorio.buscarPorLibro(libro).mapNotNull { reserva -> reserva.resenia}

        return libro.toDetalleDTO(bibliokarmasBase, resenias)
    }
}