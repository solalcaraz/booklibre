package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import org.springframework.stereotype.Service
import ar.edu.unsam.phm.dto.MiLibroProjection
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.helpers.LibroAdapter
import ar.edu.unsam.phm.exceptions.ForbiddenException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import jakarta.persistence.EntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@Service
class LibroService(
    val libroRepository: LibroRepository,
    val reservaRepository: ReservaRepository,
    val usuarioRepository: UsuarioRepository,
    val libroMapper: LibroAdapter,
    val entityManager: EntityManager
) {
//    fun getById(id: Int): Libro = libroRepositorio.getById(id)

    fun getByIdFor(id: Int, idUsuarioLogueado: Int): Libro {
        val libro = libroRepository.findById(id)
            .orElseThrow { NotFoundException("Libro no encontrado.") }
//        val libro = libroRepositorio.getById(id)
        if (libro.dueno.id != idUsuarioLogueado) {
            throw ForbiddenException("No tenés permisos para modificar este libro.")
        }
        return libro
    }

    @Transactional
    fun create(libroDto: LibroDetalleDTO) {
        val dueno = usuarioRepository.findById(libroDto.idDueno).orElseThrow { NotFoundException("Usuario no encontrado") }
        val nuevoLibro = libroMapper.fromDto(libroDto, dueno)
        libroRepository.save(nuevoLibro)
    }

    @Transactional
    fun update(id: Int, libroDto: LibroDetalleDTO) {
        val idUsuarioLogueado = libroDto.idDueno
        val libroExistente = getByIdFor(id, idUsuarioLogueado)

        // Cuando hacés un save() de un objeto con una clase distinta,
        // Hibernate no puede hacer un UPDATE de la columna discriminadora (tipo_libro).
        // La única forma de "engañar" a Hibernate sin borrar el registro es
        // modificar el discriminador manualmente antes de que el Service intente guardar.
        val tipoNuevo = libroDto.tipo.toString()
        if (libroExistente.javaClass.simpleName != "Libro${tipoNuevo.lowercase().capitalize()}") {
            entityManager.detach(libroExistente)
            libroRepository.cambiarTipoDeLibro(id, tipoNuevo)
        }

        val libroActualizado = libroMapper.fromDto(libroDto, libroExistente.dueno)

        // Generamos un libro nuevo en caso de que el tipo de libro sea distinto,
        // así que tenemos que borrar el existente
//        if (libroExistente::class != libroActualizado::class) {
//            libroRepositorio.delete(libroExistente)
//            libroRepositorio.flush()
//        }

        libroRepository.save(libroActualizado)
    }

    fun buscarMisLibros(
        userId: String,
        filter: String,
        sort: String?,
        page: Int,
        size: Int
    ): PaginationResponseDTO<MiLibroProjection> {
        val id = userId.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")
        /* Si no existe el usuario, lanza not found y corta el flujo de ejecución */
        usuarioRepository.findById(id).orElseThrow { NotFoundException("Usuario no encontrado") }

        val paginado = PageRequest.of(page, size)

        val librosPage = when (filter) {
            "Disponibles" -> libroRepository.librosDisponiblesDe(id, sort, paginado)
            "Prestados" -> libroRepository.librosPrestadosDe(id, sort, paginado)
            else -> libroRepository.todosLibrosDe(id, sort, paginado)
        }

        /* Esto lo puedo mover al controller, sería serializar el DTO final */
        return PaginationResponseDTO(
            items = librosPage.content,
            page = librosPage.number,
            size = librosPage.size,
            totalItems = librosPage.totalElements.toInt(),
            totalPages = librosPage.totalPages
        )
    }

    @Transactional
    fun despublicarLibro(id: String){
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("El ID brindado no es válido")
        val libro = libroRepository.findById(idNumerico)
            .orElseThrow { NotFoundException("Libro no encontrado") }

        libro.activo = false

        libroRepository.save(libro)
    }
}