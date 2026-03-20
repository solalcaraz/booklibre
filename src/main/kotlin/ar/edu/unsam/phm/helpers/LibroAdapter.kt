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
        val libroData = LibroData(
            titulo = dto.titulo,
            descripcion = dto.descripcion,
            genero = dto.genero,
            autor = dto.autor,
            cantPaginas = dto.paginas,
            isbn13 = dto.isbn13,
            idioma = dto.idioma,
            editorial = dto.editorial,
            fechaPublicacion = LocalDate.parse(dto.fechaPublicacion),
            estado = dto.estado,
            dueno = usuarioDueno,
            portada = dto.portada
        )

        return when (dto.tipo) {
            TipoDeLibroDTO.COMUN -> LibroComun(libroData)
            TipoDeLibroDTO.DEDICATORIA -> LibroDedicatoria(libroData)
            TipoDeLibroDTO.COLECCIONABLE -> LibroColeccionable(libroData)
        }.apply {
            this.id = dto.id
        }
    }

    override fun toDetalleDto(libro: Libro): LibroDetalleDTO {
        val tipoDto = when (libro) {
            is LibroComun -> TipoDeLibroDTO.COMUN
            is LibroDedicatoria -> TipoDeLibroDTO.DEDICATORIA
            is LibroColeccionable -> TipoDeLibroDTO.COLECCIONABLE
            else -> throw IllegalArgumentException("Tipo de libro desconocido")
        }

        return LibroDetalleDTO(
            id = libro.id,
            titulo = libro.data.titulo,
            tipo = tipoDto,
            genero = libro.data.genero,
            autor = libro.data.autor,
            idioma = libro.data.idioma,
            estado = libro.data.estado,
            editorial = libro.data.editorial,
            fechaPublicacion = libro.data.fechaPublicacion.toString(),
            paginas = libro.data.cantPaginas,
            isbn13 = libro.data.isbn13,
            descripcion = libro.data.descripcion,
            portada = libro.data.portada,
            idDueno = libro.data.dueno.id ?: 0
        )
    }
}