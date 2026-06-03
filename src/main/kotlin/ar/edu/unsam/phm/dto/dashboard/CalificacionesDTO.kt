package ar.edu.unsam.phm.dto.dashboard

import ar.edu.unsam.phm.dto.TipoDeLibroDTO

data class PromedioCalificacionPorTipo(
    val tipo: TipoDeLibroDTO,
    val puntajePromedio: Double
)

data class PromedioCalificacionPorTipoMongo(
    val tipo: String = "",
    val puntajePromedio: Double = 0.0
)

data class TasaConversionLibroDTO(
    val libroId: String,
    val titulo: String,
    val autor: String,
    val clicks: Int,
    val reservas: Int,
    val tasaConversion: Double
)
