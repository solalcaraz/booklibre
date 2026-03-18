package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.BusquedaPor
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.helpers.Ordenamiento
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.domain.ReservaSimulada
import ar.edu.unsam.phm.dto.CrearReservaDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReseniaReqDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReservaService (
    private val reservaRepositorio: ReservaRepositorio,
    private val libroRepositorio: LibroRepositorio,
    private val usuarioRepositorio: UsuarioRepositorio
) {
    fun getMisReservas(payload: PaginationReqDTO<ReservaRequestDTO>): PaginationResponseDTO<Reserva> {
        var reservas = reservaRepositorio.getAll()
        // filtrado por tab si es prestado por mi o los que preste
        if (payload.filter?.busquedaPor == BusquedaPor.TAB_PRESTADOS_POR_MI.value){
            reservas = reservas.filter { it.libro.dueno.id.toString() == payload.filter.usuarioId }
        } else if (payload.filter?.busquedaPor == BusquedaPor.TAB_PRESTADOS_A_MI.value) {
            reservas = reservas.filter { it.lector.id.toString() == payload.filter.usuarioId }
        }

        // filtrado por input de busqueda
        val search = payload.filter?.inputSearch?.trim()
        if (!search.isNullOrEmpty()) {
            reservas = reservas.filter {
                it.libro.titulo.contains(search, ignoreCase = true) ||
                        it.libro.autor.contains(search, ignoreCase = true)
            }
        }
        return paginate(reservas, payload.pageNumber, payload.pageSize)
    }

    fun getLibrosDisponibles(usuarioId: Int, filtros: FiltrosLibro): Pair<List<Reserva>, List<Int>> {
        val usuario = usuarioRepositorio.getById(usuarioId)
        // Agrupa los libros por id y cuenta cuantos hay de cada uno.
        val reservasPorLibro= reservaRepositorio.getAll()
            .groupingBy { it.libro.id }
            .eachCount()

        // verifica disponibilidad real con el repo de Reservas, aplica busqueda por teclado, filtros y ordena
        val librosDisponibles =
            (if (filtros.busqueda.isNullOrBlank()) libroRepositorio.getAll() else libroRepositorio.search(filtros.busqueda))
                    .filter { it.dueno.id != usuario.id }
                    .filter { reservaRepositorio.libroEstaDisponibleEntre(it, filtros.fechaDesde, filtros.fechaHasta) }
                    .filter { filtros.aplicarA(it) }
                    .let { libros ->
                        when (filtros.orden) {
                            Ordenamiento.POPULARIDAD -> libros.sortedByDescending { reservasPorLibro[it.id] ?: 0 } // si no fue reservado le dejo un 0 para garantizar que se traiga igualmente y no quede null
                            Ordenamiento.TITULO_ASC -> libros.sortedBy { it.titulo.lowercase() }
                            Ordenamiento.TITULO_DESC -> libros.sortedByDescending { it.titulo.lowercase() }
                            Ordenamiento.AUTOR_ASC -> libros.sortedBy { it.autor }
                            Ordenamiento.DUENO_ASC -> libros.sortedBy { it.dueno.nombreCompleto.lowercase() }
                        }
                    }

        val preReservasDisponibles = librosDisponibles.map { libro ->
            Reserva(
                libro = libro,
                lector = usuario,
                fechaDesde = filtros.fechaDesde,
                fechaHasta = filtros.fechaHasta
            )
        }

        val cantidadReservas = librosDisponibles.map { reservasPorLibro[it.id] ?: 0 }

        return preReservasDisponibles to cantidadReservas
    }

    fun calificar(resenia: ReseniaReqDTO) {
        val reserva = reservaRepositorio.getById(resenia.reservaId)
        reserva.agregarReseña(resenia.puntuacion, resenia.comentario)
    }

    fun getDetalleReserva(libroId: Int, userId:Int, fechaDesde: LocalDate, fechaHasta: LocalDate): ReservaSimulada {
        val libro = libroRepositorio.getById(libroId)
        val usuario = usuarioRepositorio.getById(userId)

        //Reserva simulada con fechas del front
        val reserva = Reserva(
            libro= libro,
            lector = usuario,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta,
        )
        val cantidadReservas = reservaRepositorio.buscarPorLibro(libro).size
        val biblioKarmas = reserva.calcularBiblioKarmas(cantidadReservas)
        val promedio = reservaRepositorio.getPuntacionPromedio(libro)
        val ultimasReservas = reservaRepositorio.getUltimasReservas(libro)
        val reservasOcupadas = reservaRepositorio.getReservasOcupadas(libro)

        return ReservaSimulada(
            reserva = reserva,
            cantidadReservasHistoricas = cantidadReservas,
            promedio = promedio,
            ultimasReservas = ultimasReservas,
            reservasOcupadas = reservasOcupadas,
            bibliokarmas = biblioKarmas
        )
    }
    fun crearReserva(userID: Int, reserva: CrearReservaDTO){

      val libro = libroRepositorio.getById(reserva.libroId)
      val usuario = usuarioRepositorio.getById(userID)

      val nuevaReserva = Reserva(
        libro = libro,
        lector = usuario,
        fechaDesde = reserva.fechaDesde,
        fechaHasta = reserva.fechaHasta,
      )

      val reservasActivas = reservaRepositorio.buscarPorLibro(libro).filter { !it.estaFinalizada() }
      val seSuperpone = reservasActivas.any{ it.seSuperponeCon(reserva.fechaDesde, reserva.fechaHasta) }

      if( seSuperpone){
        throw IllegalStateException("El libro ya se encuentra reservado en esas fechas.")
      }

        val cantidadReservas = reservaRepositorio.buscarPorLibro(libro).size
        val bibliokarmasGanados = nuevaReserva.calcularBiblioKarmas(cantidadReservas)
        usuario.aumentarBiblioKarmas(bibliokarmasGanados)
        usuarioRepositorio.update(usuario)
      reservaRepositorio.create(nuevaReserva)
    }
}
