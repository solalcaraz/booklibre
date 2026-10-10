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
        // El feed es una interface de GraphQL: DGS necesita saber qué tipo concreto es cada evento
        return when (actividad.tipoEvento) {
            "ALTA_LIBRO" -> "NuevoLibroEvento"
            "NUEVA_RESERVA" -> "NuevaReservaEvento"
            else -> throw RuntimeException("Tipo de actividad desconocido")
        }
    }
}
