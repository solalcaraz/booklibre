import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { describe, expect, test, vi, beforeEach, afterEach } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import axios from 'axios'
import { Home } from '../../pages/Home'

// mock de axios con AxiosError incluido
vi.mock('axios', async (importOriginal) => {
  const actual = await importOriginal<typeof import('axios')>()
  return {
    default: {
      ...actual.default,
      get: vi.fn(),
    },
    AxiosError: actual.AxiosError,
  }
})

// mock de useNavigate de react-router (lo usa LibroCard)
vi.mock('react-router', async () => {
  const actual = await vi.importActual('react-router')
  return {
    ...actual,
    useNavigate: vi.fn(() => vi.fn()),
  }
})

// mock del service directamente
vi.mock('../../services/ReservaService', () => ({
  reservaService: {
    getLibrosDisponiblesReserva: vi.fn().mockResolvedValue({
      items: [
        {
          id: 1, titulo: 'Rayuela', genero: 'Literatura Clásica',
          autor: 'Julio Cortázar', tipo: 'DEDICATORIA',
          isbn13: '9788437604947', idioma: 'Español', estado: 'Bueno',
          dueno: 'Lionel Messi', calificacionPromedio: 4.5,
          biblioKarmasTotal: 300, portada: 'portada.jpg'
        },
        {
          id: 2, titulo: '1984', genero: 'Ciencia Ficción',
          autor: 'George Orwell', tipo: 'COMUN',
          isbn13: '9780451524935', idioma: 'Inglés', estado: 'Muy bueno',
          dueno: 'Diego Maradona', calificacionPromedio: 5,
          biblioKarmasTotal: 200, portada: 'portada2.jpg'
        }
      ],
      page: 0, size: 3, totalItems: 2, totalPages: 1
    })
  }
}))

describe('Home', () => {
  beforeEach(() => {
    localStorage.setItem('user', JSON.stringify({ id: 1 }))
  })

  afterEach(() => {
    vi.restoreAllMocks()
    localStorage.clear()
  })

  test('muestra los libros disponibles al cargar', async () => {
    render(<MemoryRouter><Home /></MemoryRouter>)
    await waitFor(() => {
      expect(screen.getByTestId('libro-card-1')).toBeTruthy()
      expect(screen.getByTestId('libro-card-2')).toBeTruthy()
    })
  })

  test('el select de ordenamiento tiene las opciones correctas', async () => {
    render(<MemoryRouter><Home /></MemoryRouter>)
    const select = screen.getByRole('combobox')
    expect(select).toBeTruthy()
    expect(select.querySelector('option[value="TITULO_ASC"]')).toBeTruthy()
    expect(select.querySelector('option[value="TITULO_DESC"]')).toBeTruthy()
  })

  test('al cambiar el ordenamiento llama al service de nuevo', async () => {
    const { reservaService } = await import('../../services/ReservaService')
    render(<MemoryRouter><Home /></MemoryRouter>)
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'TITULO_DESC' } })
    await waitFor(() => {
      expect(reservaService.getLibrosDisponiblesReserva).toHaveBeenCalled()
    })
  })
})