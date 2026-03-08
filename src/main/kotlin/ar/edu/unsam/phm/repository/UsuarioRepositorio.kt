package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Usuario
import org.springframework.stereotype.Component

// Si alguna de las condiciones se cumple,
// devuelve true, que es recibido por el filter y devuelve el array con las coincidencias
class UsuarioSearchStrategy : SearchStrategy<Usuario> {
    override fun matches(obj: Usuario, value: String): Boolean {
        return obj.nombreCompleto.contains(value, ignoreCase = true) ||
                obj.email.contains(value, ignoreCase = true)
    }
}

@Component
class UsuarioRepositorio : Repositorio<Usuario>(searchStrategy = UsuarioSearchStrategy()) {}