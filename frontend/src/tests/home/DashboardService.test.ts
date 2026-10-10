import { beforeEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/api'
import { dashboardService } from '../../services/DashboardService'

vi.mock('../../api/api', () => ({
  api: {
    post: vi.fn()
  }
}))

describe('DashboardService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('consulta GraphQL y devuelve tasaConversionClicksReservas', async () => {
    vi.mocked(api.post).mockResolvedValue({
      data: {
        data: {
          tasaConversionClicksReservas: [
            {
              libroId: '1',
              titulo: 'Rayuela',
              autor: 'Julio Cortazar',
              clicks: 20,
              reservas: 5,
              tasaConversion: 0.25
            }
          ]
        }
      }
    })

    const resultado = await dashboardService.getTasaConversionClicksReservas()

    expect(api.post).toHaveBeenCalledWith('/graphql', {
      query: expect.stringContaining('tasaConversionClicksReservas')
    })
    expect(resultado).toEqual([
      {
        libroId: '1',
        titulo: 'Rayuela',
        autor: 'Julio Cortazar',
        clicks: 20,
        reservas: 5,
        tasaConversion: 0.25
      }
    ])
  })

  it('lanza error si GraphQL devuelve errors', async () => {
    vi.mocked(api.post).mockResolvedValue({
      data: {
        errors: [{ message: 'Error de GraphQL' }]
      }
    })

    await expect(dashboardService.getTasaConversionClicksReservas()).rejects.toThrow('Error de GraphQL')
  })
})
