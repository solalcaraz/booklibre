package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.LibroHomeDTO
import ar.edu.unsam.phm.dto.toDTO
import ar.edu.unsam.phm.dto.toDetalleDTO
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService) {
    @GetMapping("/libros")
    fun listarLibros(): Set<LibroHomeDTO> =
        libroService.getAll().map { it.toDTO() }.toMutableSet()

    @GetMapping("/libro/{id}")
    fun obtenerLibro(@PathVariable id: Int): LibroDetalleDTO = libroService.getById(id).toDetalleDTO()

}