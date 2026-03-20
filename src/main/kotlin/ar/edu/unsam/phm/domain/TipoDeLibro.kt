//package ar.edu.unsam.phm.domain
//
//import kotlin.math.ceil
//
//interface TipoDeLibro {
//    fun plusBiblioKarma(libro: Libro, usuario: Usuario, cantidadReservas: Int): Int
//}
//
//object Comun: TipoDeLibro {
//    override fun plusBiblioKarma(libro: Libro, usuario: Usuario, cantidadReservas: Int): Int =
//        if (usuario.bibliokarmas < 1000)
//            libro.cantPaginas * 5
//        else libro.cantPaginas * 2
//}
//
//object Dedicatoria: TipoDeLibro {
//    override fun plusBiblioKarma(libro: Libro, usuario: Usuario, cantidadReservas: Int): Int {
//        return 200 + 10 * cantidadReservas
//    }
//}
//
//object Coleccionable: TipoDeLibro {
//    override fun plusBiblioKarma(libro: Libro, usuario: Usuario, cantidadReservas: Int): Int {
//        val quintaParte = ceil(usuario.bibliokarmas / 5.0).toInt()
//        return quintaParte + libro.cantPaginas
//    }
//}