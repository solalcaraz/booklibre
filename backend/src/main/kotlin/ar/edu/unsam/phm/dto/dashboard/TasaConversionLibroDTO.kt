package ar.edu.unsam.phm.dto.dashboard

data class TasaConversionLibroDTO(
    val libroId: String,
    val titulo: String,
    val autor: String,
    val clicks: Int,
    val reservas: Int,
    val tasaConversion: Double
)