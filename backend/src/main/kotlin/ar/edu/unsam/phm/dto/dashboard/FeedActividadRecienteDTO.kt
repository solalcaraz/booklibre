package ar.edu.unsam.phm.dto.dashboard

data class ActividadRecienteDTO(
    val timestamp: Long,
    val fecha: String = timestamp.toString(),
    val tipoEvento: String,
    val usuario: String,

    // Solo para ALTA_LIBRO
    val libroId: String? = null,
    val titulo: String? = null,

    // Solo para NUEVA_RESERVA
    val reservaId: Int? = null
)