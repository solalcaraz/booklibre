package ar.edu.unsam.phm.helpers

import ar.edu.unsam.phm.dto.PaginationResponseDTO

fun <T> paginate(
    items: List<T>,
    page: Int = 0,
    size: Int = 6
): PaginationResponseDTO<T> {
    val totalItems = items.size
    val totalPages = if (totalItems == 0) 0 else (totalItems + size - 1) / size

    val fromIndex = (page * size).coerceAtMost(totalItems)
    val toIndex = (fromIndex + size).coerceAtMost(totalItems)

    val pageItems = items.subList(fromIndex, toIndex)

    return PaginationResponseDTO(
        items = pageItems,
        page = page,
        size = size,
        totalItems = totalItems,
        totalPages = totalPages
    )
}
