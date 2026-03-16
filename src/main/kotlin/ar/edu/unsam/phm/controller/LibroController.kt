package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.DetalleLibroDTO
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.toDetalleDTO
import ar.edu.unsam.phm.helpers.ordenarLibros
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.*
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService, val usuarioService: UsuarioService) {

    @GetMapping("/libro/{id}")
    fun obtenerLibro(@PathVariable id: Int): LibroDetalleDTO = libroService.getById(id).toDetalleDTO()

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
        @RequestParam(defaultValue = "3") size: Int,
        @RequestParam(defaultValue = "Todos") filter: String,
        @RequestParam(required = false) sort: String?
    ): PaginationResponseDTO<MiLibroDTO> {
        val usuario = usuarioService.getUsuarioById(userId)
        val libros = libroService.misLibros(usuario, filter)
        val librosOrdenados = ordenarLibros(libros, sort)

        return paginate(librosOrdenados, page, size)
    }

    @DeleteMapping("/misLibros/{bookId}")
    fun removeLibro(@PathVariable bookId: String){
        return libroService.borrarLibro(bookId)
    }

    @GetMapping("/libros/detalle/{id}")
    fun getDetalleLibro(@PathVariable id: String): DetalleLibroDTO {
        return libroService.getDetalle(id.toInt())
    }
}
