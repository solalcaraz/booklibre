package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroPopular
import org.springframework.data.repository.CrudRepository

interface LibroPopularRepository : CrudRepository<LibroPopular, String>
