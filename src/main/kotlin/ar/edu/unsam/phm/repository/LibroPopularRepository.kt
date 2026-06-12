package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroPopular
import org.springframework.data.keyvalue.core.KeyValueOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface LibroPopularRepository : CrudRepository<LibroPopular, String>, LibroPopularRepositoryCustom {
    companion object {
        const val RANKING_KEY = "LibroPopular:rankingClicks"
    }
}

interface LibroPopularRepositoryCustom {
    fun findTop5ByClicksDesc(): List<LibroPopular>
    fun saveAllWithRanking(libros: List<LibroPopular>): List<LibroPopular>
    fun rebuildRanking(libros: List<LibroPopular>)
}

@Repository
class LibroPopularRepositoryCustomImpl(
    private val redisTemplate: StringRedisTemplate,
    private val keyValueOperations: KeyValueOperations
) : LibroPopularRepositoryCustom {
    override fun findTop5ByClicksDesc(): List<LibroPopular> {
        val idsTop5 = redisTemplate.opsForZSet()
            .reverseRangeWithScores(LibroPopularRepository.RANKING_KEY, 0, 4)
            ?.map { it.value }
            ?.filterNotNull()
            ?: emptyList()

        return idsTop5.mapNotNull { id ->
            keyValueOperations.findById(id, LibroPopular::class.java).orElse(null)
        }
    }

    override fun saveAllWithRanking(libros: List<LibroPopular>): List<LibroPopular> {
        keyValueOperations.insert(libros)
        rebuildRanking(libros)
        return libros
    }

    override fun rebuildRanking(libros: List<LibroPopular>) {
        redisTemplate.delete(LibroPopularRepository.RANKING_KEY)
        libros
            .filter { it.clicks > 0 }
            .forEach { libro ->
                redisTemplate.opsForZSet()
                    .add(LibroPopularRepository.RANKING_KEY, libro.libroId, libro.clicks.toDouble())
            }
    }
}
