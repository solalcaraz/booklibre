package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.LibroResumen
import ar.edu.unsam.phm.dto.ReservaDTO
import org.springframework.stereotype.Service

interface ReservaAdapter {
    fun toDto(reserva: Reserva): ReservaDTO
}

@Service
class ReservaMapper: ReservaAdapter {
    override fun toDto(reserva: Reserva): ReservaDTO {
        val libroResumen = LibroResumen(reserva.libro.titulo, reserva.libro.autor, 4.5, reserva.libro.getImagenUrl())
        return ReservaDTO(  libroResumen, reserva.usuario.nombreCompleto, 240, reserva.fechaDesde, reserva.fechaHasta)
    }
}
