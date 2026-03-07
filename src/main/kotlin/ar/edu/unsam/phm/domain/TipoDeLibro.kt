package ar.edu.unsam.phm.domain

import kotlin.math.ceil

interface TipoDeLibro {
    fun plusBiblioKarma(libro: Libro, usuario: Usuario): Int
}

object Comun: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario): Int =
        if (usuario.bibliokarmas < 1000)
            libro.cantPaginas * 5
        else libro.cantPaginas * 2
}

object Dedicatoria: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario): Int {
        //return 200 + 10 * libro.cantReservas.size
        return 1
    }
}

object Coleccionable: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario): Int {
        val quintaParte = ceil(usuario.bibliokarmas / 5.0).toInt()
        return quintaParte + libro.cantPaginas
    }
}