package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Resenia
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
        val usuario2 = usuarioRepositorio.getById(1)
        val usuario3 = usuarioRepositorio.getById(3)
        val libro1 = libroRepositorio.getById(1)
        val libro3 = libroRepositorio.getById(3)
        val libro4 = libroRepositorio.getById(4)

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
        val reserva3 = Reserva(
            libro = libro4,
            usuario = usuario,
            fechaDesde = LocalDate.of(2026, 3, 4),
            fechaHasta = LocalDate.of(2026, 3, 7),
        )
        reserva3.agregarReseña(4, "Libro buenardo")

        val reserva4 = Reserva(
            libro = libro4,
            usuario = usuario2,
            fechaDesde = LocalDate.of(2026, 3, 8),
            fechaHasta = LocalDate.of(2026, 3, 10),
        )
        reserva4.agregarReseña(3, "Lo lei y no lo entendi")

        val reserva5 = Reserva(
            libro = libro4,
            usuario = usuario3,
            fechaDesde = LocalDate.of(2026, 3, 11),
            fechaHasta = LocalDate.of(2026, 3, 12),
        )
        reserva5.agregarReseña(3, "eemmmmmm mmmm mmmm")

        reservaRepositorio.create(reserva1)
        reservaRepositorio.create(reserva2)
        reservaRepositorio.create(reserva3)
        reservaRepositorio.create(reserva4)
        reservaRepositorio.create(reserva5)

        println("Reservas cargadas en memoria")
    }
}