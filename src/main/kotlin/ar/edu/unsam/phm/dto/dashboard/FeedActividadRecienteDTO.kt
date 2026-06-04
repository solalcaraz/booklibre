package ar.edu.unsam.phm.dto.dashboard

import java.time.LocalDateTime

interface ActividadRecienteDTO {
    val fecha: LocalDateTime
    val tipoEvento: String
    val usuario: String
}

data class NuevoLibroEventoDTO(
    override val fecha: LocalDateTime,
    override val tipoEvento: String = "ALTA_LIBRO",
    override val usuario: String, //Dueño
    val libroId: String,
    val titulo: String
) : ActividadRecienteDTO

data class NuevaReservaEventoDTO(
    override val fecha: LocalDateTime,
    override val tipoEvento: String = "NUEVA_RESERVA",
    override val usuario: String, // El usuario que hizo la reserva
    val reservaId: Int
) : ActividadRecienteDTO