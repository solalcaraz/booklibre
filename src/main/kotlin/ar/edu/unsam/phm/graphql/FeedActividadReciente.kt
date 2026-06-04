package ar.edu.unsam.phm.graphql

import ar.edu.unsam.phm.dto.dashboard.ActividadRecienteDTO
import ar.edu.unsam.phm.dto.dashboard.NuevaReservaEventoDTO
import ar.edu.unsam.phm.dto.dashboard.NuevoLibroEventoDTO
import ar.edu.unsam.phm.service.DashboardGraphQLService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.DgsTypeResolver

@DgsComponent
class ActividadRecienteDataFetcher(
    private val dashboardGraphQLService: DashboardGraphQLService
) {
    @DgsQuery
    fun feedActividadReciente(): List<ActividadRecienteDTO> =
        dashboardGraphQLService.feedActividadReciente()

    // Este resolver es clave para que GraphQL sepa qué tipo instanciar
    @DgsTypeResolver(name = "ActividadReciente")
    fun resolveActividadTipo(actividad: ActividadRecienteDTO): String {
        return when (actividad) {
            is NuevoLibroEventoDTO -> "NuevoLibroEvento"
            is NuevaReservaEventoDTO -> "NuevaReservaEvento"
            else -> throw RuntimeException("Tipo de actividad desconocido")
        }
    }
}