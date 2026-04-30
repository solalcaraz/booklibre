import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { Home } from '../../pages/Home'
import { reservaService } from '../../services/ReservaService'

const mockResponse = {
  items: [
    {
      id: 1,
      titulo: 'Rayuela',
      genero: 'Literatura Clásica',
      autor: 'Julio Cortázar',
      tipo: 'DEDICATORIA',
      isbn13: '9788437604947',
      idioma: 'Español',
      estado: 'Bueno',
      dueno: 'Lionel Messi',
      calificacionPromedio: 4.5,
      biblioKarmasTotal: 300,
      portada: 'portada.jpg'
    },
    {
      id: 2,
      titulo: '1984',
      genero: 'Ciencia Ficción',
      autor: 'George Orwell',
      tipo: 'COMUN',
      isbn13: '9780451524935',
      idioma: 'Inglés',
      estado: 'Muy bueno',
      dueno: 'Diego Maradona',
      calificacionPromedio: 5,
      biblioKarmasTotal: 200,
      portada: 'portada2.jpg'
    }
  ],
  page: 0,
  size: 3,
  totalItems: 2,
  totalPages: 1
}

vi.mock('react-router', () => ({
  useNavigate: vi.fn(() => vi.fn())
}))

vi.mock('../../services/ReservaService', () => {
  const mockResponse = {
    items: [
      {
        id: 1,
        titulo: 'Rayuela',
        genero: 'Literatura Clásica',
        autor: 'Julio Cortázar',
        tipo: 'DEDICATORIA',
        isbn13: '9788437604947',
        idioma: 'Español',
        estado: 'Bueno',
        dueno: 'Lionel Messi',
        calificacionPromedio: 4.5,
        biblioKarmasTotal: 300,
        portada: 'portada.jpg'
      },
      {
        id: 2,
        titulo: '1984',
        genero: 'Ciencia Ficción',
        autor: 'George Orwell',
        tipo: 'COMUN',
        isbn13: '9780451524935',
        idioma: 'Inglés',
        estado: 'Muy bueno',
        dueno: 'Diego Maradona',
        calificacionPromedio: 5,
        biblioKarmasTotal: 200,
        portada: 'portada2.jpg'
      }
    ],
    page: 0,
    size: 3,
    totalItems: 2,
    totalPages: 1
  }
  return {
    reservaService: {
      getLibrosDisponiblesReserva: vi.fn().mockResolvedValue(mockResponse)
    }
  }
})

describe('Home', () => {
  const renderHome = () => render(
    <MemoryRouter>
      <Home />
    </MemoryRouter>
  )

  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.setItem('user', JSON.stringify({ id: 1, role: 'LECTOR' }))
    vi.mocked(reservaService.getLibrosDisponiblesReserva).mockResolvedValue(mockResponse)
  })

  afterEach(() => {
    localStorage.clear()
  })

  it('muestra los libros disponibles al cargar', async () => {
    renderHome()

    expect(await screen.findByTestId('libro-card-1')).toBeInTheDocument()
    expect(screen.getByTestId('libro-card-2')).toBeInTheDocument()
  })

  it('el select de ordenamiento tiene las opciones correctas', async () => {
    renderHome()

    const select = await screen.findByRole('combobox')
    expect(select).toBeInTheDocument()
    expect(select.querySelector('option[value="TITULO_ASC"]')).toBeInTheDocument()
    expect(select.querySelector('option[value="TITULO_DESC"]')).toBeInTheDocument()
  })

  it('al cambiar el ordenamiento solicita el service con el nuevo orden', async () => {
    renderHome()
    await screen.findByTestId('libro-card-1')

    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'TITULO_DESC' } })

    await waitFor(() => {
      expect(reservaService.getLibrosDisponiblesReserva).toHaveBeenCalledTimes(2)
    })

    expect(reservaService.getLibrosDisponiblesReserva).toHaveBeenLastCalledWith(
      expect.objectContaining({
        filter: expect.objectContaining({ orden: 'TITULO_DESC' })
      })
    )
  })

  it('aplica la busqueda y recarga la lista', async () => {
    const user = userEvent.setup()
    renderHome()
    await screen.findByTestId('libro-card-1')

    await user.type(screen.getByTestId('input-busqueda'), 'Rayuela')
    await user.click(screen.getByTestId('btn-buscar'))

    await waitFor(() => {
      expect(reservaService.getLibrosDisponiblesReserva).toHaveBeenCalledTimes(2)
    })

    expect(reservaService.getLibrosDisponiblesReserva).toHaveBeenLastCalledWith(
      expect.objectContaining({
        filter: expect.objectContaining({ busqueda: 'Rayuela' })
      })
    )
  })

  it.each(['LECTOR', 'PUBLICADOR', 'LECTOR_PUBLICADOR'] as const)(
    'renderiza Home correctamente para role %s',
    async (role) => {
      localStorage.setItem('user', JSON.stringify({ id: 1, role }))
      renderHome()

      expect(await screen.findByText('Libros Populares')).toBeInTheDocument()
      expect(reservaService.getLibrosDisponiblesReserva).toHaveBeenCalled()
    }
  )

  it('limpia los filtros y restablece la busqueda', async () => {
    const user = userEvent.setup()
    renderHome()
    await screen.findByTestId('libro-card-1')

    await user.type(screen.getByTestId('input-busqueda'), '1984')
    await user.click(screen.getByTestId('btn-buscar'))

    await waitFor(() => {
      expect(reservaService.getLibrosDisponiblesReserva).toHaveBeenCalledTimes(2)
    })

    await user.click(screen.getByTestId('btn-limpiar'))

    expect(screen.getByTestId('input-busqueda')).toHaveValue('')
    await waitFor(() => {
      expect(reservaService.getLibrosDisponiblesReserva).toHaveBeenCalledTimes(3)
    })
  })
})