package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.repository.ReseniaRepositorio
import org.springframework.stereotype.Service

@Service
class ReseniaService (
    private val reseniaRepositorio: ReseniaRepositorio
) {
    fun getPromedioPorLibro(librosIds: List<Int>): Map<Int, Double> {
        return librosIds.associateWith { libroId ->
            val resenias = reseniaRepositorio.getReseniasPorLibro(libroId)
            if (resenias.isEmpty()) 0.0
            else resenias.sumOf { it.puntuacion }.toDouble() / resenias.size
        }
    }
}
