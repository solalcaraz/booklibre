package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.helpers.Ordenamiento
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service

@Service
class ReservaService (
    private val reservaRepositorio: ReservaRepositorio,
    private val libroRepositorio: LibroRepositorio,
    private val usuarioRepositorio: UsuarioRepositorio
) {
    //Todo: filtrar segun si la reserva es mia o no y por titulo etc
    fun getMisReservas(page: Int, size: Int): PaginationResponseDTO<Reserva> {
        val reservas = reservaRepositorio.getAll()
        return paginate(reservas, page, size)
    }

    fun getPrestados(page: Int, size: Int): PaginationResponseDTO<Reserva> {
        val reservas = reservaRepositorio.getAll()
        return paginate(reservas, page, size)
    }

    fun getLibrosDisponibles(usuarioId: Int, filtros: FiltrosLibro): Pair<List<Reserva>, List<Int>> {
        val usuario = usuarioRepositorio.getById(usuarioId)
        // Agrupa los libros por id y cuenta cuantos hay de cada uno.
        val reservasPorLibro= reservaRepositorio.getAll()
            .groupingBy { it.libro.id }
            .eachCount()

        // verifica disponibilidad real con el repo de Reservas, aplica busqueda por teclado, filtros y ordena
        val librosDisponibles =
            (if (filtros.busqueda.isNullOrBlank()) libroRepositorio.getAll() else libroRepositorio.search(filtros.busqueda))
                    .filter { it.dueno.id != usuario.id }
                    .filter { reservaRepositorio.libroEstaDisponibleEntre(it, filtros.fechaDesde, filtros.fechaHasta) }
                    .filter { filtros.aplicarA(it) }
                    .let { libros ->
                        when (filtros.orden) {
                            Ordenamiento.RELEVANCIA -> libros
                            Ordenamiento.TITULO_ASC -> libros.sortedBy { it.titulo.lowercase() }
                            Ordenamiento.TITULO_DESC -> libros.sortedByDescending { it.titulo.lowercase() }
                            Ordenamiento.FECHA_ASC -> libros.sortedBy { it.fechaPublicacion }
                            Ordenamiento.FECHA_DESC -> libros.sortedByDescending { it.fechaPublicacion }
                            Ordenamiento.POPULARIDAD -> libros.sortedByDescending { reservasPorLibro[it.id] ?: 0 } // si no fue reservado le dejo un 0 para garantizar que se traiga igualmente y no quede null
                        }
                    }

        val preReservasDisponibles = librosDisponibles.map { libro ->
                Reserva(
                    libro = libro,
                    lector = usuario,
                    fechaDesde = filtros.fechaDesde,
                    fechaHasta = filtros.fechaHasta
                )
            }

        val cantidadReservas = librosDisponibles.map { reservasPorLibro[it.id] ?: 0 }

        return preReservasDisponibles to cantidadReservas
    }
}
