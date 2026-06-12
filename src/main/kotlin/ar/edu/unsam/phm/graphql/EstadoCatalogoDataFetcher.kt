package ar.edu.unsam.phm.graphql

import ar.edu.unsam.phm.dto.dashboard.EstadoSaludCatalogoDTO
import ar.edu.unsam.phm.service.DashboardGraphQLService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class EstadoCatalogoDataFetcher(
    private val tableroAdminService: DashboardGraphQLService
) {
    @DgsQuery
    @PreAuthorize("hasRole('PUBLICADOR') or hasRole('LECTOR_PUBLICADOR')")
    fun estadoSaludCatalogo(): EstadoSaludCatalogoDTO {
        return tableroAdminService.estadoSaludCatalogo()
    }
}
