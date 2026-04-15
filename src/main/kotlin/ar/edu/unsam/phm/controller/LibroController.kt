package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.MiLibroProjection
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.LibroAdapter
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
class LibroController(
    val libroService: LibroService,
    val libroAdapter: LibroAdapter
){
//    @GetMapping("/libro/{id}")
//    fun obtenerLibro(@PathVariable id: Int): LibroDetalleDTO = libroAdapter.toDetalleDto(libroService.getById(id))

    @GetMapping("/libro/{id}")
    fun obtenerLibroPara(@PathVariable id: Int, @RequestHeader("X-User-Id") idUsuarioLogueado: Int): LibroDetalleDTO = libroAdapter.toDetalleDto(libroService.getByIdFor(id, idUsuarioLogueado))

    @PostMapping("/libro/nuevo")
    fun crearLibro(@RequestBody libroDTO : LibroDetalleDTO) = libroService.create(libroDTO)

    @PutMapping("/libro/{id}")
    fun actualizarLibro(@PathVariable id: Int, @RequestBody libroDTO: LibroDetalleDTO) { libroService.update(id, libroDTO) }

    @GetMapping("/misLibros/{userId}")
    fun listarMisLibros(
        @PathVariable userId: String, // Esto después va a ser un token por headers
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "3") size: Int, // Cambiable por @ModelAttribute
        @RequestParam(defaultValue = "Todos") filter: String,
        @RequestParam(required = false) sort: String? = ""
    ): PaginationResponseDTO<MiLibroProjection> = libroService.buscarMisLibros(userId, filter, sort, page, size)

    @DeleteMapping("/misLibros/{bookId}")
    fun removeLibro(@PathVariable bookId: String) = libroService.despublicarLibro(bookId)
}
