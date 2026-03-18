package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.dto.*
import org.springframework.stereotype.Service
import java.time.LocalDate

interface LibroAdapter {
    fun fromDto(dto: LibroDetalleDTO, usuarioDueno: Usuario): Libro
    fun toDetalleDto(libro: Libro): LibroDetalleDTO
}

@Service
class LibroMapper : LibroAdapter {

    override fun fromDto(dto: LibroDetalleDTO, usuarioDueno: Usuario): Libro {
        val tipoDominio = when (dto.tipo) {
            TipoDeLibroDTO.COMUN -> Comun
            TipoDeLibroDTO.DEDICATORIA -> Dedicatoria
            TipoDeLibroDTO.COLECCIONABLE -> Coleccionable
        }

        val libro = Libro(
            id = dto.id,
            titulo = dto.titulo,
            tipo = tipoDominio,
            descripcion = dto.descripcion,
            genero = dto.genero,
            autor = dto.autor,
            cantPaginas = dto.paginas,
            isbn13 = dto.isbn13,
            idioma = dto.idioma,
            editorial = dto.editorial,
            fechaPublicacion = LocalDate.parse(dto.fechaPublicacion),
            estado = dto.estado,
            dueno = usuarioDueno
        )
        libro.portada = dto.portada
        return libro
    }

    override fun toDetalleDto(libro: Libro): LibroDetalleDTO {
        val tipoDto = when (libro.tipo) {
            is Comun -> TipoDeLibroDTO.COMUN
            is Dedicatoria -> TipoDeLibroDTO.DEDICATORIA
            is Coleccionable -> TipoDeLibroDTO.COLECCIONABLE
            else -> TipoDeLibroDTO.COMUN // Default seguro
        }

        return LibroDetalleDTO(
            id = libro.id,
            titulo = libro.titulo,
            tipo = tipoDto,
            genero = libro.genero,
            autor = libro.autor,
            idioma = libro.idioma,
            estado = libro.estado,
            editorial = libro.editorial,
            fechaPublicacion = libro.fechaPublicacion.toString(),
            paginas = libro.cantPaginas,
            isbn13 = libro.isbn13,
            descripcion = libro.descripcion,
            portada = libro.portada,
            idDueno = libro.dueno.id!!
        )
    }
}