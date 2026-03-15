package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate

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

    fun getLibrosDisponibles(usuarioId: Int): Pair<List<Reserva>, List<Int>> {
        val usuario = usuarioRepositorio.getById(usuarioId)

        val librosAjenos = libroRepositorio.getAll().filter { it.dueno.id != usuario.id }
        val librosDisponibles = librosAjenos.filter { !reservaRepositorio.libroEstaPrestado(it) }

        val reservas = librosDisponibles.map { libro ->
            Reserva(
                libro = libro,
                lector = usuario,
                fechaDesde = LocalDate.now(),
                fechaHasta = LocalDate.now()
            )
        }

        val todasLasReservas = reservaRepositorio.getAll()
        val cantidadReservas = librosDisponibles.map { libro -> todasLasReservas.count { it.libro.id == libro.id } }

        return Pair(reservas, cantidadReservas)
    }
}
