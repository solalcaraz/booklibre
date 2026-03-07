package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class UsuarioLoader(private val usuarioRepositorio: UsuarioRepositorio): CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario = Usuario(nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password =  "admin"
        )

        usuarioRepositorio.create(usuario)

        println("Usuario ${usuario.id} ${usuario.nombreCompleto} ${usuario.email}")
    }
}