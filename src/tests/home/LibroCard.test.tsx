import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { LibroCard } from '../../components/Home/LibroCard'

const mockNavigate = vi.fn()
vi.mock('react-router', () => ({
  useNavigate: () => mockNavigate,
}))

const libroMock = {
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
}

describe('LibroCard', () => {
  it('muestra el titulo del libro', () => {
    render(<MemoryRouter><LibroCard libro={libroMock} /></MemoryRouter>)
    expect(screen.getByTestId('libro-titulo').textContent).toBe('Rayuela')
  })

  it('muestra la calificacion promedio', () => {
    render(<MemoryRouter><LibroCard libro={libroMock} /></MemoryRouter>)
    expect(screen.getByTestId('libro-calificacion').textContent).toContain('4.5')
  })

  it('renderiza la card con el id correcto', () => {
    render(<MemoryRouter><LibroCard libro={libroMock} /></MemoryRouter>)
    expect(screen.getByTestId('libro-card-1')).toBeTruthy()
  })

  it('navega al detalle cuando se hace click en la card', async () => {
    const user = userEvent.setup()
    render(<MemoryRouter><LibroCard libro={libroMock} /></MemoryRouter>)

    await user.click(screen.getByTestId('libro-card-1'))

    expect(mockNavigate).toHaveBeenCalledWith('/libros/detalle/1')
  })
})
