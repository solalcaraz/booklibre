package ar.edu.unsam.phm.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.BeanInstantiationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.OffsetDateTime

// Los filtros del home se validan en el init de FiltrosLibro. Como Spring construye ese objeto
// desde los query params, envuelve la BadRequestException y el mensaje que ve el usuario se pierde
// ("Failed to instantiate..."). Acá se desenvuelve para responder el mensaje original.
@RestControllerAdvice
class ValidacionFiltrosHandler {

    @ExceptionHandler(BeanInstantiationException::class)
    fun handle(ex: BeanInstantiationException, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val causa = ex.cause as? BadRequestException ?: throw ex

        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity.status(status).body(
            mapOf(
                "timestamp" to OffsetDateTime.now().toString(),
                "status" to status.value(),
                "error" to status.reasonPhrase,
                "message" to (causa.message ?: ""),
                "path" to request.requestURI
            )
        )
    }
}
