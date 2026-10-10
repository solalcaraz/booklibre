package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.LibroPopular
import ar.edu.unsam.phm.repository.ClickLibroRepository
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.LibroPopularRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDate

data class LibroPopularConClicks(
    val libro: LibroPopular,
    val clicks: Int
)

@Service
class LibroPopularRedisService(
    private val libroPopularRepository: LibroPopularRepository,
    private val clickLibroRepository: ClickLibroRepository,
    private val libroDocumentRepository: LibroDocumentRepository,
    private val redisTemplate: RedisTemplate<String, String>,
){
    private val RANKING_KEY="libros:ranking"

    // Si Redis arranca vacío (primer arranque o reinicio) se reconstruye desde Mongo,
    // que sigue siendo la fuente de verdad
    @EventListener(ApplicationReadyEvent::class)
    fun inicializarRedis() {
        val yaInicializado = redisTemplate.opsForZSet().size(RANKING_KEY) ?: 0
        if (yaInicializado == 0L) reconstruirDesdeDB()
    }

    // Se guardan dos estructuras: un ZSET con id y cantidad de clicks (el ranking ordenado)
    // y un hash con TTL por libro con los datos que muestra el home
    private fun reconstruirDesdeDB(): List<LibroPopular> {
        val topClicks = clickLibroRepository.top10LibrosMasClickeados()
        if (topClicks.isEmpty()) return emptyList()

        val topConIds = topClicks.map { it.libroId }
        val librosConClicks = libroDocumentRepository.findByIdInAndActivoTrueAndDisponible(
            topConIds, LocalDate.now(), LocalDate.now()).associateBy { it.id }

        val librosParaRedis = topClicks.mapNotNull { click -> librosConClicks[click.libroId]?.toLibroPopular() }

        libroPopularRepository.saveAll(librosParaRedis)
        topClicks.forEach { libro ->
            redisTemplate.opsForZSet().add(RANKING_KEY, libro.libroId, libro.clicks.toDouble())
        }
        println("Se construyó la cache de redis con un total de: ${libroPopularRepository.findAll().toList().size} libros populares")
        return librosParaRedis
    }

    fun obtenerTopIds(limite: Int): List<String> {
        return redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, limite-1L)
            ?.toList() ?: emptyList()
    }

    fun obtenerTopLibrosConClicks(limite: Int): List<LibroPopularConClicks> {
        return redisTemplate.opsForZSet()
            .reverseRangeWithScores(RANKING_KEY, 0, limite - 1L)
            ?.mapNotNull { libroConScore ->
                val libroId = libroConScore.value ?: return@mapNotNull null
                // El hash vence a los 30 segundos pero el ranking no: si expiró, se recachea desde Mongo
                val libro = libroPopularRepository.findById(libroId).orElse(null)
                    ?: libroDocumentRepository.findById(libroId).orElse(null)
                        ?.toLibroPopular()?.also { libroPopularRepository.save(it) }
                    ?: return@mapNotNull null

                LibroPopularConClicks(
                    libro = libro,
                    clicks = libroConScore.score?.toInt() ?: 0
                )
            }
            ?: emptyList()
    }

    fun obtenerLibroPopular(libroId: String): LibroPopular? =
        libroPopularRepository.findById(libroId).orElse(null)

    fun cachearLibro(libro: LibroDocument) {
        libroPopularRepository.save(libro.toLibroPopular())
    }

    fun registrarClick(libroId: String) {
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, libroId, 1.0) ?: return

        val rank = redisTemplate.opsForZSet().reverseRank(RANKING_KEY, libroId) ?: return

        // El detalle se cachea recién cuando el libro entra al top 10
        if (!libroPopularRepository.existsById(libroId) && rank < 10) {
            libroDocumentRepository.findById(libroId).orElse(null)?.let { cachearLibro(it) }
        }
    }

    private fun LibroDocument.toLibroPopular() = LibroPopular(
        libroId = id!!,
        titulo = titulo,
        autor = autor,
        tipo = tipo().label,
        genero = genero.label,
        idioma = idioma.label,
        estado = estado.label,
        dueno = dueno.nombreCompleto,
        isbn13 = isbn13,
        calificacionPromedio = promedioCalificacion,
        cantPaginas = cantPaginas,
        cantidadReservas = cantidadReservas,
        portada = portada
    )
}
