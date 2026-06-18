import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import DataGrid from '../components/DataGrid'
import type { PagResModel } from '../models/PaginationModel'

type Item = { id: number; title: string }

const renderDataGrid = (fetchFunction: (request: unknown) => Promise<PagResModel<Item>>, filter?: unknown) => {
  render(
    <MemoryRouter>
      <DataGrid
        fetchFunction={fetchFunction}
        filter={filter}
        pageSize={2}
        renderItem={(item: Item) => <div data-testid={`item-${item.id}`}>{item.title}</div>}
      />
    </MemoryRouter>
  )
}

describe('DataGrid', () => {
  it('muestra spinner al cargar y luego los items', async () => {
    const fetchFunction = vi.fn().mockResolvedValue({
      items: [{ id: 1, title: 'Elemento 1' }],
      page: 0,
      size: 2,
      totalItems: 1,
      totalPages: 1
    })

    renderDataGrid(fetchFunction)

    expect(screen.getByTestId('spinner')).toBeInTheDocument()
    expect(await screen.findByTestId('item-1')).toBeInTheDocument()
    expect(fetchFunction).toHaveBeenCalledWith(expect.objectContaining({ pageSize: 2, pageNumber: 0 }))
  })

  it('muestra mensaje cuando no hay resultados', async () => {
    const fetchFunction = vi.fn().mockResolvedValue({
      items: [],
      page: 0,
      size: 2,
      totalItems: 0,
      totalPages: 0
    })

    renderDataGrid(fetchFunction)

    expect(await screen.findByText('No se encontraron resultados para los filtros aplicados.')).toBeInTheDocument()
  })

  it('muestra el ErrorCard cuando fetch falla', async () => {
    const fetchFunction = vi.fn().mockRejectedValue(new Error('boom'))

    renderDataGrid(fetchFunction)

    expect(await screen.findByTestId('error-card')).toBeInTheDocument()
  })

  it('permite cambiar de pagina y vuelve a llamar al fetch', async () => {
    const fetchFunction = vi.fn()
      .mockResolvedValueOnce({
        items: [{ id: 1, title: 'Elemento 1' }],
        page: 0,
        size: 2,
        totalItems: 3,
        totalPages: 2
      })
      .mockResolvedValueOnce({
        items: [{ id: 2, title: 'Elemento 2' }],
        page: 1,
        size: 2,
        totalItems: 3,
        totalPages: 2
      })

    renderDataGrid(fetchFunction, { any: 'filter' })

    expect(await screen.findByTestId('item-1')).toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: '2' }))

    await waitFor(() => {
      expect(fetchFunction).toHaveBeenCalledTimes(2)
    })

    expect(fetchFunction).toHaveBeenLastCalledWith(expect.objectContaining({ pageSize: 2, pageNumber: 1, filter: { any: 'filter' } }))
  })
})