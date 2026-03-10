package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.helpers.ReservaMapper
import ar.edu.unsam.phm.service.ReservaService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
@RequestMapping("/reserva")

class ReservaController (private val reservaService: ReservaService, private val reservaMapper: ReservaMapper) {

    //Todo: filtrar por parametro
    @GetMapping("/misReservas")
    fun prestadosAmi(
        @RequestParam(defaultValue = "0") pageNum: Int,
        @RequestParam(defaultValue = "6") pageSize: Int
    ): PaginationResponseDTO<ReservaDTO> {
        val page = reservaService.getMisReservas(pageNum, pageSize)
        val reservasDto = page.items.map { reservaMapper.toDto(it) }

        return PaginationResponseDTO(
            items = reservasDto,
            page = page.page,
            size = page.size,
            totalItems = page.totalItems,
            totalPages = page.totalPages
        )
    }

    @GetMapping("/prestados")
    fun prestadosPorMi(
        @RequestParam(defaultValue = "0") pageNum: Int,
        @RequestParam(defaultValue = "6") pageSize: Int
    ): PaginationResponseDTO<ReservaDTO> {
        val page = reservaService.getPrestados(pageNum, pageSize)
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
