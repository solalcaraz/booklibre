package ar.edu.unsam.phm.domain

interface LibroCalculoBibliokarmas {
    val cantPaginas: Int
    val cantidadReservas: Int
    fun plusBiblioKarma(cantidadBiblioKarma: Int): Int
}