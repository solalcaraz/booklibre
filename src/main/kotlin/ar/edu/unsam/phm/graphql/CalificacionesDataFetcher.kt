package ar.edu.unsam.phm.graphql

import ar.edu.unsam.phm.dto.dashboard.PromedioCalificacionPorTipo
import ar.edu.unsam.phm.service.DashboardGraphQLService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery

@DgsComponent
class CalificacionesDataFetcher(
    private val tableroAdminService: DashboardGraphQLService
) {
    @DgsQuery
    fun analisisCalificaciones(): List<PromedioCalificacionPorTipo> =
        tableroAdminService.analisisCalificaciones()
}
