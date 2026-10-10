package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.dashboard.UsuarioLeaderboardDTO
import ar.edu.unsam.phm.repository.UsuarioRepository
import jakarta.annotation.PostConstruct
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

    @PostConstruct
    fun initLeaderboard() {
        cargarDesdeDB()
    }

    fun obtenerTop5(): List<UsuarioLeaderboardDTO> {
        val zsetOps = redisTemplate.opsForZSet()

        // El ZSET solo guarda ids y puntaje: nombre y bibliokarmas se leen de Postgres en el orden del ranking
        val topIds = zsetOps.reverseRange(LEADERBOARD_KEY, 0, 4)?.toList() ?: emptyList()

        if (topIds.isNotEmpty()) return mapFromDB(topIds)

        return cargarDesdeDB()
    }

    private fun cargarDesdeDB(): List<UsuarioLeaderboardDTO> {
        val topUsuarios = usuarioRepository.findTop5ByOrderByBibliokarmasDesc()

        redisTemplate.delete(LEADERBOARD_KEY)

        topUsuarios.forEach { usuario ->
            redisTemplate.opsForZSet().add(
                LEADERBOARD_KEY,
                usuario.id.toString(),
                usuario.bibliokarmas.toDouble()
            )
        }

        redisTemplate.expire(LEADERBOARD_KEY, LEADERBOARD_TTL)

        return topUsuarios.map { it.toDTO() }
    }

    private fun mapFromDB(ids: List<String>): List<UsuarioLeaderboardDTO> {
        val usuarios = usuarioRepository.findAllById(
            ids.map { it.toInt() }
        ).associateBy { it.id }

        return ids.mapNotNull { id ->
            usuarios[id.toInt()]?.toDTO()
        }
    }

    fun actualizarScoreUsuario(userId: Int, bibliokarmas: Int) {
        redisTemplate.opsForZSet().incrementScore(
            LEADERBOARD_KEY,
            userId.toString(),
            bibliokarmas.toDouble()
        )

        redisTemplate.expire(LEADERBOARD_KEY, LEADERBOARD_TTL)
    }
}

private fun Usuario.toDTO() = UsuarioLeaderboardDTO(
    id = this.id!!,
    nombreCompleto = this.nombreCompleto,
    bibliokarmas = this.bibliokarmas
)
