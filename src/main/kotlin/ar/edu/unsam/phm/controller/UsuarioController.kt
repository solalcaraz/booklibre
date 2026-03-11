package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.UsuarioCompletoDTO
import ar.edu.unsam.phm.dto.UsuarioPatch
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
@RequestMapping("/usuario")
class UsuarioController(val usuarioService: UsuarioService, val usuarioMapper: UsuarioMapper) {
    @GetMapping("/{id}")
    fun getUsuario(@PathVariable id: String): UsuarioCompletoDTO {
        val user = usuarioService.getUsuarioById(id)

        return usuarioMapper.toFullDto(user)
    }

    @PatchMapping("/{id}")
    fun updateUsuario(@PathVariable id: String, @RequestBody userPatch: UsuarioPatch) {
        return usuarioService.updateUsuario(userPatch, id)
    }
}