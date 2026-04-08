package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
/*import ar.edu.unsam.phm.dto.MiLibroDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.helpers.ordenarLibros
import ar.edu.unsam.phm.helpers.paginate*/
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Component
import java.time.LocalDate

interface ReservaRepository : JpaRepository<Reserva, Int> {
    /*@Modifying
    @Query("delete from Reserva r where r.libro.id = :libroId")
    fun deleteByLibroId(@Param("libroId") libroId: Int): Int*/

    /* Cantidad de libros leídos de un usuario = todas sus reservas que ya finalizaron */
    @Query("""
        select count(r)
        from Reserva r
        where r.lector.id = :usuarioId
        and r.fechaHasta < current_date
    """)
    fun cantidadLeidosDe(@Param("usuarioId") usuarioId: Int): Int

    /* Cantidad de libros prestados de un usuario = todas las reservas de cuyo libro es dueño */
    @Query("""
        select count(r)
        from Reserva r
        where r.libro.data.dueno.id = :usuarioId
    """)
    fun cantidadPrestadosDe(@Param("usuarioId") usuarioId: Int): Int


    @Query("SELECT r FROM Reserva r WHERE r.libro.dueno.id = :usuarioId AND (LOWER(r.libro.data.titulo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.libro.data.autor) LIKE LOWER(CONCAT('%', :search, '%')))")
    fun findPrestadosPorMi(@Param("usuarioId") usuarioId: Int, @Param("search") search: String?, pageable: Pageable): Page<Reserva>

    @Query("SELECT r FROM Reserva r WHERE r.lector.id = :usuarioId AND (LOWER(r.libro.data.titulo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.libro.data.autor) LIKE LOWER(CONCAT('%', :search, '%')))")
    fun findPrestadosAMi(@Param("usuarioId") usuarioId: Int, @Param("search") search: String?, pageable: Pageable): Page<Reserva>

    fun countByLibroId(libroId: Int): Int

    // Trae solo las puntuaciones de las reseñas de un libro específico
    @Query("SELECT r.resenia.puntuacion FROM Reserva r WHERE r.libro.id = :libroId AND r.resenia IS NOT NULL")
    fun findPuntuacionesByLibroId(@Param("libroId") libroId: Int): List<Int>

    //Ultimas dos reservas
    @Query("SELECT r FROM Reserva r WHERE r.libro.id = :libroId AND r.fechaHasta < :hoy AND r.resenia IS NOT NULL ORDER BY r.fechaHasta DESC")
    fun findTop2UltimasReservas(
        @Param("libroId") libroId: Int,
        @Param("hoy") hoy: LocalDate = LocalDate.now()
    ): List<Reserva>

    // Trae las reservas de un libro que aún no finalizaron (fechaHasta >= HOY)
    @Query("SELECT r FROM Reserva r WHERE r.libro.id = :libroId AND r.fechaHasta >= CURRENT_DATE")
    fun findReservasOcupadas(@Param("libroId") libroId: Int): List<Reserva>

    // Verifica si hay reservas que se superponen con las fechas solicitadas
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r WHERE r.libro.id = :libroId AND r.fechaDesde <= :hasta AND r.fechaHasta >= :desde AND r.fechaHasta >= CURRENT_DATE")
    fun existReservaSuperpuesta(
        @Param("libroId") libroId: Int,
        @Param("desde") desde: LocalDate,
        @Param("hasta") hasta: LocalDate
    ): Boolean

    // Verifica si hay reservas que se superponen con las fechas solicitadas
    // Una superposición ocurre si: reserva.fechaDesde <= hasta_solicitado AND reserva.fechaHasta >= desde_solicitado
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r WHERE r.libro.id = :libroId AND r.fechaDesde <= :hasta AND r.fechaHasta >= :desde AND r.fechaHasta >= CURRENT_DATE")
    fun existsReservaSuperpuesta(
        @Param("libroId") libroId: Int,
        @Param("desde") desde: LocalDate,
        @Param("hasta") hasta: LocalDate
    ): Boolean
}


// Todo: eliminar todo
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


    fun cantidadLeidosDe(usuario: Usuario): Int {
        return objetos.values
            .filter { it.lector.id == usuario.id }
            .count { it.estaFinalizada() }
    }


    fun cantidadPrestadosDe(usuario: Usuario): Int {
        return objetos.values
            .count { it.libro.dueno.id == usuario.id }
    }

    fun librosPrestadosDe(usuario: Usuario): List<Libro> {
        return objetos.values.filter { it.libro.dueno.id == usuario.id && it.estaActiva()}.map { it.libro }
    }

    /*fun buscarMisLibros(usuario: Usuario, librosDelUsuario: List<Libro>, filter: String, sort: String?, page: Int, size: Int): PaginationResponseDTO<MiLibroDTO> {
        val librosPrestados = librosPrestadosDe(usuario)
        val idsPrestados = librosPrestados.map { it.id }.toSet()

        val librosFiltrados = when (filter) {
            *//* Los libros disponibles son todos los libros del usuario que no estén prestados *//*
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

         Antes de ordenarlos yo necesito que ya estén etiquetados con "PRESTADO" o "DISPONIBLE"
        porque uno de los sorts es por el estado de la reserva, y el otro por fecha de agregado
         Entiendo que después la base de datos puede agregar una columna con esa etiqueta a la tabla,
        y no hace falta terminar armando el DTO en el repositorio, delegándolo al controller
        val librosOrdenados = ordenarLibros(librosDto, sort)

        return paginate(librosOrdenados, page, size)
    }*/

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
}