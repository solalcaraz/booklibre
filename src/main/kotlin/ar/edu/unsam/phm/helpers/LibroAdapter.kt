package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.domain.Estado
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.LibroColeccionableDocument
import ar.edu.unsam.phm.domain.LibroComunDocument
import ar.edu.unsam.phm.domain.LibroDedicatoriaDocument
import ar.edu.unsam.phm.domain.LibroDocument
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibroMapper {
    fun fromDto(dto: LibroDetalleDTO, duenioId: Int?): LibroDocument {
        val libro = when (dto.tipo) {
            TipoDeLibroDTO.COMUN -> LibroComunDocument()
            TipoDeLibroDTO.DEDICATORIA -> LibroDedicatoriaDocument()
            TipoDeLibroDTO.COLECCIONABLE -> LibroColeccionableDocument()
        }

        return libro.apply {
            id = dto.id
            titulo = dto.titulo
            descripcion = dto.descripcion
            genero = Genero.valueOf(dto.genero)
            autor = dto.autor
            cantPaginas = dto.paginas
            isbn13 = dto.isbn13
            idioma = Idioma.valueOf(dto.idioma)
            editorial = dto.editorial
            fechaPublicacion = LocalDate.parse(dto.fechaPublicacion)
            estado = Estado.valueOf(dto.estado)
            portada = dto.portada
            duenoId = dto.duenoId
        }
    }

    fun toDetalleDto(libro: LibroDocument): LibroDetalleDTO {
        val tipoDto = when (libro) {
            is LibroComunDocument -> TipoDeLibroDTO.COMUN
            is LibroDedicatoriaDocument -> TipoDeLibroDTO.DEDICATORIA
            is LibroColeccionableDocument -> TipoDeLibroDTO.COLECCIONABLE
            else -> throw IllegalArgumentException("Tipo de libro desconocido")
        }

        return LibroDetalleDTO(
            id = libro.id,
            titulo = libro.titulo,
            tipo = tipoDto,
            genero = libro.genero.label,
            autor = libro.autor,
            idioma = libro.idioma.label,
            estado = libro.estado.label,
            editorial = libro.editorial,
            fechaPublicacion = libro.fechaPublicacion.toString(),
            paginas = libro.cantPaginas,
            isbn13 = libro.isbn13,
            descripcion = libro.descripcion,
            portada = libro.portada,
            duenoId = libro.duenoId
        )
    }
}