package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.UsuarioDTO
import ar.edu.unsam.phm.dto.UsuarioLogeadoDTO
import ar.edu.unsam.phm.dto.UsuarioRegister
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
class UsuarioController(val usuarioService: UsuarioService, val usuarioMapper: UsuarioMapper) {
    @PostMapping("/login")
    fun login(@RequestBody usuario: UsuarioDTO): UsuarioLogeadoDTO {
        val usuario = usuarioService.login(usuario)
        return usuarioMapper.toDto(usuario)
    }

    @PostMapping("/register")
    fun register(@RequestBody usuarioRegister: UsuarioRegister): UsuarioLogeadoDTO {
        val usuario = usuarioService.register(usuarioRegister)
        return usuarioMapper.toDto(usuario)
    }

}