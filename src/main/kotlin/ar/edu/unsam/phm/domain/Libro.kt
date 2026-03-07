package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.EntidadConId
import java.util.Date

class Libro(
    override var id: Int? = null,
    val titulo: String,
    val tipo: TipoDeLibro,
    val descripcion: String,
    val genero: Genero,
    val autor: String,
    val cantPaginas: Int,
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

enum class Genero {
    DRAMA,
    CIENCIA_FICCION,
    ROMANCE,
    AUTOAYUDA,
    DISENO,
    LITERATURA_CLASICA
}

enum class Idioma {
    ESPANOL,
    INGLES,
    FRANCES,
    PORTUGUES
}

enum class Estado {
    EXCELENTE,
    MUY_BUENO,
    BUENO,
    MALO,
    REGULAR
}