import { api } from '../api/api'
import type { GraphQLResponse, TasaConversionLibro, EstadoSaludCatalogo, EstadoSaludData, ActividadReciente, FeedActividadData } from '../types/Dashboard'

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

  async getEstadoSaludCatalogo(): Promise<EstadoSaludCatalogo | null> {
    const response = await api.post<GraphQLResponse<EstadoSaludData>>('/graphql', {
      query: `
        query EstadoSaludCatalogo {
          estadoSaludCatalogo {
            total
            prestados
            disponiblesNuncaReservados
            disponiblesReservadosAFuturo
            disponiblesDevueltos
          }
        }
      `
    })

    if (response.data.errors?.length) {
      throw new Error(response.data.errors[0].message)
    }

    return response.data.data?.estadoSaludCatalogo ?? null
  }

  //Punto 5
  async getFeedActividadReciente(): Promise<ActividadReciente[]> {
    const response = await api.post<GraphQLResponse<FeedActividadData>>('/graphql', {
      query: `
        query FeedActividadReciente {
          feedActividadReciente {
            fecha
            tipoEvento
            usuario
            ... on NuevoLibroEvento {
              libroId
              titulo
            }
            ... on NuevaReservaEvento {
              reservaId
            }
          }
        }
      `
    })

    if (response.data.errors?.length) {
      throw new Error(response.data.errors[0].message)
    }

    return response.data.data?.feedActividadReciente ?? []
  }
}

export const dashboardService = new DashboardService()
