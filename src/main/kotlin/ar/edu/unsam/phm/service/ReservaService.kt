package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.ReservaPendiente
import ar.edu.unsam.phm.domain.Usuario
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
import ar.edu.unsam.phm.helpers.Ordenamiento
import ar.edu.unsam.phm.helpers.ReservaAdapter
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.ReseniaRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ReservaService(
    private val reservaRepository: ReservaRepository,
    private val libroRepository: LibroDocumentRepository,
    private val usuarioRepository: UsuarioRepository,
    private val reservaAdapter: ReservaAdapter,
    private val reseniaRepository: ReseniaRepository,
    private val libroPopularRedisService: LibroPopularRedisService,
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
        return reservaRepository.findPrestadosPorMi(usuarioId.toInt(), payload.filter.inputSearch.trim(), pageable)
    }

    @Transactional(readOnly = true)
    fun getLibrosDisponiblesParaReserva(usuarioId: Int, filtros: FiltrosLibro): Page<ReservaLibroDTO> {
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NotFoundException("Usuario no encontrado") }

        // Solo la pagina 0 y 1 podrian aplicar una logica de paginacion hibrida
        if (filtros.numeroPagina < 2 && filtros.orden == Ordenamiento.POPULARIDAD) return resolverPaginadoHibrido(usuario, filtros)

        // pagina 2+ se resuelve desde mongo directo
        val pageDeLibros = libroRepository.findDisponiblesParaReserva(usuarioId, filtros)
        // se transforma cada LibroDocument en una pre reserva y hace el mapeo (dto)
        return pageDeLibros.map { libro ->
            Reserva(
                lector = usuario,
                fechaDesde = filtros.fechaDesde,
                fechaHasta = filtros.fechaHasta,
                libroMongoId = libro.id!!,
            ).toReservaLibroDTO(libro)
        }
    }

    private fun resolverPaginadoHibrido(usuario: Usuario, filtros: FiltrosLibro): Page<ReservaLibroDTO> {
        // rango absoluto que le corresponde a esta pagina dentro del top de redis
        // pagina 0: [0..5], pagina 1: [6..11], etc
        val desdeAbsoluto = filtros.numeroPagina * filtros.tamanoPaginado
        val hastaAbsoluto = desdeAbsoluto + filtros.tamanoPaginado - 1

        // si el rango esta dentro del top 10, entonces pueden haber libros de redis
        val hayAlgoEnRedis = desdeAbsoluto < 10

        val dtoDesdeRedis: List<ReservaLibroDTO> =
            if (hayAlgoEnRedis) {
                // si hay libros para mostrar de redis, cuantos son los libros que corresponden a esa pagina?
                val hastaRedis = minOf(hastaAbsoluto, 10 - 1)
                val cantidadRedis = hastaRedis - desdeAbsoluto + 1

                // pedir a zset exactamente los libros de slice del ranking
                val librosRedis = libroPopularRedisService.obtenerTopLibrosOrdenados(desde = desdeAbsoluto.toLong(), hasta = hastaRedis.toLong())

                librosRedis.map { libro ->
                    Reserva(
                        lector = usuario,
                        fechaDesde = filtros.fechaDesde,
                        fechaHasta = filtros.fechaHasta,
                        libroMongoId = libro.libroId
                    ).toReservaDTODesdeRedis(libro)
                }
            } else emptyList()

        // cuantos libros faltan para completar la pagina ?
        val faltantes = filtros.tamanoPaginado - dtoDesdeRedis.size

        //info de la pagina para PageImpl
        val pageable: Pageable = PageRequest.of(
            filtros.numeroPagina, filtros.tamanoPaginado, filtros.orden.toSort()
        )
        if (faltantes <= 0) {
            return PageImpl(dtoDesdeRedis, pageable, 50)//calcularTotalMongo(filtros))
        }

        // Complemento desde Mongo:
        // - Excluye los IDs ya traídos de Redis
        //   (los primeros 10 libros ya los maneja Redis, el resto es Mongo)
        val idsYaCargados = dtoDesdeRedis.mapNotNull { it.id }

        val complementoDeMongo = libroRepository.findDisponiblesParaReserva(
            usuario.id!!, filtros.copy(numeroPagina = 0, tamanoPaginado = faltantes), idsYaCargados
        )

        val dtosDesdeMongo = complementoDeMongo.content.map { libro ->
            Reserva(
                lector        = usuario,
                fechaDesde    = filtros.fechaDesde,
                fechaHasta    = filtros.fechaHasta,
                libroMongoId  = libro.id!!
            ).toReservaLibroDTO(libro)
        }

        return PageImpl(
            dtoDesdeRedis + dtosDesdeMongo,
            PageRequest.of(filtros.numeroPagina, filtros.tamanoPaginado, filtros.orden.toSort()),
            50//calcularTotalMongo(filtros)   // total sin excluir, para que el paginador del front sea correcto
        )
    }
//    private fun calcularTotalMongo(filtros: FiltrosLibro): Long =
//        libroRepository.countDisponiblesParaReserva(filtros)

    @Transactional(readOnly = true)
    fun getDetalleReserva(libroId: String, userId: Int, fechaDesde: LocalDate, fechaHasta: LocalDate): DetalleReservaDTO {
        val libro = libroRepository.findById(libroId).orElseThrow { NotFoundException("Libro no encontrado") }
        val usuario = usuarioRepository.findById(userId).orElseThrow{NotFoundException("Usuario no encontrado")}

        //Reserva simulada con fechas del front
        var reserva = Reserva(
            lector = usuario,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta,
            libroMongoId = libroId
        )

        val biblioKarmas = reserva.calcularBiblioKarmas(libro)
        val calificacionPromedio = libro.promedioCalificacion
        val ultimasResenias = reseniaRepository.findTop2ByLibroMongoIdOrderByFechaDesc(libroId);
        val reservasOcupadas = reservaRepository.findReservasOcupadas(libroId)

        return reservaAdapter.toDetalleReservaDto(
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

        var nuevaReserva = Reserva(
            lector = usuario,
            fechaDesde = reserva.fechaDesde,
            fechaHasta = reserva.fechaHasta,
            libroMongoId = reserva.libroMongoID
        )
        val libroResumen = LibroResumen(libro.titulo, libro.autor, libro.portada, libro.dueno.nombreCompleto, libro.dueno.id)
        nuevaReserva.libroResumen = libroResumen

        val seSuperpone = reservaRepository.existReservaSuperpuesta(libro.id!!, reserva.fechaDesde, reserva.fechaHasta)
        if (seSuperpone) {
            throw ConflictException("El libro ya se encuentra reservado en esas fechas.") //otra exception, el usuario la pifio
        }

        val bibliokarmasGanados = nuevaReserva.calcularBiblioKarmas(libro)
        nuevaReserva.bibliokarmasGanados = bibliokarmasGanados
        usuario.aumentarBiblioKarmas(bibliokarmasGanados)
        usuarioRepository.save(usuario)

        //Sincronizacion entre repoReservas y libroDocument.

        val reservaGuardada = reservaRepository.save(nuevaReserva)

        val reservaPendienteMongo = ReservaPendiente(
            idReserva = reservaGuardada.id!!,
            fechaInicio = reservaGuardada.fechaDesde,
            fechaFin = reservaGuardada.fechaHasta,
        )

        libro.agregarReserva(reservaPendienteMongo)
        libroRepository.save(libro)
        return reservaAdapter.toDto(nuevaReserva)
    }
}
