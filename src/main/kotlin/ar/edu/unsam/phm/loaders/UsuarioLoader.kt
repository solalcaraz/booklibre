package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class UsuarioLoader(private val usuarioRepositorio: UsuarioRepositorio): CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario = Usuario(
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password =  "admin"
        ).apply {
            descripcion = "Lector Ávido & Coleccionista"
            ciudad = "Gonzalez Catán, BA"
            telefono = "+54 (11) 123-4567"
            bibliokarmas = 2345
        }

        val usuario2 = Usuario(
            nombreCompleto = "Lionel Messi",
            email = "lionel@messi.com",
            password =  "admin"
        )

        usuarioRepositorio.create(usuario)
        usuarioRepositorio.create(usuario2)

        println("Usuarios cargados en memoria")
    }
}