package ar.edu.unsam.phm.loaders

import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.env.Environment
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Order(1)
class UsuarioLoader(
    private val usuarioRepository: UsuarioRepository,
    private val encoder: PasswordEncoder
): CommandLineRunner {
    override fun run(vararg args: String?) {
        val usuario = Usuario(
            nombreCompleto = "Diego Maradona",
            email = "diego@maradona.com",
            password =  encoder.encode("admin")
        ).apply {
            descripcion = "La Mano de Dios"
            ciudad = "Lanús, BA"
            telefono = "+54 (11) 123-4567"
            bibliokarmas = 1986
            imagen = Environment.API_URL + "/imagenes/diego.jpg"
        }

        val usuario2 = Usuario(
            nombreCompleto = "Max Verstappen",
            email = "max@verstappen.com",
            password =  encoder.encode("admin")
        ).apply {
            descripcion = "The Flying Dutchman"
            ciudad = "Maaseik, Bélgica"
            telefono = "+54 (11) 123-4567"
            bibliokarmas = 2023
            imagen = "https://cdn.crash.net/2025-10/si202510191331.jpg?width=1600&aspect_ratio=1:1"
        }

        val usuario3 = Usuario(
            nombreCompleto = "Ye west",
            email = "yewest@bully.com",
            password =  encoder.encode("admin")
        ).apply { tipoUsuario = TipoUsuario.PUBLICADOR }

        val usuario4 = Usuario(
            nombreCompleto = "Yiya Murano",
            email = "yiyi@killer.com",
            password =  encoder.encode("admin")
        ).apply { tipoUsuario = TipoUsuario.LECTOR }

        usuarioRepository.save(usuario)
        usuarioRepository.save(usuario2)
        usuarioRepository.save(usuario3)
        usuarioRepository.save(usuario4)

        println("Usuarios cargados en Postgres")
    }
}