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
            genero = Genero.valueOf(dto.genero),
            autor = dto.autor,
            cantPaginas = dto.paginas,
            isbn13 = dto.isbn13,
            idioma = Idioma.valueOf(dto.idioma),
            editorial = dto.editorial,
            fechaPublicacion = LocalDate.parse(dto.fechaPublicacion),
            estado = Estado.valueOf(dto.estado),
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

        val libroData = libro.data //para achicar los llamados
        return LibroDetalleDTO(
            id = libro.id,
            titulo = libroData.titulo,
            tipo = tipoDto,
            genero = libroData.genero.label,
            autor = libroData.autor,
            idioma = libroData.idioma.label,
            estado = libroData.estado.label,
            editorial = libroData.editorial,
            fechaPublicacion = libroData.fechaPublicacion.toString(),
            paginas = libroData.cantPaginas,
            isbn13 = libroData.isbn13,
            descripcion = libroData.descripcion,
            portada = libroData.portada,
            idDueno = libroData.dueno.id ?: 0
        )
    }
}