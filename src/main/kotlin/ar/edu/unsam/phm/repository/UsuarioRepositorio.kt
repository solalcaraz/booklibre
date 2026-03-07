package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Usuario
import org.springframework.stereotype.Component

@Component
class UsuarioRepositorio : Repositorio<Usuario>(searchStrategy = UsuarioSearchStrategy()) {}