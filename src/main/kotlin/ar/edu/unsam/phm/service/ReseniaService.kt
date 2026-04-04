package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.dto.ReseniaReqDTO
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

        if (!reserva.estaFinalizada()) {
            throw IllegalStateException("No podés reseñar una reserva no finalizada")
        }
        val nuevaResenia = Resenia(reserva.lector.nombreCompleto, reseniaReq.puntuacion, reseniaReq.comentario, reserva)
        reserva.validarReseña(nuevaResenia)
        reserva.resenia = nuevaResenia
        reseniaRepository.save(nuevaResenia)

        val libro = reserva.libro
        libro.agregarPuntaje(nuevaResenia.puntuacion)
        libroRepository.save(libro)
    }
}
