package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.LibroColeccionableDocument
import ar.edu.unsam.phm.domain.LibroComunDocument
import ar.edu.unsam.phm.domain.LibroDedicatoriaDocument
import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.domain.LibroPopular
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.repository.ClickLibroRepository
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.LibroPopularRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class LibroPopularRedisService(
    private val libroPopularRepository: LibroPopularRepository,
    private val clickLibroRepository: ClickLibroRepository,
    private val libroDocumentRepository: LibroDocumentRepository,
    private val redisTemplate: RedisTemplate<String, String>
){
    private val RANKING_KEY="libros:ranking"

    // Si Redis esta vacía (reinicio del servidor o primer levantamiento de la app), pregunta a mongo por los libros e hidrata redis, sigue siendo la fuente de verdad
    @EventListener(ApplicationReadyEvent::class)
    fun inicializarRedis() {
        val librosEnRedis = libroPopularRepository.findAll().toList()
        if (librosEnRedis.isEmpty()) reconstruirDesdeDB()
    }

    private fun reconstruirDesdeDB(): List<LibroPopular> {
        // cuales son los libros que mas clicks tiene? puede no llegar al top 10
        val topClicks = clickLibroRepository.top10LibrosMasClickeados()
        if (topClicks.isEmpty()) return emptyList()
        val topConIds = topClicks.map { it.libroId }

        // ahora anda y busca el detalle completo de cada libro "popular" en mongo
        val librosConClicks = libroDocumentRepository.findByIdInAndActivoTrue(topConIds).associateBy { it.id }

        val librosParaRedis = topClicks.mapNotNull { click ->
            val libro = librosConClicks[click.libroId] ?: return@mapNotNull null

            LibroPopular(
                libroId = libro.id!!,
                titulo = libro.titulo,
                autor = libro.autor,
                tipo = obtenerTipo(libro),
                genero = libro.genero.label,
                idioma = libro.idioma.label,
                estado = libro.estado.label,
                dueno = libro.dueno.nombreCompleto,
                isbn13 = libro.isbn13,
                portada = libro.portada,
                calificacionPromedio = libro.promedioCalificacion
            )
        }

        libroPopularRepository.saveAll(librosParaRedis)
        // se guarda el ID del libro con su puntaje de clicks en el ZSET
        topClicks.forEach { libro ->
            redisTemplate.opsForZSet().add(RANKING_KEY, libro.libroId, libro.clicks.toDouble())
        }
        println("Redis vacio. Se construyó con un total de: ${libroPopularRepository.findAll().toList().size} libros populares")
        return librosParaRedis
    }

    private fun obtenerTipo(libro: LibroDocument): String =
        when (libro) {
            is LibroComunDocument -> TipoDeLibroDTO.COMUN.label
            is LibroDedicatoriaDocument -> TipoDeLibroDTO.DEDICATORIA.label
            is LibroColeccionableDocument -> TipoDeLibroDTO.COLECCIONABLE.label
            else -> "DESCONOCIDO"
        }

    // registrar click actualiza el score en el ZSET
    fun registrarClick(libroId: String) {
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, libroId, 1.0) ?: return

        val rank = redisTemplate.opsForZSet().reverseRank(RANKING_KEY, libroId) ?: return

        if (rank < 10){
            //carga libro a redis solo si todavia no esta cargado (o sea ingresa al ranking)
            if (!libroPopularRepository.existsById(libroId)){
                hidratar(libroId)
            }
            sacarLibroDelTop()
        }
    }
    private fun hidratar(libroId: String) {
        val libro = libroDocumentRepository.findById(libroId).orElse(null) ?: return

        LibroPopular(
            libroId = libro.id!!,
            titulo = libro.titulo,
            autor = libro.autor,
            tipo = obtenerTipo(libro),
            genero = libro.genero.label,
            idioma = libro.idioma.label,
            estado = libro.estado.label,
            dueno = libro.dueno.nombreCompleto,
            isbn13 = libro.isbn13,
            portada = libro.portada,
            calificacionPromedio = libro.promedioCalificacion
        )
    }

    private fun sacarLibroDelTop() {
        // Obtiene todos los IDs que quedaron fuera del top 10
        val totalEnZSet = redisTemplate.opsForZSet().size(RANKING_KEY) ?: return
        if (totalEnZSet > 10) {
            // cuales estan afuera del top y los elimina de redis
            val afuera = redisTemplate.opsForZSet().range(RANKING_KEY, 0, totalEnZSet - 10 - 1)
            afuera?.forEach { libroId ->
                libroPopularRepository.deleteById(libroId)
                redisTemplate.opsForZSet().remove(RANKING_KEY, libroId)
            }
        }
    }

    // Devuelve los ids del top segun el zset, ordenados por scrore descendiente (clicks).
    fun obtenerTopLibrosOrdenados(desde: Long, hasta: Long): List<LibroPopular> {
        val ids = redisTemplate.opsForZSet().reverseRange(RANKING_KEY, desde, hasta)
            ?.toList() ?: emptyList()

        val libros = libroPopularRepository.findAllById(ids).associateBy { it.libroId }

        return ids.mapNotNull { libros[it]}
    }
}