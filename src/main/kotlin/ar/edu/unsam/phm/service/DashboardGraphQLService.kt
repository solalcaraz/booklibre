package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.dto.dashboard.ActividadRecienteDTO
import ar.edu.unsam.phm.dto.dashboard.EstadoSaludCatalogoDTO
import ar.edu.unsam.phm.dto.dashboard.PromedioCalificacionPorTipo
import ar.edu.unsam.phm.dto.dashboard.TasaConversionLibroDTO
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class DashboardGraphQLService(
    private val libroRepository: LibroDocumentRepository,
    private val libroPopularRedisService: LibroPopularRedisService,
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
        val topLibros = libroPopularRedisService.obtenerTopLibrosConClicks(5)
        if (topLibros.isEmpty()) return emptyList()

        val reservasPorLibro = reservaRepository.contarReservasPorLibroMongoIdIn(topLibros.map { it.libro.libroId })
            .associate { it.libroId to it.reservas.toInt() }

        return topLibros.map { topLibro ->
            val libro = topLibro.libro
            val reservas = reservasPorLibro[libro.libroId] ?: 0
            TasaConversionLibroDTO(
                libroId = libro.libroId,
                titulo = libro.titulo,
                autor = libro.autor,
                clicks = topLibro.clicks,
                reservas = reservas,
                tasaConversion = reservas.toDouble() / topLibro.clicks
            )
        }
    }

    fun estadoSaludCatalogo(): EstadoSaludCatalogoDTO {
        val hoy = LocalDate.now()
        var prestados = 0
        var devueltos = 0
        var reservadosFuturo = 0

        val totalLibros = libroRepository.count().toInt()
        val todasLasReservas = reservaRepository.findAll()
        val reservasPorLibro = todasLasReservas.groupBy { it.libroMongoId }

        reservasPorLibro.values.forEach { reservasDelLibro ->
            val estaPrestadoHoy = reservasDelLibro.any { it.fechaDesde <= hoy && it.fechaHasta >= hoy }
            val tienePasadas = reservasDelLibro.any { it.fechaHasta < hoy }
            val tieneFuturas = reservasDelLibro.any { it.fechaDesde > hoy }

            when {
                estaPrestadoHoy -> prestados++
                tienePasadas -> devueltos++
                tieneFuturas -> reservadosFuturo++
            }
        }

        val nuncaReservados = totalLibros - (prestados + devueltos + reservadosFuturo)

        return EstadoSaludCatalogoDTO(
            total = totalLibros,
            prestados = prestados,
            disponiblesNuncaReservados = nuncaReservados,
            disponiblesReservadosAFuturo = reservadosFuturo,
            disponiblesDevueltos = devueltos
        )
    }

    fun feedActividadReciente(): List<ActividadRecienteDTO> {
        val zonaHoraria = ZoneId.of("America/Argentina/Buenos_Aires")

        val ultimosLibros = libroRepository.findTop5ByOrderByCreatedAtDesc().map { libro ->
            val epochMillis = libro.createdAt.atZone(zonaHoraria).toInstant().toEpochMilli()

            ActividadRecienteDTO(
                timestamp = epochMillis,
                tipoEvento = "ALTA_LIBRO",
                usuario = libro.dueno?.nombreCompleto ?: "Sistema",
                libroId = libro.id!!,
                titulo = libro.titulo
            )
        }

        val ultimasReservas = reservaRepository.findTop5ByOrderByFechaDesdeDesc().map { reserva ->
            val epochMillis = reserva.fechaDesde.atStartOfDay(zonaHoraria).toInstant().toEpochMilli()

            ActividadRecienteDTO(
                timestamp = epochMillis,
                tipoEvento = "NUEVA_RESERVA",
                usuario = reserva.lector.nombreCompleto,
                reservaId = reserva.id!!
            )
        }

        return (ultimosLibros + ultimasReservas)
            .sortedByDescending { it.timestamp }
            .take(5)
    }
}
