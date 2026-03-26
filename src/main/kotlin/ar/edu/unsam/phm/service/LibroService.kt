package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.LibroAdapter
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.repository.ReservaRepositorio

@Service
class LibroService(
    val libroRepositorio: LibroRepositorio,
    val reservaRepositorio: ReservaRepositorio,
    val usuarioRepositorio: UsuarioRepositorio,
    val libroMapper: LibroAdapter
) {
    fun getById(id: Int): Libro = libroRepositorio.getById(id)

    fun create(libroDto: LibroDetalleDTO) {
        val duenio = usuarioRepositorio.getById(libroDto.idDueno)
//        val nuevoLibro = libro.toEntity(duenio)
        val nuevoLibro = libroMapper.fromDto(libroDto, duenio)
        libroRepositorio.create(nuevoLibro)
    }

    fun update(id: Int, libroDto: LibroDetalleDTO) {
        val libroExistente = libroRepositorio.getById(id)
        val libroActualizado = libroMapper.fromDto(libroDto, libroExistente.data.dueno)
        libroRepositorio.update(libroActualizado)
    }

    fun misLibros(userId: Int, filter: String, sort: String?, page: Int, size: Int): PaginationResponseDTO<MiLibroDTO> {
        /* Me traigo el usuario por ID*/
        val usuario = usuarioRepositorio.getById(userId)

        /* Para los disponibles sigo necesitando llamar a todos los libros del usuario, no encuentro otra forma */
        /* Porque puede haber libros que nunca hayan sido reservados */
        val librosDelUsuario = libroRepositorio.librosDe(usuario)

        return reservaRepositorio.buscarMisLibros(usuario, librosDelUsuario, filter, sort, page, size)
    }

    fun borrarLibro(id: Int) = libroRepositorio.deleteById(id)
}