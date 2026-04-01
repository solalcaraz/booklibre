package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.ordenarLibros
import ar.edu.unsam.phm.helpers.paginate
import org.springframework.stereotype.Component
import java.time.LocalDate
import kotlin.text.isNullOrEmpty

class ReservaSearchStrategy : SearchStrategy<Reserva> {
    override fun matches(obj: Reserva, value: String): Boolean {
        return obj.libro.data.titulo.contains(value, ignoreCase = true) ||
                obj.lector.nombreCompleto.contains(value, ignoreCase = true) ||
                obj.libro.data.isbn13.contains(value, ignoreCase = true)
    }
}

@Component
class ReservaRepositorio : Repositorio<Reserva>(searchStrategy = ReservaSearchStrategy()) {
    /* Trae todas las reservas de un libro */
    fun buscarPorLibro(libro: Libro): List<Reserva> {
        return objetos.values.filter { it.libro == libro }
    }

    /* Cantidad de libros leídos de un usuario = todas sus reservas que ya finalizaron */
    fun cantidadLeidosDe(usuario: Usuario): Int {
        return objetos.values
            .filter { it.lector.id == usuario.id }
            .count { it.estaFinalizada() }
    }

    /* Cantidad de libros prestados de un usuario = todas las reservas de cuyo libro es dueño */
    fun cantidadPrestadosDe(usuario: Usuario): Int {
        return objetos.values
            .count { it.libro.data.dueno.id == usuario.id }
    }

    fun librosPrestadosDe(usuario: Usuario): List<Libro> {
        return objetos.values.filter { it.libro.data.dueno.id == usuario.id && it.estaActiva()}.map { it.libro }
    }

    fun buscarMisLibros(usuario: Usuario, librosDelUsuario: List<Libro>, filter: String, sort: String?, page: Int, size: Int): PaginationResponseDTO<MiLibroDTO> {
        val librosPrestados = librosPrestadosDe(usuario)
        val idsPrestados = librosPrestados.map { it.id }.toSet()

        val librosFiltrados = when (filter) {
            /* Los libros disponibles son todos los libros del usuario que no estén prestados */
            "Disponibles" -> librosDelUsuario.filter { it.id !in idsPrestados }
            "Prestados" -> librosPrestados
            else -> librosDelUsuario
        }

        val librosDto = librosFiltrados.map { libro ->
            MiLibroDTO(
                id = libro.id,
                titulo = libro.data.titulo,
                autor = libro.data.autor,
                genero = libro.data.genero.label,
                estadoReserva = if (libro.id in idsPrestados) "PRESTADO" else "DISPONIBLE",
                agregadoEl = libro.data.fechaPublicacion,
                portada = libro.data.portada
            )
        }

        /* Antes de ordenarlos yo necesito que ya estén etiquetados con "PRESTADO" o "DISPONIBLE"
        porque uno de los sorts es por el estado de la reserva, y el otro por fecha de agregado */
        /* Entiendo que después la base de datos puede agregar una columna con esa etiqueta a la tabla,
        y no hace falta terminar armando el DTO en el repositorio, delegándolo al controller */
        val librosOrdenados = ordenarLibros(librosDto, sort)

        return paginate(librosOrdenados, page, size)
    }

    // filtra por el id de los libros que estan reservados
    fun getIdLibrosOcupadosEntre(desde: LocalDate, hasta: LocalDate): Set<Int> =
        objetos.values
            .filter { it.seSuperponeCon(desde, hasta) }
            .mapNotNull { it.libro.id }
            .toSet()

    // devuelve la cantidad de reservas que tiene un libro
    fun getCantidadReservasPorLibro(id: Int): Int =
        objetos.values.count { it.libro.id == id }

    // devuelve una lista de las puntaciones que tiene un libro
    fun getPuntuacionesByLibro(id: Int): List<Int> =
        objetos.values
            .filter { it.libro.id == id }
            .mapNotNull { it.resenia?.puntuacion }

    fun getUltimasReservas(libro:Libro): List<Reserva>{
        return buscarPorLibro(libro)
            .filter { it.estaFinalizada() }
            .filter { it.resenia != null }
            .sortedByDescending { it.fechaHasta }
            .take(2)
    }

    fun getReservasOcupadas(libro:Libro): List<Reserva>{
        return buscarPorLibro(libro)
            .filter { !it.estaFinalizada() }
    }

    fun tieneReservasSuperpuestas(libro: Libro, desde: LocalDate, hasta: LocalDate): Boolean {
        return getReservasOcupadas(libro).any { it.seSuperponeCon(desde, hasta) }
    }

    fun usuarioTieneReservaActiva(libro: Libro, usuario: Usuario): Boolean{
        return buscarPorLibro(libro).any{
            it.lector.id == usuario.id && !it.estaFinalizada()
        }
    }

    fun getPrestadosPorMi(usuarioId: String, pageNumber: Int, pageSize: Int, search: String?): PaginationResponseDTO<Reserva>  {
        var reservas = objetos.values.filter { it.libro.data.dueno.id.toString() == usuarioId }
        reservas = filtrarReservas(reservas, search)
        return paginate(reservas, pageNumber, pageSize)
    }

    fun getPrestadosAMi(usuarioId: String, pageNumber: Int, pageSize: Int, search: String?): PaginationResponseDTO<Reserva>  {
        var reservas = objetos.values.filter { it.lector.id.toString() == usuarioId }
        reservas = filtrarReservas(reservas, search)
        return paginate(reservas, pageNumber, pageSize)
    }

    private fun filtrarReservas(reservas: List<Reserva>, search: String?): List<Reserva> {
        if (!search.isNullOrEmpty()) {
            return reservas.filter {
                it.libro.data.titulo.contains(search, ignoreCase = true) ||
                        it.libro.data.autor.contains(search, ignoreCase = true)
            }
        }
        return reservas
    }
}
