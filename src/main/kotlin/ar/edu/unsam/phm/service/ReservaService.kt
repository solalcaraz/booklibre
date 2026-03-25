package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.BusquedaPor
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.helpers.Ordenamiento
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.dto.CrearReservaDTO
import ar.edu.unsam.phm.dto.DetalleReservaDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReseniaReqDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.dto.ReservaLibroDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.dto.toReservaLibroDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.ReservaAdapter
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReservaService(
    private val reservaRepositorio: ReservaRepositorio,
    private val libroRepositorio: LibroRepositorio,
    private val usuarioRepositorio: UsuarioRepositorio,
    private val reservaAdapter: ReservaAdapter
) {
    fun getMisReservas(payload: PaginationReqDTO<ReservaRequestDTO>): PaginationResponseDTO<Reserva> {
        var reservas = reservaRepositorio.getAll() // filtros en el repo
        // filtrado por tab si es prestado por mi o los que preste
        if (payload.filter?.busquedaPor == BusquedaPor.TAB_PRESTADOS_POR_MI.value) {
            reservas = reservas.filter { it.libro.data.dueno.id.toString() == payload.filter.usuarioId }
        } else if (payload.filter?.busquedaPor == BusquedaPor.TAB_PRESTADOS_A_MI.value) {
            reservas =
                reservas.filter { it.lector.id.toString() == payload.filter.usuarioId } // componer en una funcion privada el filtro
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

    fun getDetalleReserva(libroId: Int, userId: Int, fechaDesde: LocalDate, fechaHasta: LocalDate): DetalleReservaDTO {
        val libro = libroRepositorio.getById(libroId)
        val usuario = usuarioRepositorio.getById(userId)

        //Reserva simulada con fechas del front
        var reserva: Reserva
        try {
            reserva = Reserva(
                libro = libro,
                lector = usuario,
                fechaDesde = fechaDesde,
                fechaHasta = fechaHasta,
            )
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message ?: "Fechas invalidas para la reserva")
        }

        val cantidadReservas = reservaRepositorio.buscarPorLibro(libro).size
        val biblioKarmas = reserva.calcularBiblioKarmas(cantidadReservas)
        val puntuacionesLibro = reservaRepositorio.getPuntuacionesByLibro(libroId)
        val calificacionPromedio =
            if (puntuacionesLibro.isEmpty()) 0.0 else puntuacionesLibro.average() //No tiene ternario :(
        val ultimasReservas = reservaRepositorio.getUltimasReservas(libro)
        val reservasOcupadas = reservaRepositorio.getReservasOcupadas(libro)

        return reservaAdapter.toDetalleReservaDto(
            reserva = reserva,
            bibliokarmas = biblioKarmas,
            calificacionPromedio = calificacionPromedio,
            ultimasReservas = ultimasReservas,
            reservasOcupadas = reservasOcupadas
        )
    }

    fun crearReserva(userID: Int, reserva: CrearReservaDTO): Reserva {

        val libro = libroRepositorio.getById(reserva.libroId)
        val usuario = usuarioRepositorio.getById(userID)

        var nuevaReserva: Reserva
        try {
            nuevaReserva = Reserva(
                libro = libro,
                lector = usuario,
                fechaDesde = reserva.fechaDesde,
                fechaHasta = reserva.fechaHasta,
            )
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message ?: "Fechas inválidas para la reserva")
        }

        val seSuperpone = reservaRepositorio.tieneReservasSuperpuestas(libro, reserva.fechaDesde, reserva.fechaHasta)
        if (seSuperpone) {
            throw ConflictException("El libro ya se encuentra reservado en esas fechas.") //otra exception, el usuario la pifio
        }

        //Validacion para que el usuario no reserve mas de una vez.
        val usuarioYaLoReservo = reservaRepositorio.usuarioTieneReservaActiva(libro, usuario)
        if( usuarioYaLoReservo ) {
            throw ConflictException("Ya tenés una reserva activa para este libro. Dejá leer a los demás chimwe")
        }

        val cantidadReservas = reservaRepositorio.buscarPorLibro(libro).size
        val bibliokarmasGanados = nuevaReserva.calcularBiblioKarmas(cantidadReservas)
        usuario.aumentarBiblioKarmas(bibliokarmasGanados)
        usuarioRepositorio.update(usuario)
        reservaRepositorio.create(nuevaReserva)
        return nuevaReserva
    }
}
