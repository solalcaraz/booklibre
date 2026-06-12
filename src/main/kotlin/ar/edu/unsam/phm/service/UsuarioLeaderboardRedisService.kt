package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioLeaderboardDTO
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class UsuarioLeaderboardRedisService(
    private val usuarioRepository: UsuarioRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val LEADERBOARD_KEY = "usuarios:leaderboard"
    private val LEADERBOARD_TTL = Duration.ofMinutes(5)

    fun obtenerTop5(): List<UsuarioLeaderboardDTO> {
        val zsetOps = redisTemplate.opsForZSet()

        // Intenta leer del ZSET: reverseRange devuelve top 5 en orden descendente por score
        val topIds = zsetOps.reverseRange(LEADERBOARD_KEY, 0, 4)?.toList() ?: emptyList()

        // Si hay datos en Redis, recupera desde DB manteniendo orden
        if (topIds.isNotEmpty()) return mapFromDB(topIds)

        // Cache miss: calcula desde DB y guarda en Redis
        return cargarDesdeDB()
    }

    private fun cargarDesdeDB(): List<UsuarioLeaderboardDTO> {
        val topUsuarios = usuarioRepository.findTop5ByOrderByBibliokarmasDesc()

        // Borra el ranking viejo
        redisTemplate.delete(LEADERBOARD_KEY)

        // Carga Redis ZSET con bibliokarmas como score (descendente en reverseRange)
        topUsuarios.forEach { usuario ->
            redisTemplate.opsForZSet().add(
                LEADERBOARD_KEY,
                usuario.id.toString(),
                usuario.bibliokarmas.toDouble()
            )
        }

        // Establece TTL para expiración automática
        redisTemplate.expire(LEADERBOARD_KEY, LEADERBOARD_TTL)

        return topUsuarios.map { it.toDTO() }
    }

    private fun mapFromDB(ids: List<String>): List<UsuarioLeaderboardDTO> {
        // Obtiene usuarios del DB usando los IDs del ZSET
        val usuarios = usuarioRepository.findAllById(
            ids.map { it.toInt() }
        ).associateBy { it.id }

        // Mantiene el orden del ZSET (que ya está ordenado descendentemente)
        return ids.mapNotNull { id ->
            usuarios[id.toInt()]?.toDTO()
        }
    }

    // Invalida caché cuando se actualiza un usuario (llamado desde ReservaService)
    fun invalidarLeaderboard() {
        redisTemplate.delete(LEADERBOARD_KEY)
    }
}

private fun Usuario.toDTO() = UsuarioLeaderboardDTO(
    id = this.id!!,
    nombreCompleto = this.nombreCompleto,
    bibliokarmas = this.bibliokarmas
)
