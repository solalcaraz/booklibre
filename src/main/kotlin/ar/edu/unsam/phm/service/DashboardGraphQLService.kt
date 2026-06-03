package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.dto.dashboard.PromedioCalificacionPorTipo
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.dto.dashboard.TasaConversionLibroDTO
import ar.edu.unsam.phm.domain.LibroPopular
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.LibroPopularRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service

@Service
class DashboardGraphQLService(
    private val libroRepository: LibroDocumentRepository,
    private val libroPopularRepository: LibroPopularRepository,
    private val reservaRepository: ReservaRepository
) {
    fun analisisCalificaciones(): List<PromedioCalificacionPorTipo> =
        libroRepository.promedioCalificacionPorTipo()
            .map {
                PromedioCalificacionPorTipo(
                    tipo = tipoDesdeAliasMongo(it.tipo),
                    puntajePromedio = it.puntajePromedio
                )
            }
            .sortedBy { it.tipo.ordinal }

    fun tasaConversionClicksReservas(): List<TasaConversionLibroDTO> {
        val topLibros = libroPopularRepository.findAll()
            .filter { it.clicks > 0 }
            .sortedWith(compareByDescending<LibroPopular> { it.clicks }.thenBy { it.titulo })
            .take(5)

        if (topLibros.isEmpty()) return emptyList()

        val reservasPorLibro = reservaRepository.contarReservasPorLibroMongoIdIn(topLibros.map { it.libroId })
            .associate { it.libroId to it.reservas.toInt() }

        return topLibros.map { libro ->
            val reservas = reservasPorLibro[libro.libroId] ?: 0
            TasaConversionLibroDTO(
                libroId = libro.libroId,
                titulo = libro.titulo,
                autor = libro.autor,
                clicks = libro.clicks,
                reservas = reservas,
                tasaConversion = reservas.toDouble() / libro.clicks
            )
        }
    }

    private fun tipoDesdeAliasMongo(alias: String): TipoDeLibroDTO =
        when (alias.substringAfterLast(".")) {
            "COMUN", "LibroComunDocument" -> TipoDeLibroDTO.COMUN
            "DEDICATORIA", "LibroDedicatoriaDocument" -> TipoDeLibroDTO.DEDICATORIA
            "COLECCIONABLE", "LibroColeccionableDocument" -> TipoDeLibroDTO.COLECCIONABLE
            else -> throw IllegalArgumentException("Tipo de libro desconocido: $alias")
        }
}
