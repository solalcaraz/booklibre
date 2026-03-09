package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Coleccionable
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.Dedicatoria
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.dto.toEntity
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.Repositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibroService(
    val libroRepositorio: LibroRepositorio,
    val usuarioRepositorio: UsuarioRepositorio
) {
    fun getAll(): List<Libro> = libroRepositorio.getAll()

    fun getById(id: Int): Libro = libroRepositorio.getById(id)

    fun create(libro: LibroDetalleDTO, usuarioId: Int) {
        val duenio = usuarioRepositorio.getById(usuarioId)
        val nuevoLibro = libro.toEntity(duenio)
        libroRepositorio.create(nuevoLibro)
    }

    fun update(id: Int, libro: LibroDetalleDTO) {
        val libroExistente = libroRepositorio.getById(id)

        val nuevoTipo = when (libro.tipo) {
            TipoDeLibroDTO.COMUN -> Comun
            TipoDeLibroDTO.DEDICATORIA -> Dedicatoria
            TipoDeLibroDTO.COLECCIONABLE -> Coleccionable
        }

        libroExistente.apply {
            titulo = libro.titulo
            autor = libro.autor
            genero = libro.genero
            idioma = libro.idioma
            estado = libro.estado
            editorial = libro.editorial
            descripcion = libro.descripcion
            cantPaginas = libro.paginas
            isbn13 = libro.isbn13
            fechaPublicacion = LocalDate.parse(libro.fechaPublicacion)
            tipo = nuevoTipo
        }

        libroRepositorio.update(libroExistente)
    }
}