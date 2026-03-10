package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroHomeDTO
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.toDTO
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService, val usuarioService: UsuarioService) {
    @GetMapping("/libros")
    fun listarLibros(): Set<LibroHomeDTO> =
        libroService.getAll().map { it.toDTO() }.toMutableSet()

    @GetMapping("/misLibros/{id}")
    fun listarMisLibros(@PathVariable id: String): List<MiLibroDTO> {
        val usuario = usuarioService.getUsuarioById(id)
        return libroService.misLibros(usuario)
    }
}