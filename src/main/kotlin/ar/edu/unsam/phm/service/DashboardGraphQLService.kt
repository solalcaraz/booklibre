package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.dto.dashboard.PromedioCalificacionPorTipo
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import org.springframework.stereotype.Service

@Service
class DashboardGraphQLService(
    private val libroRepository: LibroDocumentRepository
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

    private fun tipoDesdeAliasMongo(alias: String): TipoDeLibroDTO =
        when (alias.substringAfterLast(".")) {
            "COMUN", "LibroComunDocument" -> TipoDeLibroDTO.COMUN
            "DEDICATORIA", "LibroDedicatoriaDocument" -> TipoDeLibroDTO.DEDICATORIA
            "COLECCIONABLE", "LibroColeccionableDocument" -> TipoDeLibroDTO.COLECCIONABLE
            else -> throw IllegalArgumentException("Tipo de libro desconocido: $alias")
        }
}
