package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.ReservaPendiente
import ar.edu.unsam.phm.dto.PaginationReqDTO
import ar.edu.unsam.phm.dto.CrearReservaDTO
import ar.edu.unsam.phm.dto.DetalleReservaDTO
import ar.edu.unsam.phm.dto.LibroResumen
import ar.edu.unsam.phm.dto.ReservaRequestDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.dto.ReservaLibroDTO
import ar.edu.unsam.phm.dto.toReservaDTODesdeRedis
import ar.edu.unsam.phm.dto.toReservaLibroDTO
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.helpers.ReservaMapper
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.ReseniaRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ReservaService(
    private val reservaRepository: ReservaRepository,
    private val libroRepository: LibroDocumentRepository,
    private val usuarioRepository: UsuarioRepository,
    private val reservaMapper: ReservaMapper,
    private val reseniaRepository: ReseniaRepository,
    private val libroPopularRedisService: LibroPopularRedisService,
    private val usuarioLeaderboardRedisService: UsuarioLeaderboardRedisService,
) {
    @Transactional(readOnly = true)
    fun getMisReservas(payload: PaginationReqDTO<ReservaRequestDTO>): Page<Reserva> {
        val usuarioId = payload.filter?.usuarioId ?: throw BadRequestException("Falta el ID del usuario en el payload")
        val pageable = PageRequest.of(payload.pageNumber, payload.pageSize)
        return reservaRepository.findPrestadosAMi(usuarioId, payload.filter.inputSearch.trim(), pageable)
    }

    fun getPrestadosPorMi(payload: PaginationReqDTO<ReservaRequestDTO>): Page<Reserva> {
        val usuarioId = payload.filter?.usuarioId ?: throw BadRequestException("Falta el ID del usuario en el payload")
        val pageable = PageRequest.of(payload.pageNumber, payload.pageSize)
        return reservaRepository.findPrestadosPorMi(usuarioId, payload.filter.inputSearch.trim(), pageable)
    }

    @Transactional(readOnly = true)
    fun getLibrosDisponiblesParaReserva(usuarioId: Int, filtros: FiltrosLibro): Page<ReservaLibroDTO> {
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NotFoundException("Usuario no encontrado") }

        val pageDeLibros = libroRepository.findDisponiblesParaReserva(usuarioId, filtros)

        // Cada libro se arma como una reserva simulada con las fechas del filtro para calcular sus bibliokarmas
        return pageDeLibros.map { libro ->
            Reserva(
                lector = usuario,
                fechaDesde = filtros.fechaDesde,
                fechaHasta = filtros.fechaHasta,
                libroMongoId = libro.id!!,
            ).toReservaLibroDTO(libro)
        }
    }

    @Transactional(readOnly = true)
    fun getLibrosPopulares(usuarioId: Int): List<ReservaLibroDTO> {
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NotFoundException("Usuario no encontrado") }

        // Consigna: del top 10 del ranking se necesitan al menos 6 libros todavía cacheados (no vencidos)
        val topIds = libroPopularRedisService.obtenerTopIds(limite = 10)
        val librosEnRedis = topIds.mapNotNull { libroPopularRedisService.obtenerLibroPopular(it) }

        // Si no alcanzan, se recachean desde Mongo y el front cae a la búsqueda normal
        if (librosEnRedis.size < 6) {
            val librosDeMongo = libroRepository
                .findByIdInAndActivoTrueAndDisponible(topIds, LocalDate.now(), LocalDate.now())

            librosDeMongo.forEach { libroPopularRedisService.cachearLibro(it) }

            throw NotFoundException("Redis no tiene suficientes libros populares para mostrar")
        }

        return librosEnRedis.take(6).map { libro ->
            Reserva(
                lector = usuario,
                fechaDesde = LocalDate.now(),
                fechaHasta = LocalDate.now(),
                libroMongoId = libro.libroId
            ).toReservaDTODesdeRedis(libro)
        }
    }

    @Transactional(readOnly = true)
    fun getDetalleReserva(libroId: String, userId: Int, fechaDesde: LocalDate, fechaHasta: LocalDate): DetalleReservaDTO {
        val libro = libroRepository.findById(libroId).orElseThrow { NotFoundException("Libro no encontrado") }
        val usuario = usuarioRepository.findById(userId).orElseThrow{NotFoundException("Usuario no encontrado")}

        // Reserva simulada con las fechas elegidas en el front, solo para calcular bibliokarmas
        val reserva = Reserva(
            lector = usuario,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta,
            libroMongoId = libroId
        )

        val biblioKarmas = reserva.calcularBiblioKarmas(libro)
        val calificacionPromedio = libro.promedioCalificacion
        val ultimasResenias = reseniaRepository.findTop2ByLibroMongoIdOrderByFechaDesc(libroId)
        val reservasOcupadas = reservaRepository.findReservasOcupadas(libroId)

        return reservaMapper.toDetalleReservaDto(
            reserva = reserva,
            bibliokarmas = biblioKarmas,
            calificacionPromedio = calificacionPromedio,
            ultimasResenias = ultimasResenias,
            reservasOcupadas = reservasOcupadas,
            libro = libro
        )
    }

    @Transactional(readOnly = false)
    fun crearReserva(userID: Int, reserva: CrearReservaDTO): ReservaDTO {
        val libro = libroRepository.findById(reserva.libroMongoID).orElseThrow { NotFoundException("Libro no encontrado") }
        val usuario = usuarioRepository.findById(userID).orElseThrow { NotFoundException("Usuario no encontrado") }

        val nuevaReserva = Reserva(
            lector = usuario,
            fechaDesde = reserva.fechaDesde,
            fechaHasta = reserva.fechaHasta,
            libroMongoId = reserva.libroMongoID
        )
        val libroResumen = LibroResumen(libro.titulo, libro.autor, libro.portada, libro.dueno.nombreCompleto, libro.dueno.id)
        nuevaReserva.libroResumen = libroResumen

        val seSuperpone = reservaRepository.existReservaSuperpuesta(libro.id!!, reserva.fechaDesde, reserva.fechaHasta)
        if (seSuperpone) {
            throw ConflictException("El libro ya se encuentra reservado en esas fechas.")
        }

        val bibliokarmasGanados = nuevaReserva.calcularBiblioKarmas(libro)
        nuevaReserva.bibliokarmasGanados = bibliokarmasGanados
        usuario.aumentarBiblioKarmas(bibliokarmasGanados)
        usuarioRepository.save(usuario)

        usuarioLeaderboardRedisService.actualizarScoreUsuario(
            usuario.id!!,
            bibliokarmasGanados
        )

        // La reserva vive en Postgres, pero el libro en Mongo guarda una copia de sus fechas
        // para poder filtrar disponibilidad sin cruzar bases
        val reservaGuardada = reservaRepository.save(nuevaReserva)

        val reservaPendienteMongo = ReservaPendiente(
            idReserva = reservaGuardada.id!!,
            fechaInicio = reservaGuardada.fechaDesde,
            fechaFin = reservaGuardada.fechaHasta,
        )

        libro.agregarReserva(reservaPendienteMongo)
        libroRepository.save(libro)
        return reservaMapper.toDto(nuevaReserva)
    }
}
