import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, waitFor, cleanup, fireEvent } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { TuReserva } from '../../components/DetalleReserva/TuReserva'
import { reservaService } from '../../services/ReservaService'

const mockNavigate = vi.fn()

// Mockeo react-router-dom
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate
  }
})

// Mockeo ReservaService
vi.mock('../../services/ReservaService', () => ({
  reservaService: {
    crearReserva: vi.fn()
  }
}))

describe('Componente TuReserva', () => {
  const mockSetFechaDesde = vi.fn()
  const mockSetFechaHasta = vi.fn()

  const renderComponent = (fechaDesde: string, fechaHasta: string) => {
    return render(
      <MemoryRouter>
        <TuReserva
          libroId={1}
          fechaDesde={fechaDesde}
          fechaHasta={fechaHasta}
          fechasOcupadas={[{ fechaDesde: '2026-05-10', fechaHasta: '2026-05-15' }]}
          setFechaDesde={mockSetFechaDesde}
          setFechaHasta={mockSetFechaHasta}
        />
      </MemoryRouter>
    )
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    cleanup()
  })

  it('Botón deshabilitado y mensaje de error si las fechas están invertidas', () => {
    // Renderizamos con la fecha de Devolución ANTES que la de Recogida
    renderComponent('2026-12-20', '2026-12-10')

    const btn = screen.getByRole('button', { name: /confirmar reserva/i })
    expect(btn).toBeDisabled()
    expect(screen.getByText(/la fecha de recogida debe ser hoy o posterior/i)).toBeInTheDocument()
  })

  it('Botón deshabilitado si el rango pisa una fecha ocupada', () => {
    // Fechas que chocan con el mock (10 al 15 de mayo)
    renderComponent('2026-05-12', '2026-05-18')

    const btn = screen.getByRole('button', { name: /confirmar reserva/i })
    expect(btn).toBeDisabled()
    expect(screen.getByText(/el libro no está disponible en el rango/i)).toBeInTheDocument()
  })

  it('Muestra el ErrorCard si el backend falla al crear la reserva', async () => {
    const user = userEvent.setup()

    // Forzamos que la API falle con un error
    vi.mocked(reservaService.crearReserva).mockRejectedValueOnce(new Error('Backend explotó'))

    // Fechas válidas
    renderComponent('2026-06-01', '2026-06-10')

    const btn = screen.getByRole('button', { name: /confirmar reserva/i })
    expect(btn).not.toBeDisabled()

    await user.click(btn)

    // Esperamos a que la vista cambie y aparezca el ErrorCard
    await waitFor(() => {
      // Tu errorHandler setea un mensaje default si no es AxiosError
      expect(screen.getByTestId('error-card')).toBeInTheDocument()
    })
  })

  it('Llama a crearReserva, muestra éxito y redirige', async () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    vi.mocked(reservaService.crearReserva).mockResolvedValueOnce({} as any)

    vi.useFakeTimers({ shouldAdvanceTime: true })

    renderComponent('2026-06-01', '2026-06-10')

    const btn = screen.getByRole('button', { name: /confirmar reserva/i })
    fireEvent.click(btn)

    await waitFor(() => {
      expect(reservaService.crearReserva).toHaveBeenCalledTimes(1)
      expect(screen.getByText(/reserva exitosa/i)).toBeInTheDocument()
    })

    vi.advanceTimersByTime(2000)

    expect(mockNavigate).toHaveBeenCalledWith('/prestamos')

    vi.useRealTimers()
  })
})
