package ar.edu.unsam.phm.domain

interface TipoDeLibro {
    fun plusBiblioKarma(libro: Libro, usuario: Usuario)
}

object Comun: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario) {
        // usuario > 1000 bibliokarmas
            //libro.cantPaginas * 5
        //sino
            //libro.cantPaginas * 2
    }
}

object Dedicatoria: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario) {
        //return 200 + 10 * libro.cantReservas.size()
    }
}

object Coleccionable: TipoDeLibro {
    override fun plusBiblioKarma(libro: Libro, usuario: Usuario) {
        //quinta parte de los bibliokarmas del usuario (redondeo) + libro.cantPaginas
    }
}