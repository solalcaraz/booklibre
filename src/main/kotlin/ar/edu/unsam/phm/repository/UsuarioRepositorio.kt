package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Usuario
import org.springframework.data.jpa.repository.JpaRepository

/* @Component */
interface UsuarioRepository : JpaRepository<Usuario, Int> {
    fun findByEmail(email: String): MutableList<Usuario>
}