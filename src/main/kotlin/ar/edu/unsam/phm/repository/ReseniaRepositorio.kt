package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Resenia
import org.springframework.stereotype.Component

class ReseniaSearchStrategy : SearchStrategy<Resenia> {
    override fun matches(obj: Resenia, value: String): Boolean {
        return obj.comentario.contains(value)
    }
}

@Component
class ReseniaRepositorio : Repositorio<Resenia>(searchStrategy = ReseniaSearchStrategy()) {
    fun getReseniasPorLibro(libroId: Int): List<Resenia> {
        return objetos.values.filter { it.libroId == libroId }
    }
}