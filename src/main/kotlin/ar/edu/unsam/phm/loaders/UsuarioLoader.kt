package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.env.Environment
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
            descripcion = "La Mano de Dios"
            ciudad = "Lanús, BA"
            telefono = "+54 (11) 123-4567"
            bibliokarmas = 1986
            imagen = Environment.API_URL + "/imagenes/diego.jpg"
            librosLeidos = 45
            librosPrestados = 12
        }

        val usuario2 = Usuario(
            nombreCompleto = "Lionel Messi",
            email = "lionel@messi.com",
            password =  "admin"
        )
        val usuario3 = Usuario(
            nombreCompleto = "Ye west",
            email = "yewest@bully.com",
            password =  "admin"
        )

        usuarioRepositorio.create(usuario)
        usuarioRepositorio.create(usuario2)
        usuarioRepositorio.create(usuario3)

        println("Usuarios cargados en memoria")
    }
}