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
import org.springframework.stereotype.Service

@Service
class LibroPopularidadRedisService(
    private val libroPopularRepository: LibroPopularRepository,
    private val clickLibroRepository: ClickLibroRepository,
    private val libroDocumentRepository: LibroDocumentRepository
){
    // Si Redis esta vacía (reinicio del servidor o primer levantamiento de la app), pregunta a mongo por los libros e hidrata redis, sigue siendo la fuente de verdad
    @EventListener(ApplicationReadyEvent::class)
    fun inicializarRedis() {
        if (libroPopularRepository.findAll().toList().isEmpty()) reconstruirDesdeDB()
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
                calificacionPromedio = libro.promedioCalificacion,
                clicks = click.clicks
            )
        }

        libroPopularRepository.saveAll(librosParaRedis)
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
}