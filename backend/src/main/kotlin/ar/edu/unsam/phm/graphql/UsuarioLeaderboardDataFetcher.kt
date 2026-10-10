package ar.edu.unsam.phm.graphql

import ar.edu.unsam.phm.dto.UsuarioLeaderboardDTO
import ar.edu.unsam.phm.service.UsuarioLeaderboardRedisService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery

@DgsComponent
class UsuarioLeaderboardDataFetcher(
    private val usuarioLeaderboardRedisService: UsuarioLeaderboardRedisService
) {
    @DgsQuery
    fun leaderboardUsuarios(): List<UsuarioLeaderboardDTO> =
        usuarioLeaderboardRedisService.obtenerTop5()
}