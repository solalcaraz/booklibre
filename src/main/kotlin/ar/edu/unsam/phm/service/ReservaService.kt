package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.BusquedaPor
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.repository.ReservaRepositorio
import org.springframework.stereotype.Service

@Service
class ReservaService (private val reservaRepositorio: ReservaRepositorio) {
    fun getMisReservas(payload: PaginationReqDTO<ReservaRequestDTO>): PaginationResponseDTO<Reserva> {
        var reservas = reservaRepositorio.getAll()
        // filtrado por tab si es prestado por mi o los que preste
        if (payload.filter?.busquedaPor == BusquedaPor.TAB_PRESTADOS_POR_MI.value){
            reservas = reservas.filter { it.libro.dueno.id.toString() == payload.filter.usuarioId }
        } else if (payload.filter?.busquedaPor == BusquedaPor.TAB_PRESTADOS_A_MI.value) {
            reservas = reservas.filter { it.usuario.id.toString() == payload.filter.usuarioId }
        }

        // filtrado por input de busqueda
        val search = payload.filter?.inputSearch?.trim()
        if (!search.isNullOrEmpty()) {
            reservas = reservas.filter {
                it.libro.titulo.contains(search, ignoreCase = true) ||
                        it.libro.autor.contains(search, ignoreCase = true)
            }
        }
        return paginate(reservas, payload.pageNumber, payload.pageSize)
    }
}
