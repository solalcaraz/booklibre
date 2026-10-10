package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.ReseniaReqDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.ReseniaRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class ReseniaService (
    private val reseniaRepository: ReseniaRepository,
    private val reservaRepository: ReservaRepository,
    private val libroMongoRepository: LibroDocumentRepository
) {

    @Transactional
    fun crearResenia(reseniaReq: ReseniaReqDTO) {
        val reserva = reservaRepository.findById(reseniaReq.reservaId).orElseThrow { NotFoundException("Reserva no encontrada") }
        validarResenia(reseniaReq, reserva)
        val libroMongoId = reserva.libroMongoId ?: throw NotFoundException("Reserva sin libro asociado")
        val nuevaResenia = Resenia(reserva.lector.nombreCompleto, reseniaReq.puntuacion, reseniaReq.comentario, libroMongoId)
        reseniaRepository.save(nuevaResenia)

        actualizarPromedioCalificacionEnMongo(libroMongoId)

        reserva.calificar(nuevaResenia.puntuacion)
        reservaRepository.save(reserva)
    }

    private fun validarResenia(reseniaReq: ReseniaReqDTO, reserva: Reserva) {
        if (!reserva.puedeCalificar()){
            throw BadRequestException("No podés reseñar, no esta finalizada o ya calificaste")
        }
        if (reseniaReq.puntuacion !in 0..5) {
            throw BadRequestException("El puntaje debe estar entre 0 y 5")
        }
        if (reseniaReq.comentario.isBlank()) {
            throw BadRequestException("El comentario no puede estar vacio")
        }
    }

    private fun actualizarPromedioCalificacionEnMongo(libroId: String) {
        val libroMongo = libroMongoRepository.findById(libroId).orElseThrow { NotFoundException("Libro no encontrado en Mongo") }
        val promedio = reseniaRepository.promedioCalificacionDe(libroId)

        libroMongo.promedioCalificacion = BigDecimal(promedio)
            .setScale(1, RoundingMode.HALF_UP)
            .toDouble()

        libroMongoRepository.save(libroMongo)
    }
}
