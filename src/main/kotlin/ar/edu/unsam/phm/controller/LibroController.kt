package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.DetalleLibroDTO
import ar.edu.unsam.phm.dto.LibroHomeDTO
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.toDTO
import ar.edu.unsam.phm.dto.toDetalleDTO
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.*
import ar.edu.unsam.phm.service.UsuarioService

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService, val usuarioService: UsuarioService) {
    @GetMapping("/libros")
    fun listarLibros(): Set<LibroHomeDTO> =
        libroService.getAll().map { it.toDTO() }.toMutableSet()

    @GetMapping("/libro/{id}")
    fun obtenerLibro(@PathVariable id: Int): LibroDetalleDTO = libroService.getById(id).toDetalleDTO()

    @PostMapping("/libro/nuevo")
    fun crearLibro(@RequestBody libroDTO : LibroDetalleDTO, @RequestParam usuarioId: Int) = libroService.create(libroDTO, usuarioId)

    @PutMapping("/libro/{id}")
    fun actualizarLibro(@PathVariable id: Int, @RequestBody libroDTO: LibroDetalleDTO) =
        libroService.update(id, libroDTO)

    @GetMapping("/misLibros/{id}")
    fun listarMisLibros(@PathVariable id: String): List<MiLibroDTO> {
        val usuario = usuarioService.getUsuarioById(id)
        return libroService.misLibros(usuario)
    }
    @GetMapping("/libros/detalle/{id}")
    fun getDetalleLibro(@PathVariable id: String): DetalleLibroDTO {
        return libroService.getDetalle(id.toInt())
    }
}