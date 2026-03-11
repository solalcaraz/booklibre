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
        val libro4 = libroRepositorio.getById(4)
        val libro5 = libroRepositorio.getById(5)
        val libro6 = libroRepositorio.getById(6)
        val libro7 = libroRepositorio.getById(7)


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
            fechaHasta = LocalDate.of(2026, 3, 12),
        )
        val reserva3 = Reserva(
            libro = libro4,
            usuario = usuario,
            fechaDesde = LocalDate.of(2026, 3, 8),
            fechaHasta = LocalDate.of(2026, 3, 14),
        )
        val reserva4 = Reserva(
            libro = libro5,
            usuario = usuario,
            fechaDesde = LocalDate.of(2026, 3, 7),
            fechaHasta = LocalDate.of(2026, 3, 10),
        )
        val reserva5 = Reserva(
            libro = libro6,
            usuario = usuario,
            fechaDesde = LocalDate.of(2026, 3, 28),
            fechaHasta = LocalDate.of(2026, 4, 14),
        )
        val reserva6 = Reserva(
            libro = libro7,
            usuario = usuario,
            fechaDesde = LocalDate.of(2026, 3, 7),
            fechaHasta = LocalDate.of(2026, 3, 10),
        )
        val reserva7 = Reserva(
            libro = libro7,
            usuario = usuario,
            fechaDesde = LocalDate.of(2026, 1, 7),
            fechaHasta = LocalDate.of(2026, 1, 14),
        )
        reservaRepositorio.create(reserva1)
        reservaRepositorio.create(reserva2)
        reservaRepositorio.create(reserva3)
        reservaRepositorio.create(reserva4)
        reservaRepositorio.create(reserva5)
        reservaRepositorio.create(reserva6)
        reservaRepositorio.create(reserva7)

        println("Reservas cargadas en memoria")
    }
}