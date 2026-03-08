package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Order(3)
class ReservaLoader(
    private val reservaRepositorio: ReservaRepositorio,
    private val usuarioRepositorio: UsuarioRepositorio,
    private val libroRepositorio: LibroRepositorio
): CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario = usuarioRepositorio.getById(2)
        val libro1 = libroRepositorio.getById(1)
        val libro3 = libroRepositorio.getById(3)

        val reserva1 = Reserva(
            libro = libro1,
            usuario = usuario,
            fechaDesde = LocalDate.of(2026, 3, 7),
            fechaHasta = LocalDate.of(2026, 3, 14),
        )
        val reserva2 = Reserva(
            libro = libro3,
            usuario = usuario,
            fechaDesde = LocalDate.of(2026, 3, 3),
            fechaHasta = LocalDate.of(2026, 3, 10),
        )

        reservaRepositorio.create(reserva1)
        reservaRepositorio.create(reserva2)

        println("Reservas cargadas en memoria")
    }
}