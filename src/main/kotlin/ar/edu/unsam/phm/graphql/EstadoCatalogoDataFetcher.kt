package ar.edu.unsam.phm.graphql

import ar.edu.unsam.phm.dto.dashboard.EstadoSaludCatalogoDTO
import ar.edu.unsam.phm.service.DashboardGraphQLService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery

@DgsComponent
class EstadoCatalogoDataFetcher(
    private val tableroAdminService: DashboardGraphQLService
) {
    @DgsQuery
    fun estadoSaludCatalogo(): EstadoSaludCatalogoDTO {
        return tableroAdminService.estadoSaludCatalogo()
    }
}