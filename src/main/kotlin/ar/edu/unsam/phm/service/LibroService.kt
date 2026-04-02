package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.repository.LibroRepositorio
import org.springframework.stereotype.Service
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.helpers.LibroAdapter
import ar.edu.unsam.phm.exceptions.ForbiddenException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepository

@Service
class LibroService(
    val libroRepositorio: LibroRepositorio,
    val reservaRepositorio: ReservaRepositorio,
    val usuarioRepository: UsuarioRepository,
    val libroMapper: LibroAdapter
) {
    fun getById(id: Int): Libro = libroRepositorio.getById(id)

    fun getByIdFor(id: Int, idUsuarioLogueado: Int): Libro {
        val libro = libroRepositorio.getById(id)
        if (libro.data.dueno.id != idUsuarioLogueado) {
            throw ForbiddenException("No tenés permisos para modificar este libro.")
        }
        return libro
    }

    fun create(libroDto: LibroDetalleDTO) {
        val dueno = usuarioRepository.findById(libroDto.idDueno).orElseThrow { NotFoundException("Usuario no encontrado") }
        val nuevoLibro = libroMapper.fromDto(libroDto, dueno)
        libroRepositorio.create(nuevoLibro)
    }

    fun update(id: Int, libroDto: LibroDetalleDTO) {
        val idUsuarioLogueado = libroDto.idDueno
        val libroExistenteData = getByIdFor(id, idUsuarioLogueado).data

        val libroActualizado = libroMapper.fromDto(libroDto, libroExistenteData.dueno)
        libroRepositorio.update(libroActualizado)
    }

    fun misLibros(userId: String, filter: String, sort: String?, page: Int, size: Int): PaginationResponseDTO<MiLibroDTO> {
        val idNumerico = userId.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")
        /* Me traigo el usuario por ID*/
        val usuario = usuarioRepository.findById(idNumerico).orElseThrow { NotFoundException("Usuario no encontrado") }

        /* Para los disponibles sigo necesitando llamar a todos los libros del usuario, no encuentro otra forma */
        /* Porque puede haber libros que nunca hayan sido reservados */
        val librosDelUsuario = libroRepositorio.librosDe(usuario)

        return reservaRepositorio.buscarMisLibros(usuario, librosDelUsuario, filter, sort, page, size)
    }

    fun borrarLibro(id: String){
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")

        return libroRepositorio.deleteById(idNumerico)
    }
}