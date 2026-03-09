package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.DetalleLibroDTO
import ar.edu.unsam.phm.dto.LibroHomeDTO
import ar.edu.unsam.phm.dto.toDTO
import ar.edu.unsam.phm.dto.toDetalleDTO
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService) {
    @GetMapping("/libros")
    fun listarLibros(): Set<LibroHomeDTO> =
        libroService.getAll().map { it.toDTO() }.toMutableSet()

    @GetMapping("/libros/detalle/{id}")
    fun getDetalleLibro(@PathVariable id: String): DetalleLibroDTO {
        val libro = libroService.getById(id.toInt());
        return libro.toDetalleDTO();
    }
}