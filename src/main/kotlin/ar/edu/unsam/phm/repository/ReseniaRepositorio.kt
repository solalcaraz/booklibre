package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Resenia
import org.springframework.data.jpa.repository.JpaRepository

interface ReseniaRepository : JpaRepository<Resenia, Int> {}
