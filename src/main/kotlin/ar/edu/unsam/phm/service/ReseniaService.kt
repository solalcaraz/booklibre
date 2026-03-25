package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.repository.ReseniaRepositorio
import org.springframework.stereotype.Service

@Service
class ReseniaService (
    private val reseniaRepositorio: ReseniaRepositorio
) {
    fun getMapaPromedioPorLibro(librosIds: List<Int>): Map<Int, Double> {
        return librosIds.associateWith { libroId -> getPromedioPorLibro(libroId) }
    }

    fun getPromedioPorLibro(libroId: Int): Double {
        val resenias = reseniaRepositorio.getReseniasPorLibro(libroId)
        if (resenias.isEmpty()){
            return 0.0
        }
        return resenias.sumOf { it.puntuacion }.toDouble() / resenias.size
    }
}
