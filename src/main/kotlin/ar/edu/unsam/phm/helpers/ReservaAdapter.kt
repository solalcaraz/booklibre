package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.ReservaSimulada
import ar.edu.unsam.phm.dto.*
import org.springframework.stereotype.Service

interface ReservaAdapter {
    fun toDto(reserva: Reserva): ReservaDTO
    fun toDetalleReservaDto(simulacion: ReservaSimulada): DetalleReservaDTO
}

@Service
class ReservaMapper(private val libroMapper: LibroAdapter): ReservaAdapter {
    override fun toDto(reserva: Reserva): ReservaDTO {
        val libroResumen = LibroResumen(reserva.libro.data.titulo, reserva.libro.data.autor, 4.5, reserva.libro.data.portada)
        return ReservaDTO( reserva.id, libroResumen, reserva.lector.nombreCompleto,
            240, reserva.fechaDesde, reserva.fechaHasta, reserva.calcularEstado(), reserva.puedeCalificar(), reserva.resenia?.puntuacion)
    }

    override fun toDetalleReservaDto(simulacion: ReservaSimulada): DetalleReservaDTO {
        return DetalleReservaDTO(
            bibliokarmasTotales = simulacion.bibliokarmas,
            calificacionPromedio = simulacion.promedio,
            ultimasResenias = simulacion.ultimasReservas.map { reserva ->
                val resenia = reserva.resenia!!
                ReseniaDTO(
                    id = reserva.id,
                    nombreUsuario = resenia.nombreUsuario,
                    puntuacion = resenia.puntuacion,
                    comentario = resenia.comentario
                )
            },
            rangosFechasOcupadas = simulacion.reservasOcupadas.map { reservaOcupada ->
                RangoFechaDTO(
                    fechaDesde = reservaOcupada.fechaDesde,
                    fechaHasta = reservaOcupada.fechaHasta
                )
            },
            libroDetalleDTO = libroMapper.toDetalleDto(simulacion.reserva.libro)
        )
    }
}
