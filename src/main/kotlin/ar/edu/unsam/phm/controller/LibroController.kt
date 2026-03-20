package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.LibroAdapter
import ar.edu.unsam.phm.helpers.ordenarLibros
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.*
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService,
                      val libroAdapter: LibroAdapter
) {
    @GetMapping("/libro/{id}")
    fun obtenerLibro(@PathVariable id: Int): LibroDetalleDTO = libroAdapter.toDetalleDto(libroService.getById(id))

    @PostMapping("/libro/nuevo")
    fun crearLibro(@RequestBody libroDTO : LibroDetalleDTO) = libroService.create(libroDTO)

    @PutMapping("/libro/{id}")
    fun actualizarLibro(@PathVariable id: Int, @RequestBody libroDTO: LibroDetalleDTO) {
        val libroOriginal = libroService.getById(id)
        if (libroOriginal.dueno.id != libroDTO.idDueno){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes editar un libro que no es tuyo")
        }
        libroService.update(id, libroDTO)
    }

    @GetMapping("/misLibros/{userId}")
    fun listarMisLibros(
        @PathVariable userId: String, // Esto después va a ser un token por headers
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "3") size: Int, // Cambiable por @ModelAttribute
        @RequestParam(defaultValue = "Todos") filter: String,
        @RequestParam(required = false) sort: String?
    ): PaginationResponseDTO<MiLibroDTO> = libroService.misLibros(userId, filter, sort, page, size)


    @DeleteMapping("/misLibros/{bookId}")
    fun removeLibro(@PathVariable bookId: String) = libroService.borrarLibro(bookId)
}
