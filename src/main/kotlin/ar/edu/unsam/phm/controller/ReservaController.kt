package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.CrearReservaDTO
import ar.edu.unsam.phm.dto.DetalleReservaDTO
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReseniaReqDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.dto.ReservaLibroDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.dto.toReservaLibroDTO
import ar.edu.unsam.phm.helpers.ReservaMapper
import ar.edu.unsam.phm.service.ReseniaService
import ar.edu.unsam.phm.service.ReservaService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@CrossOrigin("*")
@RequestMapping("/reserva")
class ReservaController (private val reservaService: ReservaService, private val reservaMapper: ReservaMapper,
    private val reseniaService: ReseniaService) {

    @PostMapping("/obtenerMisReservas")
    fun obtenerReservas(
        @RequestBody payload: PaginationReqDTO<ReservaRequestDTO>
    ): PaginationResponseDTO<ReservaDTO> {
        return reservaService.getMisReservas(payload)
    }

    @PostMapping("/obtenerPrestadosPorMi")
    fun obtenerPrestadosPorMi(
        @RequestBody payload: PaginationReqDTO<ReservaRequestDTO>
    ): PaginationResponseDTO<ReservaDTO> {
        val page = reservaService.getPrestadosPorMi(payload)
        return reservaMapper.toDtoReservaPag(page)
    }

    @GetMapping("/librosDisponibles")
    fun listarLibros(
        @ModelAttribute filtros: FiltrosLibro
    ): PaginationResponseDTO<ReservaLibroDTO> {
        val userId = SecurityContextHolder
            .getContext()
            .authentication
            .name
            .toInt()

        val page = reservaService.getLibrosDisponiblesParaReserva(userId, filtros)
        return PaginationResponseDTO(
            items = page.content.map { it.toReservaLibroDTO() },
            page = page.number,
            size = page.size,
            totalItems = page.totalElements.toInt(),
            totalPages = page.totalPages
        )
    }

    @PostMapping("/calificar")
    fun calificar(@RequestBody payload: ReseniaReqDTO) {
        reseniaService.crearResenia(payload)
    }

    @GetMapping("/detalles/{libroId}")
    fun getDetalles(
        @PathVariable libroId: String,
        @RequestHeader("userId") userId: String,
        @RequestParam fechaDesde: String,
        @RequestParam fechaHasta: String
    ): DetalleReservaDTO {
        return reservaService.getDetalleReserva(
            libroId.toInt(),
            userId.toInt(),
            LocalDate.parse(fechaDesde),
            LocalDate.parse(fechaHasta)
        )
    }

    @PostMapping("/crear")
    fun crearReserva(
        @RequestHeader("userId") userId: String,
        @RequestBody reserva: CrearReservaDTO
    ): ReservaDTO {
        return reservaService.crearReserva(userId.toInt(), reserva)
    }
}
