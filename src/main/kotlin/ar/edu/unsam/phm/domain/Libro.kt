package ar.edu.unsam.phm.domain

import org.springframework.hateoas.mediatype.alps.Descriptor
import java.util.Date

class Libro(
    override var id: Int?,
    val titulo: String,
    val tipo: TipoDeLibro,
    val descripcion: String,
    val genero: Genero,
    val autor: String,
    val cantPaginas: String,
    val ISBN13: String,
    val idioma: Idioma,
    val editorial: String,
    val fechaPublicacion: Date,
    val estado: Estado,
    val dueno: Usuario
) : EntidadConId {
    fun calcularBiblioKarmas(usuario: Usuario) {
        //5 * dias reservados
        // tipo.plusBiblioKarma(this, usuario)
    }
}