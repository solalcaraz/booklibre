import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import { reservaService } from '../../services/ReservaService'
import { authService } from '../../services/AuthService'

vi.mock('axios')
const mockedAxios = vi.mocked(axios, true)

// Mockeamos el authService para que devuelva un usuario falso
vi.mock('../../services/AuthService', () => ({
  authService: {
    userDeStorage: vi.fn(),
    tokenDeStorage: vi.fn(() => 'fake-token')
  }
}))

// getLibrosDisponiblesReserva usa la instancia 'api' (axios.create), no axios directo
vi.mock('../../api/api', () => ({
  api: {
    get: vi.fn()
  }
}))

import { api } from '../../api/api'
const mockApi = api as unknown as { get: ReturnType<typeof vi.fn> }

describe('ReservaService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(authService.userDeStorage).mockReturnValue({
      sub: '1',
      role: 'LECTOR_PUBLICADOR',
      iat: 1700000000,
      exp: 9999999999
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
        headers: { Authorization: 'Bearer fake-token' },
        params: { fechaDesde: '2026-01-01', fechaHasta: '2026-01-10' }
      })
    )
    expect(result).toEqual({ calificacionPromedio: 4 })
  })

  it('crearReserva: Lanza error si el back devuelve 409 Conflicto', async () => {
    mockedAxios.post.mockRejectedValueOnce(new Error('Fechas ocupadas'))

    const mockReserva = { libroId: '1', libroMongoID: 'mongo-id-1', fechaDesde: '2026-01-01', fechaHasta: '2026-01-10' }

    await expect(reservaService.crearReserva(mockReserva)).rejects.toThrow('Fechas ocupadas')
  })
})

describe('ReservaService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('buscarPrestadosAmi', () => {
    it('devuelve datos correctamente', async () => {
      const request = {
        pageSize: 10,
        pageNumber: 0,
        filter: {
          inputSearch: '',
          usuarioId: '1'
        }
      }
      const mockResponse = {
        items: [],
        page: 0,
        size: 10,
        totalItems: 0,
        totalPages: 0
      }

      mockedAxios.post.mockResolvedValueOnce({ data: mockResponse })

      const result = await reservaService.buscarPrestadosAmi(request)

      expect(mockedAxios.post).toHaveBeenCalledWith(
        expect.stringContaining('/reserva/obtenerMisReservas'),
        request,
        {
          headers: {
            Authorization: `Bearer fake-token`
          }
        }
      )

      expect(result).toEqual(mockResponse)
    })

    it('lanza error si la API falla', async () => {
      const request = {
        pageSize: 10,
        pageNumber: 0,
        filter: {
          inputSearch: '',
          usuarioId: '1'
        }
      }

      mockedAxios.post.mockRejectedValueOnce(new Error('Error API'))

      await expect(
        reservaService.buscarPrestadosAmi(request)
      ).rejects.toThrow('Error API')
    })
  })

  describe('buscarPrestadosPorMi', () => {
    it('llama a la URL correcta y devuelve datos', async () => {
      const request = {
        pageSize: 10,
        pageNumber: 0,
        filter: {
          inputSearch: '',
          usuarioId: '1'
        }
      }
      const mockResponse = {
        items: [],
        page: 0,
        size: 10,
        totalItems: 0,
        totalPages: 0
      }

      mockedAxios.post.mockResolvedValueOnce({ data: mockResponse })

      const result = await reservaService.buscarPrestadosPorMi(request)

      expect(mockedAxios.post).toHaveBeenCalledWith(
        expect.stringContaining('/reserva/obtenerPrestadosPorMi'),
        request,
        {
          headers: {
            Authorization: `Bearer fake-token`
          }
        }
      )

      expect(result).toEqual(mockResponse)
    })

    // omito error porque es igual que el anterior
  })

  describe('calificar', () => {
    it('devuelve string correctamente', async () => {
      const request = {
        reservaId: 1,
        puntuacion: 5,
        comentario: 'Excelente'
      }

      mockedAxios.post.mockResolvedValueOnce({ data: 'OK' })

      const result = await reservaService.calificar(request)

      expect(mockedAxios.post).toHaveBeenCalledWith(
        expect.stringContaining('/reserva/calificar'),
        request,
        {
          headers: {
            Authorization: `Bearer fake-token`
          }
        }
      )

      expect(result).toBe('OK')
    })

    it('lanza error si falla la API', async () => {
      const request = {
        reservaId: 1,
        puntuacion: 5,
        comentario: 'Excelente'
      }

      mockedAxios.post.mockRejectedValueOnce(new Error('Error al calificar'))

      await expect(
        reservaService.calificar(request)
      ).rejects.toThrow('Error al calificar')
    })
  })

  describe('getLibrosDisponiblesReserva', () => {
    it('devuelve datos correctamente', async () => {
      const request = {
        pageSize: 10,
        pageNumber: 0,
        filter: {
          busqueda: 'Rayuela',
          generos: [],
          cantPaginasMin: 0,
          cantPaginasMax: 1000,
          fechaDesde: '2026-01-01',
          fechaHasta: '2026-01-10',
          isbn: '',
          usuarioPrestador: '',
          orden: 'TITULO_ASC' as any // TODO: reemplazar por el enum Orden real, ej. Orden.TITULO_ASC, una vez confirmado en models/PaginationModel.ts
        }
      }
      const mockResponse = {
        items: [],
        page: 0,
        size: 10,
        totalItems: 0,
        totalPages: 0
      }

      mockApi.get.mockResolvedValueOnce({ data: mockResponse })

      const result = await reservaService.getLibrosDisponiblesReserva(request)

      expect(mockApi.get).toHaveBeenCalledWith(
        '/reserva/librosDisponibles',
        expect.objectContaining({
          params: expect.objectContaining({
            busqueda: 'Rayuela',
            numeroPagina: 0,
            tamanoPaginado: 10
          }),
          headers: { Authorization: 'Bearer fake-token' }
        })
      )

      expect(result).toEqual(mockResponse)
    })

    it('lanza error si la API falla', async () => {
      const request = {
        pageSize: 10,
        pageNumber: 0,
        filter: {}
      }

      mockApi.get.mockRejectedValueOnce(new Error('Error API'))

      await expect(
        reservaService.getLibrosDisponiblesReserva(request)
      ).rejects.toThrow('Error API')
    })
  })
})