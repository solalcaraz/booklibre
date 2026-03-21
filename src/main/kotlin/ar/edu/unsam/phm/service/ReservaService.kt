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
import ar.edu.unsam.phm.dto.ReservaLibroDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.dto.toReservaLibroDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.NotFoundException
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
        var reservas = reservaRepositorio.getAll() // filtros en el repo
        // filtrado por tab si es prestado por mi o los que preste
        if (payload.filter?.busquedaPor == BusquedaPor.TAB_PRESTADOS_POR_MI.value){
            reservas = reservas.filter { it.libro.data.dueno.id.toString() == payload.filter.usuarioId }
        } else if (payload.filter?.busquedaPor == BusquedaPor.TAB_PRESTADOS_A_MI.value) {
            reservas = reservas.filter { it.lector.id.toString() == payload.filter.usuarioId } // componer en una funcion privada el filtro
        }

        // filtrado por input de busqueda
        val search = payload.filter?.inputSearch?.trim()
        if (!search.isNullOrEmpty()) {
            reservas = reservas.filter {
                it.libro.data.titulo.contains(search, ignoreCase = true) ||
                        it.libro.data.autor.contains(search, ignoreCase = true)
            }
        }
        return paginate(reservas, payload.pageNumber, payload.pageSize)
    }

    fun getLibrosDisponiblesParaReserva(usuarioId: Int, filtros: FiltrosLibro): PaginationResponseDTO<ReservaLibroDTO> {
        // valido que el id del usuario existe y lo asigno
        val usuario = usuarioRepositorio.getById(usuarioId)

        /* filtro los id de los libros que estan ocupados en la fecha de filtros (id porque es un dato mas liviano que traer el objeto completo.
            para luego descartarlos en el filtro que hace el repo de libros porque pueden haber libros disponibles pero que nunca se reservaron
         */
        val idLibrosOcupados = reservaRepositorio.getIdLibrosOcupadosEntre(filtros.fechaDesde, filtros.fechaHasta)

        // trae la pre reserva de los libros filtrados y ordenados
        val reservaDeLibrosDisponibles =
            libroRepositorio.getLibrosDisponibles(filtros, usuario.id!!, idLibrosOcupados).map { libro ->
                val cantidadReservas = reservaRepositorio.getCantidadReservasPorLibro(libro.id!!)
                val puntuaciones = reservaRepositorio.getPuntuacionesByLibro(libro.id!!)
                Reserva(
                    libro = libro, lector = usuario, fechaDesde = filtros.fechaDesde, fechaHasta = filtros.fechaHasta
                ).toReservaLibroDTO(cantidadReservas, puntuaciones)
            }

        return paginate(reservaDeLibrosDisponibles, filtros.numeroPagina, filtros.tamanoPaginado)
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

        return ReservaSimulada(  // DTO , chau data class
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

      val reservasActivas = reservaRepositorio.buscarPorLibro(libro).filter { !it.estaFinalizada() } // filtro al repo
      val seSuperpone = reservasActivas.any{ it.seSuperponeCon(reserva.fechaDesde, reserva.fechaHasta) }

      if( seSuperpone){
        throw IllegalStateException("El libro ya se encuentra reservado en esas fechas.") //otra exception, el usuario la pifio
      }

        val cantidadReservas = reservaRepositorio.buscarPorLibro(libro).size
        val bibliokarmasGanados = nuevaReserva.calcularBiblioKarmas(cantidadReservas)
        usuario.aumentarBiblioKarmas(bibliokarmasGanados)
        usuarioRepositorio.update(usuario)
      reservaRepositorio.create(nuevaReserva)
    }
}
