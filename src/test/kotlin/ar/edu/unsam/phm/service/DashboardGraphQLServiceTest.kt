package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.LibroPopular
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.LibroPopularRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.ReservasPorLibroProjection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

class DashboardGraphQLServiceTest {
    private val libroRepository = mock<LibroDocumentRepository>()
    private val libroPopularRepository = mock<LibroPopularRepository>()
    private val reservaRepository = mock<ReservaRepository>()
    private val service = DashboardGraphQLService(libroRepository, libroPopularRepository, reservaRepository)

    @Test
    fun `tasaConversionClicksReservas devuelve top 5 y calcula conversion`() {
        /* mockeo los libros populares */
        `when`(libroPopularRepository.findAll()).thenReturn(
            listOf(
                libroPopular("1", "Dune", 10),
                libroPopular("2", "Rayuela", 20),
                libroPopular("3", "1984", 30),
                libroPopular("4", "Orgullo y prejuicio", 40),
                libroPopular("5", "El gran Gatsby", 50),
                libroPopular("6", "Cien anios de soledad", 60)
            )
        )

        /* mockeo la llamada a postgres con la cantidad de reservas por libro*/
        `when`(reservaRepository.contarReservasPorLibroMongoIdIn(listOf("6", "5", "4", "3", "2"))).thenReturn(
            listOf(
                reservasPorLibro("6", 12),
                reservasPorLibro("5", 5),
                reservasPorLibro("4", 0),
                reservasPorLibro("3", 15)
            )
        )

        val resultado = service.tasaConversionClicksReservas()

        assertEquals(listOf("6", "5", "4", "3", "2"), resultado.map { it.libroId })
        assertEquals(12, resultado[0].reservas)
        assertEquals(0.2, resultado[0].tasaConversion)
        assertEquals(5, resultado[1].reservas)
        assertEquals(0.1, resultado[1].tasaConversion)
        assertEquals(0, resultado[4].reservas)
        assertEquals(0.0, resultado[4].tasaConversion)
        verify(reservaRepository).contarReservasPorLibroMongoIdIn(listOf("6", "5", "4", "3", "2"))
    }

    @Test
    fun `tasaConversionClicksReservas devuelve vacio si Redis no tiene libros con clicks`() {
        `when`(libroPopularRepository.findAll()).thenReturn(
            listOf(libroPopular("1", "Dune", 0))
        )

        val resultado = service.tasaConversionClicksReservas()

        assertEquals(emptyList<Any>(), resultado)
        verify(reservaRepository, never()).contarReservasPorLibroMongoIdIn(any())
    }

    private fun libroPopular(id: String, titulo: String, clicks: Int) =
        LibroPopular(
            libroId = id,
            titulo = titulo,
            autor = "Autor $id",
            tipo = "Comun",
            genero = "Novela",
            idioma = "Espanol",
            estado = "Bueno",
            dueno = "Dueno $id",
            isbn13 = "978000000000$id",
            portada = "portada-$id",
            calificacionPromedio = 0.0,
            clicks = clicks
        )

    private fun reservasPorLibro(id: String, reservas: Long) =
        object : ReservasPorLibroProjection {
            override val libroId = id
            override val reservas = reservas
        }
}
