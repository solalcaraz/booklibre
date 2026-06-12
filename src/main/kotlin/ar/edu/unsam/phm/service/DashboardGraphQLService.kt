package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.dto.dashboard.PromedioCalificacionPorTipo
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.dto.dashboard.TasaConversionLibroDTO
import ar.edu.unsam.phm.dto.dashboard.ActividadRecienteDTO
import ar.edu.unsam.phm.dto.dashboard.NuevaReservaEventoDTO
import ar.edu.unsam.phm.dto.dashboard.NuevoLibroEventoDTO
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.LibroPopularRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import ar.edu.unsam.phm.dto.dashboard.EstadoSaludCatalogoDTO

@Service
class DashboardGraphQLService(
    private val libroRepository: LibroDocumentRepository,
    private val libroPopularRepository: LibroPopularRepository,
    private val reservaRepository: ReservaRepository
) {
    fun analisisCalificaciones(): List<PromedioCalificacionPorTipo> =
        libroRepository.promedioCalificacionPorTipo()
            .map {
                PromedioCalificacionPorTipo(
                    tipo = tipoDesdeAliasMongo(it.tipo),
                    puntajePromedio = it.puntajePromedio
                )
            }
            .sortedBy { it.tipo.ordinal }

    private fun tipoDesdeAliasMongo(alias: String): TipoDeLibroDTO =
        when (alias.substringAfterLast(".")) {
            "COMUN", "LibroComunDocument" -> TipoDeLibroDTO.COMUN
            "DEDICATORIA", "LibroDedicatoriaDocument" -> TipoDeLibroDTO.DEDICATORIA
            "COLECCIONABLE", "LibroColeccionableDocument" -> TipoDeLibroDTO.COLECCIONABLE
            else -> throw IllegalArgumentException("Tipo de libro desconocido: $alias")
        }

    fun tasaConversionClicksReservas(): List<TasaConversionLibroDTO> {
        /* me agarro los 5 libros con mas clicks de redis, en orden descendente */
        val topLibros = libroPopularRepository.findTop5ByClicksDesc()

        /* si no tengo, devuelvo lista vacia para no dividir por 0 */
        if (topLibros.isEmpty()) return emptyList()

        /* busco las reservas de los 5 libros con una sola query mandandole el array de ids */
        val reservasPorLibro = reservaRepository.contarReservasPorLibroMongoIdIn(topLibros.map { it.libroId })
            .associate { it.libroId to it.reservas.toInt() }

        /* convierto a dto y devuelvo */
        return topLibros.map { libro ->
            val reservas = reservasPorLibro[libro.libroId] ?: 0
            TasaConversionLibroDTO(
                libroId = libro.libroId,
                titulo = libro.titulo,
                autor = libro.autor,
                clicks = libro.clicks,
                reservas = reservas,
                tasaConversion = reservas.toDouble() / libro.clicks
            )
        }
    }



    fun estadoSaludCatalogo(): EstadoSaludCatalogoDTO {
        val libros = libroRepository.findAll()
        val hoy = LocalDate.now()

        var prestados = 0
        var nuncaReservados = 0
        var devueltos = 0
        var reservadosFuturo = 0

        libros.forEach { libro ->
            val reservas = libro.reservasPendientes ?: emptyList()

            val estaPrestadoHoy = reservas.any { it.fechaInicio <= hoy && it.fechaFin >= hoy }
            val tienePasadas = reservas.any { it.fechaFin < hoy }
            val tieneFuturas = reservas.any { it.fechaInicio > hoy }

            when {
                estaPrestadoHoy -> prestados++
                reservas.isEmpty() -> nuncaReservados++
                tienePasadas -> devueltos++
                tieneFuturas -> reservadosFuturo++
            }
        }

        return EstadoSaludCatalogoDTO(
            total = libros.size,
            prestados = prestados,
            disponiblesNuncaReservados = nuncaReservados,
            disponiblesReservadosAFuturo = reservadosFuturo,
            disponiblesDevueltos = devueltos
        )
    }

    fun feedActividadReciente(): List<ActividadRecienteDTO> {
        // 1. Buscar los últimos 5 libros en Mongo
        val ultimosLibros = libroRepository.findTop5ByOrderByCreatedAtDesc().map { libro ->
            NuevoLibroEventoDTO(
                fecha = libro.createdAt,
                usuario = libro.dueno.nombreCompleto,
                libroId = libro.id!!,
                titulo = libro.titulo
            )
        }

        // 2. Buscar las últimas 5 reservas en Postgres
        val ultimasReservas = reservaRepository.findTop5ByOrderByFechaDesdeDesc().map { reserva ->
            NuevaReservaEventoDTO(
                fecha = reserva.fechaDesde.atStartOfDay(), // O el campo exacto de timestamp de la reserva
                usuario = reserva.lector.nombreCompleto,
                reservaId = reserva.id!!
            )
        }

        // 3. Unificar, ordenar cronológicamente descendente y tomar el Top 5
        return (ultimosLibros + ultimasReservas)
            .sortedByDescending { it.fecha }
            .take(5)
    }
}
