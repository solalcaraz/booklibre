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
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate

@Service
class LibroPopularRedisService(
    private val libroPopularRepository: LibroPopularRepository,
    private val clickLibroRepository: ClickLibroRepository,
    private val libroDocumentRepository: LibroDocumentRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
){
    private val RANKING_KEY="libros:ranking"
    private val CACHE_PREFIX = "libro:cache"
    private val CACHE_TTL = Duration.ofMinutes(10)

    // Si Redis esta vacía (reinicio del servidor o primer levantamiento de la app),
    // pregunta a mongo por los libros e hidrata redis, sigue siendo la fuente de verdad
    @EventListener(ApplicationReadyEvent::class)
    fun inicializarRedis() {
        //val librosEnRedis = libroPopularRepository.findAll().toList()
        //if (librosEnRedis.isEmpty())
            reconstruirDesdeDB()
    }

    // consulta en mongo los libros mas clickeados y guarda la info en dos estructuras
    // HASH (libroPopularRepository): la info completa que necesia el home del libro
    // ZSET (libros:ranking): solo el id del libro con su scrore que es la cantidad de clicks para poder ordenarlos
    private fun reconstruirDesdeDB(): List<LibroPopular> {
        // cuales son los libros que mas clicks tiene? puede no llegar al top 10
        val topClicks = clickLibroRepository.top10LibrosMasClickeados()
        if (topClicks.isEmpty()) return emptyList()

        // ahora anda y busca el detalle completo de cada libro "popular" en mongo
        val topConIds = topClicks.map { it.libroId }
        val librosConClicks = libroDocumentRepository.findByIdInAndActivoTrueAndDisponible(
            topConIds, LocalDate.now(), LocalDate.now()).associateBy { it.id }

        val librosParaRedis = topClicks.mapNotNull { click ->
            val libro = librosConClicks[click.libroId] ?: return@mapNotNull null
            cachearLibro(libro)
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
        println("Se construyó la cache de redis con un total de: ${libroPopularRepository.findAll().toList().size} libros populares")
        return librosParaRedis
    }

    // Devuelve solo los id del top segun el zset, sin traer el detalle de los libros
    // sirve para verificar si tenemos la informacion de tal id, es mas liviano traer ids a memoria
    fun obtenerTopIds(limite: Int): List<String> {
        return redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, limite-1.toLong())
            ?.toList() ?: emptyList()
    }

    /*
    obtenerLibroCacheado y cachearLibro trabajan con otra estructura:
    claves "libro:cache:<id>" que son strings json con TTL de 10 minutos
    independiente del hash y el ranking
    hago esto porque quiero que la clase libro popular mantenga persistencia con el ranking pero que expire la informacion hasheada
    esta tercer clase permite justamente eso y el costo a pagar es armar el ttl manual
     */
    // intenta obtener el libro en redis por su id -> devuelve null si expiro su ttl o no existe
    fun obtenerLibroCacheado(libroId: String): LibroPopular? {
        val key = "$CACHE_PREFIX$libroId"
        val json = redisTemplate.opsForValue().get(key) ?: return null

        return objectMapper.readValue(json, LibroPopular::class.java)
    }

    // cachea un libro de mongo con un TTL: se llama cada vez q mongo responde y busca libros
    fun cachearLibro(libro: LibroDocument) {
        val libroPopular = LibroPopular(
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
        val key = "$CACHE_PREFIX${libro.id}"
        val json = objectMapper.writeValueAsString(libroPopular)

        redisTemplate.opsForValue().set(key, json, CACHE_TTL)
    }

    // registrar click actualiza el score en el ZSET
    fun registrarClick(libroId: String) {
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, libroId, 1.0) ?: return

        val rank = redisTemplate.opsForZSet().reverseRank(RANKING_KEY, libroId) ?: return

        //carga libro a redis solo si todavia no esta cargado (o sea ingresa al ranking)
        if (!libroPopularRepository.existsById(libroId) && rank < 10) hidratar(libroId)
    }

    // carga el libro de mongo a redis porque entro en el ranking del top 10
    private fun hidratar(libroId: String) {
        val libro = libroDocumentRepository.findById(libroId).orElse(null) ?: return

        libroPopularRepository.save(LibroPopular(
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
        ))
    }

    private fun obtenerTipo(libro: LibroDocument): String =
        when (libro) {
            is LibroComunDocument -> TipoDeLibroDTO.COMUN.label
            is LibroDedicatoriaDocument -> TipoDeLibroDTO.DEDICATORIA.label
            is LibroColeccionableDocument -> TipoDeLibroDTO.COLECCIONABLE.label
            else -> "DESCONOCIDO"
        }
}