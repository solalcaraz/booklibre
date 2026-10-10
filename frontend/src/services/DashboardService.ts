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
  private async consultar<T>(query: string): Promise<T | undefined> {
    const response = await api.post<GraphQLResponse<T>>('/graphql', { query })

    // GraphQL responde 200 aunque falle un resolver: el error viene en el body
    if (response.data.errors?.length) {
      throw new Error(response.data.errors[0].message)
    }

    return response.data.data
  }

  async getTasaConversionClicksReservas(): Promise<TasaConversionLibro[]> {
    const data = await this.consultar<TasaConversionData>(`
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
      `)
    return data?.tasaConversionClicksReservas ?? []
  }

  async getEstadoSaludCatalogo(): Promise<EstadoSaludCatalogo | null> {
    const data = await this.consultar<EstadoSaludData>(`
        query EstadoSaludCatalogo {
          estadoSaludCatalogo {
            total
            prestados
            disponiblesNuncaReservados
            disponiblesReservadosAFuturo
            disponiblesDevueltos
          }
        }
      `)
    return data?.estadoSaludCatalogo ?? null
  }

  async getAnalisisCalificaciones(): Promise<PromedioCalificacionPorTipo[]> {
    const data = await this.consultar<PromedioCalificacionData>(`
        query PromedioCalificacionData {
          analisisCalificaciones {
            tipo,
            puntajePromedio
          }
        }
      `)
    return data?.analisisCalificaciones ?? []
  }

  async getFeedActividadReciente(): Promise<ActividadReciente[]> {
    const data = await this.consultar<FeedActividadData>(`
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
      `)
    return data?.feedActividadReciente ?? []
  }

  async getLeaderboardUsuarios(): Promise<UsuarioLeaderboard[]> {
    const data = await this.consultar<LeaderboardData>(`
        query LeaderboardUsuarios {
          leaderboardUsuarios {
            id
            nombreCompleto
            bibliokarmas
          }
        }
      `)
    return data?.leaderboardUsuarios ?? []
  }
}

export const dashboardService = new DashboardService()
