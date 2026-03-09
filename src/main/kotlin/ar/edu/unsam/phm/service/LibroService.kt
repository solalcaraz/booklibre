package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.Repositorio
import org.springframework.stereotype.Service

@Service
class LibroService(
    val libroRepositorio: LibroRepositorio
) {
    fun getAll(): List<Libro> = libroRepositorio.getAll()
    fun getById(id: Int): Libro = libroRepositorio.getById(id)
}