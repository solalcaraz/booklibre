package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.PruebaUsuario
import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioPruebaRepository : JpaRepository<PruebaUsuario, Long>
