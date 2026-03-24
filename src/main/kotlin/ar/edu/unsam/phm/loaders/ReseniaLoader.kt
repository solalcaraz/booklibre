package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.repository.ReseniaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(3)
class ReseniaLoader(
    private val reseniaRepositorio: ReseniaRepositorio,
    private val usuarioRepositorio: UsuarioRepositorio,
): CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario3 = usuarioRepositorio.getById(3)
        val usuario2 = usuarioRepositorio.getById(2)

        val resenia1 = Resenia(1, usuario2.nombreCompleto,
            4, "Libro buenardo",)
        val resenia2 = Resenia(1, usuario3.nombreCompleto,
            5, "Epico",)
        val resenia3 = Resenia(2, usuario2.nombreCompleto,
            3, "Lo lei y no lo entendi")

        reseniaRepositorio.create(resenia1)
        reseniaRepositorio.create(resenia2)
        reseniaRepositorio.create(resenia3)

        println("Resenias cargadas en memoria")
    }
}