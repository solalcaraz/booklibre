package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.helpers.ReservaMapper
import ar.edu.unsam.phm.service.ReservaService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
@RequestMapping("/reserva")

class ReservaController (private val reservaService: ReservaService, private val reservaMapper: ReservaMapper) {

    //Todo: filtrar por inputSearch
    @PostMapping("/misReservas")
    fun prestadosAmi(
        @RequestBody request: PaginationReqDTO<ReservaRequestDTO>
    ): PaginationResponseDTO<ReservaDTO> {
        val page = reservaService.getMisReservas(request.pageNumber, request.pageSize)
        val reservasDto = page.items.map { reservaMapper.toDto(it) }

        return PaginationResponseDTO(
            items = reservasDto,
            page = page.page,
            size = page.size,
            totalItems = page.totalItems,
            totalPages = page.totalPages
        )
    }

    @PostMapping("/prestados")
    fun prestadosPorMi(
        @RequestBody request: PaginationReqDTO<ReservaRequestDTO>
    ): PaginationResponseDTO<ReservaDTO> {
        val page = reservaService.getPrestados(request.pageNumber, request.pageSize)
        val reservasDto = page.items.map { reservaMapper.toDto(it) }

        return PaginationResponseDTO(
            items = reservasDto,
            page = page.page,
            size = page.size,
            totalItems = page.totalItems,
            totalPages = page.totalPages
        )
    }

}
