package ar.edu.unsam.phm.graphql

import ar.edu.unsam.phm.dto.dashboard.ActividadRecienteDTO
import ar.edu.unsam.phm.service.DashboardGraphQLService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.DgsTypeResolver
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class ActividadRecienteDataFetcher(
    private val dashboardGraphQLService: DashboardGraphQLService
) {
    @DgsQuery
    @PreAuthorize("hasRole('PUBLICADOR') or hasRole('LECTOR_PUBLICADOR')")
    fun feedActividadReciente(): List<ActividadRecienteDTO> =
        dashboardGraphQLService.feedActividadReciente()

    @DgsTypeResolver(name = "ActividadReciente")
    fun resolveActividadTipo(actividad: ActividadRecienteDTO): String {
        // Evaluamos el String que le pusimos al evento para decidir el tipo de GraphQL
        return when (actividad.tipoEvento) {
            "ALTA_LIBRO" -> "NuevoLibroEvento"
            "NUEVA_RESERVA" -> "NuevaReservaEvento"
            else -> throw RuntimeException("Tipo de actividad desconocido")
        }
    }
}
