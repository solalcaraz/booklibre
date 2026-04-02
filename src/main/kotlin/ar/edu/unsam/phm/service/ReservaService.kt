package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.BusquedaPor
import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.dto.CrearReservaDTO
import ar.edu.unsam.phm.dto.DetalleReservaDTO
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.dto.ReseniaReqDTO
import ar.edu.unsam.phm.dto.ReservaLibroDTO
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.dto.toReservaLibroDTO
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.ReservaAdapter
import ar.edu.unsam.phm.helpers.paginate
import ar.edu.unsam.phm.repository.LibroRepositorio
import ar.edu.unsam.phm.repository.ReseniaRepositorio
import ar.edu.unsam.phm.repository.ReservaRepositorio
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReservaService(
    private val reservaRepositorio: ReservaRepositorio,
    private val libroRepositorio: LibroRepositorio,
    private val usuarioRepository: UsuarioRepository,
    private val reseniaRepositorio: ReseniaRepositorio,
    private val reservaAdapter: ReservaAdapter
) {
    fun getMisReservas(payload: PaginationReqDTO<ReservaRequestDTO>): PaginationResponseDTO<Reserva> {
        val usuarioId = payload.filter?.usuarioId ?: throw BadRequestException("Falta el ID del usuario en el payload")
        return reservaRepositorio.getPrestadosAMi(usuarioId, payload.pageNumber, payload.pageSize, payload.filter.inputSearch)
    }

    fun getPrestadosPorMi(payload: PaginationReqDTO<ReservaRequestDTO>): PaginationResponseDTO<Reserva> {
        val usuarioId = payload.filter?.usuarioId ?: throw BadRequestException("Falta el ID del usuario en el payload")
        return reservaRepositorio.getPrestadosPorMi(usuarioId, payload.pageNumber, payload.pageSize, payload.filter.inputSearch)
    }

    fun getLibrosDisponiblesParaReserva(usuarioId: Int, filtros: FiltrosLibro): PaginationResponseDTO<ReservaLibroDTO> {
        // valido que el id del usuario existe y lo asigno
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NotFoundException("Usuario no encontrado") }

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
        val nuevaResenia = Resenia(reserva.libro.id!!, reserva.lector.nombreCompleto, resenia.puntuacion, resenia.comentario)
        reserva.validarReseña(nuevaResenia)
        reseniaRepositorio.create(nuevaResenia)
        reserva.resenia = nuevaResenia
    }

    fun getDetalleReserva(libroId: Int, userId: Int, fechaDesde: LocalDate, fechaHasta: LocalDate): DetalleReservaDTO {
        val libro = libroRepositorio.getById(libroId)
        val usuario = usuarioRepository.findById(userId).orElseThrow { NotFoundException("Usuario no encontrado") }

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

    fun crearReserva(userID: Int, reserva: CrearReservaDTO): Pair<Reserva, Int> {

        val libro = libroRepositorio.getById(reserva.libroId)
        val usuario = usuarioRepository.findById(userID).orElseThrow { NotFoundException("Usuario no encontrado") }

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
        usuarioRepository.save(usuario)
        reservaRepositorio.create(nuevaReserva)
        return Pair(nuevaReserva, cantidadReservas)
    }

    fun getCantidadDeReservasPorLibro(librosIds: List<Int>): Map<Int, Int>  {
        return librosIds.associateWith { libroId ->
            val libro = libroRepositorio.getById(libroId)
            reservaRepositorio.buscarPorLibro(libro).size
        }
    }
}
