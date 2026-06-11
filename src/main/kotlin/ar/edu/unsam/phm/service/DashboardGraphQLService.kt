package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.dto.dashboard.PromedioCalificacionPorTipo
import ar.edu.unsam.phm.dto.TipoDeLibroDTO
import ar.edu.unsam.phm.dto.dashboard.TasaConversionLibroDTO
import ar.edu.unsam.phm.domain.LibroPopular
import ar.edu.unsam.phm.dto.dashboard.ActividadRecienteDTO
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.LibroPopularRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import ar.edu.unsam.phm.dto.dashboard.EstadoSaludCatalogoDTO
import java.time.ZoneId

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

//    fun tasaConversionClicksReservas(): List<TasaConversionLibroDTO> {
//        /* me agarro los 5 libros con mas clicks de redis, en orden descendente */
//        val topLibros = libroPopularRepository.findAll()
//            .filter { it.clicks > 0 }
//            .sortedWith(compareByDescending<LibroPopular> { it.clicks }.thenBy { it.titulo })
//            .take(5)
//
//        /* si no tengo, devuelvo lista vacía para no dividir por 0 y que explote hiroshima */
//        if (topLibros.isEmpty()) return emptyList()
//
//        /* busco las reservas de los 5 libros con una sola query mandandole el array de ids */
//        val reservasPorLibro = reservaRepository.contarReservasPorLibroMongoIdIn(topLibros.map { it.libroId })
//            .associate { it.libroId to it.reservas.toInt() }
//
//        /* convierto a dto y devuelvo */
//        return topLibros.map { libro ->
//            val reservas = reservasPorLibro[libro.libroId] ?: 0
//            TasaConversionLibroDTO(
//                libroId = libro.libroId,
//                titulo = libro.titulo,
//                autor = libro.autor,
//                clicks = libro.clicks,
//                reservas = reservas,
//                tasaConversion = reservas.toDouble() / libro.clicks
//            )
//        }
//    }

    private fun tipoDesdeAliasMongo(alias: String): TipoDeLibroDTO =
        when (alias.substringAfterLast(".")) {
            "COMUN", "LibroComunDocument" -> TipoDeLibroDTO.COMUN
            "DEDICATORIA", "LibroDedicatoriaDocument" -> TipoDeLibroDTO.DEDICATORIA
            "COLECCIONABLE", "LibroColeccionableDocument" -> TipoDeLibroDTO.COLECCIONABLE
            else -> throw IllegalArgumentException("Tipo de libro desconocido: $alias")
        }

    fun estadoSaludCatalogo(): EstadoSaludCatalogoDTO {
        val hoy = LocalDate.now()
        var prestados = 0
        var devueltos = 0
        var reservadosFuturo = 0

        // 1. La verdad absoluta de la existencia (Mongo)
        val totalLibros = libroRepository.count().toInt()

        // 2. La verdad absoluta del historial (Postgres)
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

        // 3. Cruce de verdades (bases)
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
        // zona horaria para que el cálculo del timestamp sea exacto
        val zonaHoraria = ZoneId.of("America/Argentina/Buenos_Aires")

        // 1. Mapeo de Libros (LocalDateTime -> Timestamp)
        val ultimosLibros = libroRepository.findTop5ByOrderByCreatedAtDesc().map { libro ->
            val epochMillis = libro.createdAt.atZone(zonaHoraria).toInstant().toEpochMilli()

            ActividadRecienteDTO(
                timestamp = epochMillis,
                tipoEvento = "ALTA_LIBRO",
                usuario = libro.dueno?.nombreCompleto ?: "Sistema",
                libroId = libro.id!!, // Llenas estos dos
                titulo = libro.titulo
            )
        }

        // 2. Mapeo de Reservas (LocalDate -> Timestamp)
        val ultimasReservas = reservaRepository.findTop5ByOrderByFechaDesdeDesc().map { reserva ->
            // Se agrega el "inicio del día" (00:00) para poder convertirlo
            val epochMillis = reserva.fechaDesde.atStartOfDay(zonaHoraria).toInstant().toEpochMilli()

            ActividadRecienteDTO(
                timestamp = epochMillis,
                tipoEvento = "NUEVA_RESERVA",
                usuario = reserva.lector.nombreCompleto,
                reservaId = reserva.id!! // Llenas solo este
                // libroId y titulo quedan en null por defecto
            )
        }

        // 3. Unificar, ordenar usando el Long y tomar el Top 5
        return (ultimosLibros + ultimasReservas)
            .sortedByDescending { it.timestamp }
            .take(5)
    }
}
