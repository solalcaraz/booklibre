package ar.edu.unsam.phm.dto

data class PaginationResponseDTO<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalItems: Int,
    val totalPages: Int
)
