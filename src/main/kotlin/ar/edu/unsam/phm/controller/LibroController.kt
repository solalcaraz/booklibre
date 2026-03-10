package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.DetalleLibroDTO
import ar.edu.unsam.phm.dto.LibroHomeDTO
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.UsuarioLogeadoDTO
import ar.edu.unsam.phm.dto.toDTO
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService, val usuarioService: UsuarioService) {
    @GetMapping("/libros")
    fun listarLibros(): Set<LibroHomeDTO> =
        libroService.getAll().map { it.toDTO() }.toMutableSet()

    @PostMapping("/misLibros")
    fun listarMisLibros(@RequestBody usuarioLogeado: UsuarioLogeadoDTO): List<MiLibroDTO> {
        val usuario = usuarioService.getUsuario(usuarioLogeado)
        return libroService.misLibros(usuario)
    }
    @GetMapping("/libros/detalle/{id}")
    fun getDetalleLibro(@PathVariable id: String, @RequestParam userId: String): DetalleLibroDTO {
        return libroService.getDetalle(id.toInt(), userId.toInt())
    }
}