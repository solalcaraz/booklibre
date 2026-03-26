import { render, screen } from '@testing-library/react'
import { describe, expect, test, vi } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import { LibroCard } from '../../components/Home/LibroCard'

vi.mock('react-router', () => ({
  useNavigate: vi.fn(() => vi.fn()),
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
  test('muestra el titulo del libro', () => {
    render(<MemoryRouter><LibroCard libro={libroMock} /></MemoryRouter>)
    expect(screen.getByTestId('libro-titulo').textContent).toBe('Rayuela')
  })

  test('muestra la calificacion promedio', () => {
    render(<MemoryRouter><LibroCard libro={libroMock} /></MemoryRouter>)
    expect(screen.getByTestId('libro-calificacion').textContent).toContain('4.5')
  })

  test('renderiza la card con el id correcto', () => {
    render(<MemoryRouter><LibroCard libro={libroMock} /></MemoryRouter>)
    expect(screen.getByTestId('libro-card-1')).toBeTruthy()
  })
})