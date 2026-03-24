import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import { reservaService } from '../../services/ReservaService'
import { authService } from '../../services/AuthService'

vi.mock('axios')
const mockedAxios = vi.mocked(axios, true)

// Mockeamos el authService para que devuelva un usuario falso
vi.mock('../../services/AuthService', () => ({
  authService: {
    userDeStorage: vi.fn()
  }
}))

describe('ReservaService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(authService.userDeStorage).mockReturnValue({
      id: 1,
      nombreCompleto: 'Test User',
      imagen: 'img.jpg',
      bibliokarmas: 100
    })
  })

  it('getDetalle: Lanza error si la API falla (ej. 404/500)', async () => {
    mockedAxios.get.mockRejectedValueOnce(new Error('Libro no encontrado'))

    await expect(
      reservaService.getDetalleLibroByID('99', '2026-01-01', '2026-01-10')
    ).rejects.toThrow('Libro no encontrado')
  })

  it('getDetalle: Llama a la URL correcta con los headers y params', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: { calificacionPromedio: 4 } })

    const result = await reservaService.getDetalleLibroByID('1', '2026-01-01', '2026-01-10')

    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining('/reserva/detalles/1'),
      expect.objectContaining({
        headers: { userId: 1 },
        params: { fechaDesde: '2026-01-01', fechaHasta: '2026-01-10' }
      })
    )
    expect(result).toEqual({ calificacionPromedio: 4 })
  })

  it('crearReserva: Lanza error si el back devuelve 409 Conflicto', async () => {
    mockedAxios.post.mockRejectedValueOnce(new Error('Fechas ocupadas'))

    const mockReserva = { libroId: '1', fechaDesde: '2026-01-01', fechaHasta: '2026-01-10' }

    await expect(reservaService.crearReserva(mockReserva)).rejects.toThrow('Fechas ocupadas')
  })
})