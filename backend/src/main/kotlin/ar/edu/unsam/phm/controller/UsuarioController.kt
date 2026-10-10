package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.PerfilUsuarioDTO
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
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
    @GetMapping("/me")
    fun getUsuario(): ResponseEntity<PerfilUsuarioDTO> {
        val userId = SecurityContextHolder
            .getContext()
            .authentication
            .name
            .toInt()

        val user = usuarioService.getUsuarioById(userId)

        return ResponseEntity.ok(usuarioMapper.toFullDto(user))
    }

    @PatchMapping("/me")
    fun updateUsuario(@RequestBody userPatch: UsuarioPatchRequest): AuthenticationResponse {
        val userId = SecurityContextHolder
            .getContext()
            .authentication
            .name
            .toInt()

        return usuarioService.updateUsuario(userPatch, userId)
    }

    @GetMapping("/bibliokarmas/{id}")
    fun getBibliokarma(@PathVariable id: String): Int {
        return usuarioService.getBibliokarma(id)
    }
}