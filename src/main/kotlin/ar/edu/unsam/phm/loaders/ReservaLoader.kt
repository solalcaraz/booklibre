package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReseniaRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Order(4)
class ReservaLoader(
    private val reservaRepositorio: ReservaRepositorio,
    private val usuarioRepositorio: UsuarioRepositorio,
    private val libroRepositorio: LibroRepositorio,
    private val reseniaRepositorio: ReseniaRepositorio
): CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario = usuarioRepositorio.getById(1)
        val usuario2 = usuarioRepositorio.getById(2)
        val usuario3 = usuarioRepositorio.getById(3)

        val libro1 = libroRepositorio.getById(1)
        val libro2 = libroRepositorio.getById(2)
        val libro3 = libroRepositorio.getById(3)
        val libro4 = libroRepositorio.getById(4)
        val libro5 = libroRepositorio.getById(5)
        val libro6 = libroRepositorio.getById(6)
        val libro7 = libroRepositorio.getById(7)
        val libro8 = libroRepositorio.getById(8)
        val libro9 = libroRepositorio.getById(9)
        val libro10 = libroRepositorio.getById(10)
        val libro11 = libroRepositorio.getById(11)
        val libro12 = libroRepositorio.getById(12)
        val libro13 = libroRepositorio.getById(13)
        val libro14 = libroRepositorio.getById(14)

        val resenia1= reseniaRepositorio.getById(1) //del libro 1
        val resenia2 = reseniaRepositorio.getById(2)//del libro 1
        val resenia3 = reseniaRepositorio.getById(3)//del libro 2

        // maradona u1 = dueno de libro 1 2 3
        // verstappen u2 = dueno de libro 4 5 6 9 10 11 12 13 14
        // u3 = dueno de libro 7 8

        val reserva1 = Reserva(
            libro = libro1,
            lector = usuario2,
            fechaDesde = LocalDate.of(2026, 3, 7),
            fechaHasta = LocalDate.of(2026, 3, 14),
        )
        reserva1.resenia = resenia1
        val reserva2 = Reserva(
            libro = libro2,
            lector = usuario2,
            fechaDesde = LocalDate.of(2026, 3, 3),
            fechaHasta = LocalDate.of(2026, 3, 12),
        )
        reserva2.resenia = resenia3
        val reserva3 = Reserva(
            libro = libro3,
            lector = usuario2,
            fechaDesde = LocalDate.of(2026, 3, 8),
            fechaHasta = LocalDate.of(2026, 3, 14),
        )
        val reserva4 = Reserva(
            libro = libro4,
            lector = usuario,
            fechaDesde = LocalDate.of(2026, 3, 7),
            fechaHasta = LocalDate.of(2026, 5, 25),
        )
        val reserva5 = Reserva(
            libro = libro5,
            lector = usuario,
            fechaDesde = LocalDate.of(2026, 3, 28),
            fechaHasta = LocalDate.of(2026, 4, 14),
        )
        val reserva6 = Reserva(
            libro = libro6,
            lector = usuario,
            fechaDesde = LocalDate.of(2026, 3, 7),
            fechaHasta = LocalDate.of(2026, 3, 10),
        )
        val reserva7 = Reserva(
            libro = libro7,
            lector = usuario2,
            fechaDesde = LocalDate.of(2026, 1, 5),
            fechaHasta = LocalDate.of(2026, 1, 10),
        )
        val reserva8 = Reserva(
            libro = libro8,
            lector = usuario2,
            fechaDesde = LocalDate.of(2026, 1, 10),
            fechaHasta = LocalDate.of(2026, 1, 12),
        )
        val reserva9 = Reserva(
            libro = libro7,
            lector = usuario,
            fechaDesde = LocalDate.of(2026, 3, 20),
            fechaHasta = LocalDate.of(2026, 3, 25),
        )
        val reserva10 = Reserva(
            libro = libro9,
            lector = usuario,
            fechaDesde = LocalDate.of(2026, 3, 17),
            fechaHasta = LocalDate.of(2026, 3, 25)
        )
        val reserva11 = Reserva(
            libro = libro11,
            lector = usuario,
            fechaDesde = LocalDate.of(2026, 3, 10),
            fechaHasta = LocalDate.of(2026, 4, 21)
        )
        val reserva12 = Reserva(
            libro = libro13,
            lector = usuario,
            fechaDesde = LocalDate.of(2026, 2, 24),
            fechaHasta = LocalDate.of(2026, 4, 30)
        )
        val reserva13 = Reserva(
            libro = libro1,
            lector = usuario3,
            fechaDesde = LocalDate.of(2025, 2, 24),
            fechaHasta = LocalDate.of(2025, 4, 30)
        )
        reserva13.resenia = resenia2

        reservaRepositorio.create(reserva1)
        reservaRepositorio.create(reserva2)
        reservaRepositorio.create(reserva3)
        reservaRepositorio.create(reserva4)
        reservaRepositorio.create(reserva5)
        reservaRepositorio.create(reserva6)
        reservaRepositorio.create(reserva7)
        reservaRepositorio.create(reserva8)
        reservaRepositorio.create(reserva9)
        reservaRepositorio.create(reserva10)
        reservaRepositorio.create(reserva11)
        reservaRepositorio.create(reserva12)
        reservaRepositorio.create(reserva13)

        println("Reservas cargadas en memoria")
    }
}