import { api } from '../api/api'
import type { GraphQLResponse, TasaConversionLibro, EstadoSaludCatalogo, EstadoSaludData, ActividadReciente, FeedActividadData, PromedioCalificacionPorTipo, PromedioCalificacionData } from '../types/Dashboard'

interface TasaConversionData {
  tasaConversionClicksReservas: TasaConversionLibro[]
}

export interface UsuarioLeaderboard {
  id: number
  nombreCompleto: string
  bibliokarmas: number
}

interface LeaderboardData {
  leaderboardUsuarios: UsuarioLeaderboard[]
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

  async getAnalisisCalificaciones(): Promise<PromedioCalificacionPorTipo[]> {
    const response = await api.post<GraphQLResponse<PromedioCalificacionData>>('/graphql', {
      query: `
        query PromedioCalificacionData {
          analisisCalificaciones {
            tipo,
            puntajePromedio
          }
        }
      `
    })

    if (response.data.errors?.length) {
      throw new Error(response.data.errors[0].message)
    }

    return response.data.data?.analisisCalificaciones ?? []
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

  async getLeaderboardUsuarios(): Promise<UsuarioLeaderboard[]> {
    const response = await api.post<GraphQLResponse<LeaderboardData>>('/graphql', {
      query: `
        query LeaderboardUsuarios {
          leaderboardUsuarios {
            id
            nombreCompleto
            bibliokarmas
          }
        }
      `
    })

    if (response.data.errors?.length) {
      throw new Error(response.data.errors[0].message)
    }

    return response.data.data?.leaderboardUsuarios ?? []
  }
}

export const dashboardService = new DashboardService()
