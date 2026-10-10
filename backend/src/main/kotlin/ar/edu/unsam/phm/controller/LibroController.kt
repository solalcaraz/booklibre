package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.MiLibroProjection
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.LibroMapper
import ar.edu.unsam.phm.service.LibroService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
class LibroController(
    val libroService: LibroService,
    val libroAdapter: LibroMapper
){
    @PreAuthorize("hasRole('PUBLICADOR') or hasRole('LECTOR_PUBLICADOR')")
    @GetMapping("/libro/{id}")
    fun obtenerLibroParaUsuario(@PathVariable id: String): LibroDetalleDTO {
        return libroAdapter.toDetalleDto(libroService.getByIdForUser(id))
    }

    @PreAuthorize("hasRole('PUBLICADOR') or hasRole('LECTOR_PUBLICADOR')")
    @PostMapping("/libro/nuevo")
    fun crearLibro(@RequestBody libroDTO : LibroDetalleDTO) = libroService.create(libroDTO)

    @PreAuthorize("hasRole('PUBLICADOR') or hasRole('LECTOR_PUBLICADOR')")
    @PutMapping("/libro/{id}")
    fun actualizarLibro(@PathVariable id: String, @RequestBody libroDTO: LibroDetalleDTO) { libroService.update(id, libroDTO) }

    @PreAuthorize("hasRole('PUBLICADOR') or hasRole('LECTOR_PUBLICADOR')")
    @GetMapping("/misLibros/me")
    fun listarMisLibros(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "3") size: Int,
        @RequestParam(defaultValue = "Todos") filter: String,
        @RequestParam(required = false) sort: String? = ""
    ): PaginationResponseDTO<MiLibroProjection> {
        val userId = SecurityContextHolder
            .getContext()
            .authentication
            .name
            .toInt()

        return libroService.buscarMisLibros(userId, filter, sort, page, size)
    }

    @PreAuthorize("hasRole('PUBLICADOR') or hasRole('LECTOR_PUBLICADOR')")
    @DeleteMapping("/misLibros/{bookId}")
    fun removeLibro(@PathVariable bookId: String) = libroService.despublicarLibro(bookId)
}
