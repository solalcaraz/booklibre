package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.ReservaSimulada
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

    //Creo un dataClass para obtener toda la info necesaria para el DTO.
        fun getDetalleReserva(libroId: Int, userId:Int, fechaDesde: LocalDate, fechaHasta: LocalDate): ReservaSimulada {
            val libro = libroRepositorio.getById(libroId)
            val usuario = usuarioRepositorio.getById(userId)

            //Reserva simulada con fechas del front
            val reserva = Reserva(
                libro= libro,
                lector = usuario,
                fechaDesde = fechaDesde,
                fechaHasta = fechaHasta,
            )
            val cantidadReservas = reservaRepositorio.buscarPorLibro(libro).size
            val biblioKarmas = reserva.calcularBiblioKarmas(cantidadReservas)
            val promedio = reservaRepositorio.getPuntacionPromedio(libro)
            val ultimasReservas = reservaRepositorio.getUltimasReservas(libro)
            val reservasOcupadas = reservaRepositorio.getReservasOcupadas(libro)

            return ReservaSimulada(
                reserva = reserva,
                cantidadReservasHistoricas = cantidadReservas,
                promedio = promedio,
                ultimasReservas = ultimasReservas,
                reservasOcupadas = reservasOcupadas,
                bibliokarmas = biblioKarmas
            )
        }
}
