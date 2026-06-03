package ar.edu.unsam.phm.graphql

import ar.edu.unsam.phm.dto.dashboard.TasaConversionLibroDTO
import ar.edu.unsam.phm.service.DashboardGraphQLService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery

@DgsComponent
class TasaConversionDataFetcher(
    private val dashboardGraphQLService: DashboardGraphQLService
) {
    @DgsQuery
    fun tasaConversionClicksReservas(): List<TasaConversionLibroDTO> =
        dashboardGraphQLService.tasaConversionClicksReservas()
}
