package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.ReseniaReqDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.ReseniaRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.LibroRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ReseniaService (
    private val reseniaRepository: ReseniaRepository,
    private val reservaRepository: ReservaRepository,
    private val libroRepository: LibroRepository
) {

    @Transactional
    fun crearResenia(reseniaReq: ReseniaReqDTO) {
        val reserva = reservaRepository.findById(reseniaReq.reservaId).orElseThrow { NotFoundException("Reserva no encontrada") }
        validarResenia(reseniaReq, reserva)
        val nuevaResenia = Resenia(reserva.lector.nombreCompleto, reseniaReq.puntuacion, reseniaReq.comentario, reserva.libro)
        reseniaRepository.save(nuevaResenia)

        val libro = reserva.libro
        libro.agregarResenia(nuevaResenia)
        libroRepository.save(libro)

        reserva.calificar()
        reservaRepository.save(reserva)
    }

    private fun validarResenia(reseniaReq: ReseniaReqDTO, reserva: Reserva) {
        if (!reserva.estaFinalizada()) {
            throw IllegalStateException("No podés reseñar una reserva no finalizada")
        }
        if (reseniaReq.puntuacion !in 0..5) {
            throw BadRequestException("El puntaje debe estar entre 0 y 5")
        }
        if (reseniaReq.comentario.isBlank()) {
            throw BadRequestException("El comentario no puede estar vacio")
        }
    }
}
