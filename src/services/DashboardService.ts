import { api } from '../api/api'
import type { GraphQLResponse, TasaConversionLibro } from '../types/Dashboard'

interface TasaConversionData {
  tasaConversionClicksReservas: TasaConversionLibro[]
}

class DashboardService {
  async getTasaConversionClicksReservas(): Promise<TasaConversionLibro[]> {
    const response = await api.post<GraphQLResponse<TasaConversionData>>('/graphql', {
      query: `
        query TasaConversionClicksReservas {
          tasaConversionClicksReservas {
            libroId
            titulo
            autor
            clicks
            reservas
            tasaConversion
          }
        }
      `
    })

    if (response.data.errors?.length) {
      throw new Error(response.data.errors[0].message)
    }

    return response.data.data?.tasaConversionClicksReservas ?? []
  }
}

export const dashboardService = new DashboardService()
