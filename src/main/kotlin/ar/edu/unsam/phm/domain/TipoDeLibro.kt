package ar.edu.unsam.phm.domain

interface TipoDeLibro {
    fun plusBiblioKarma(libro: Libro, usuario: Usuario): Int
}

object Comun: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario): Int {
        // usuario > 1000 bibliokarmas
            //libro.cantPaginas * 5
        //sino
            //libro.cantPaginas * 2
        return 1
    }
}

object Dedicatoria: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario): Int {
        //return 200 + 10 * libro.cantReservas.size()
        return 1
    }
}

object Coleccionable: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario): Int {
        //quinta parte de los bibliokarmas del usuario (redondeo) + libro.cantPaginas
        return 1
    }
}