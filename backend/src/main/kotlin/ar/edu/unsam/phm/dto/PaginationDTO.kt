package ar.edu.unsam.phm.dto

import org.springframework.data.domain.Page

data class PaginationResponseDTO<TData>(
    val items: List<TData>,
    val page: Int,
    val size: Int,
    val totalItems: Int,
    val totalPages: Int
)

fun <T> Page<T>.toPaginationDTO() = PaginationResponseDTO(
    items = content,
    page = number,
    size = size,
    totalItems = totalElements.toInt(),
    totalPages = totalPages
)

data class PaginationReqDTO<TFilter>(
    val pageSize: Int,
    val pageNumber: Int,
    val filter: TFilter ?
)
