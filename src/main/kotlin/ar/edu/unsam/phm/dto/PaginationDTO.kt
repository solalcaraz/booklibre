package ar.edu.unsam.phm.dto

data class PaginationResponseDTO<TData>(
    val items: List<TData>,
    val page: Int,
    val size: Int,
    val totalItems: Int,
    val totalPages: Int
)

data class PaginationReqDTO<TFilter>(
    val pageSize: Int,
    val pageNumber: Int,
    val filter: TFilter ?
)
