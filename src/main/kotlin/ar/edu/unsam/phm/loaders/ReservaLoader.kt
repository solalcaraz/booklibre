package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.ReseniaReqDTO
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.service.ReseniaService
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDate
import kotlin.Int

@Component
@Order(3)
class ReservaLoader(
    private val reservaRepository: ReservaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val libroRepository: LibroRepository,
    private val reseniaService: ReseniaService
): CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario = usuarioRepository.findById(1).orElseThrow { NotFoundException("Usuario no encontrado") }
        val usuario2 = usuarioRepository.findById(2).orElseThrow { NotFoundException("Usuario no encontrado") }
        val usuario3 = usuarioRepository.findById(3).orElseThrow { NotFoundException("Usuario no encontrado") }

        val libro1 = libroRepository.findById(1).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro2 = libroRepository.findById(2).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro3 = libroRepository.findById(3).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro4 = libroRepository.findById(4).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro5 = libroRepository.findById(5).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro6 = libroRepository.findById(6).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro7 = libroRepository.findById(7).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro8 = libroRepository.findById(8).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro9 = libroRepository.findById(9).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro10 = libroRepository.findById(10).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro11 = libroRepository.findById(11).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro12 = libroRepository.findById(12).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro13 = libroRepository.findById(13).orElseThrow { NotFoundException("Libro no encontrado") }
        val libro14 = libroRepository.findById(14).orElseThrow { NotFoundException("Libro no encontrado") }

        // maradona u1 = dueno de libro 1 2 3
        // verstappen u2 = dueno de libro 4 5 6 9 10 11 12 13 14
        // u3 = dueno de libro 7 8

        val reserva1 = Reserva(
            libro = libro1,
            lector = usuario2,
            fechaDesde = LocalDate.of(2026, 3, 7),
            fechaHasta = LocalDate.of(2026, 3, 14),
        )

        val reserva2 = Reserva(
            libro = libro2,
            lector = usuario2,
            fechaDesde = LocalDate.of(2026, 3, 3),
            fechaHasta = LocalDate.of(2026, 3, 12),
        )

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

        reservaRepository.save(reserva1)
        reservaRepository.save(reserva2)
        reservaRepository.save(reserva3)
        reservaRepository.save(reserva4)
        reservaRepository.save(reserva5)
        reservaRepository.save(reserva6)
        reservaRepository.save(reserva7)
        reservaRepository.save(reserva8)
        reservaRepository.save(reserva9)
        reservaRepository.save(reserva10)
        reservaRepository.save(reserva11)
        reservaRepository.save(reserva12)
        reservaRepository.save(reserva13)

        // Crear reseñas usando el servicio para que actualice libro
        reseniaService.crearResenia(ReseniaReqDTO(reserva1.id!!, 4, "Libro buenardo"))
        reseniaService.crearResenia(ReseniaReqDTO(reserva2.id!!, 3, "Lo lei y no lo entendi"))
        reseniaService.crearResenia(ReseniaReqDTO(reserva13.id!!, 5, "Epico"))

        //Todo: eliminar
//        reservaRepositorio.create(reserva1)
//        reservaRepositorio.create(reserva2)
//        reservaRepositorio.create(reserva3)
//        reservaRepositorio.create(reserva4)
//        reservaRepositorio.create(reserva5)
//        reservaRepositorio.create(reserva6)
//        reservaRepositorio.create(reserva7)
//        reservaRepositorio.create(reserva8)
//        reservaRepositorio.create(reserva9)
//        reservaRepositorio.create(reserva10)
//        reservaRepositorio.create(reserva11)
//        reservaRepositorio.create(reserva12)
//        reservaRepositorio.create(reserva13)

        println("Reservas cargadas en Postgresql")
    }
}