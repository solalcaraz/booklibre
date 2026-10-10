import { describe, it, expect, vi, beforeEach } from 'vitest'
import { reservaService } from '../../services/ReservaService'
import { api } from '../../api/api'

// El token lo agrega el interceptor de api, así que alcanza con mockear la instancia
vi.mock('../../api/api', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn()
  }
}))

const mockApi = vi.mocked(api)

describe('ReservaService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('getDetalle: Lanza error si la API falla (ej. 404/500)', async () => {
    mockApi.get.mockRejectedValueOnce(new Error('Libro no encontrado'))

    await expect(
      reservaService.getDetalleLibroByID('99', '2026-01-01', '2026-01-10')
    ).rejects.toThrow('Libro no encontrado')
  })

  it('getDetalle: Llama a la URL correcta con los params', async () => {
    mockApi.get.mockResolvedValueOnce({ data: { calificacionPromedio: 4 } })

    const result = await reservaService.getDetalleLibroByID('1', '2026-01-01', '2026-01-10')

    expect(mockApi.get).toHaveBeenCalledWith(
      '/reserva/detalles/1',
      { params: { fechaDesde: '2026-01-01', fechaHasta: '2026-01-10' } }
    )
    expect(result).toEqual({ calificacionPromedio: 4 })
  })

  it('crearReserva: Lanza error si el back devuelve 409 Conflicto', async () => {
    mockApi.post.mockRejectedValueOnce(new Error('Fechas ocupadas'))

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

      mockApi.post.mockResolvedValueOnce({ data: mockResponse })

      const result = await reservaService.buscarPrestadosAmi(request)

      expect(mockApi.post).toHaveBeenCalledWith(
        '/reserva/obtenerMisReservas',
        request
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

      mockApi.post.mockRejectedValueOnce(new Error('Error API'))

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

      mockApi.post.mockResolvedValueOnce({ data: mockResponse })

      const result = await reservaService.buscarPrestadosPorMi(request)

      expect(mockApi.post).toHaveBeenCalledWith(
        '/reserva/obtenerPrestadosPorMi',
        request
      )

      expect(result).toEqual(mockResponse)
    })

  })

  describe('calificar', () => {
    it('devuelve string correctamente', async () => {
      const request = {
        reservaId: 1,
        puntuacion: 5,
        comentario: 'Excelente'
      }

      mockApi.post.mockResolvedValueOnce({ data: 'OK' })

      const result = await reservaService.calificar(request)

      expect(mockApi.post).toHaveBeenCalledWith(
        '/reserva/calificar',
        request
      )

      expect(result).toBe('OK')
    })

    it('lanza error si falla la API', async () => {
      const request = {
        reservaId: 1,
        puntuacion: 5,
        comentario: 'Excelente'
      }

      mockApi.post.mockRejectedValueOnce(new Error('Error al calificar'))

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
          orden: 'TITULO_ASC' as const
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
          })
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