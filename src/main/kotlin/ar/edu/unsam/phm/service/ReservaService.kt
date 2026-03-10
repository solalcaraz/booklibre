package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.repository.ReservaRepositorio
import org.springframework.stereotype.Service

@Service
class ReservaService (private val reservaRepositorio: ReservaRepositorio) {
    //Todo: filtrar segun si la reserva es mia o no y por titulo etc
    fun getMisReservas(page: Int, size: Int): PaginationResponseDTO<Reserva> {
        val reservas = reservaRepositorio.getAll()
        return paginate(reservas, page, size)
    }

    fun getPrestados(page: Int, size: Int): PaginationResponseDTO<Reserva> {
        val reservas = reservaRepositorio.getAll()
        return paginate(reservas, page, size)
    }
}
