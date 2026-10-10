package ar.edu.unsam.phm.dto.dashboard

data class ActividadRecienteDTO(
    // Campos obligatorios
    val timestamp: Long,
    val fecha: String = timestamp.toString(),
    val tipoEvento: String,
    val usuario: String,

    // OPCIONAL para el evento de Libro
    val libroId: String? = null,
    val titulo: String? = null,

    // OPCIONAL para el evento de Reserva
    val reservaId: Int? = null
)