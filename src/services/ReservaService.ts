import axios from 'axios'
import { API_URL } from '../env'
import type { ReservaRequestModel, ReservaResponseModel } from '../models/ReservaModel'
import type { ReservaLibroHomeJSON, FiltrosLibro } from '../types/ReservaLibroHome'
import { getAxiosData } from './common'
import type { PagReqModel, PagResModel } from '../models/PaginationModel'
import type { ReseniaReq } from '../models/ReseniaModel'
import type { DetalleReserva } from '../types/DetalleReserva'
import { authService } from './AuthService'
import type { CrearReservaJSON } from '../classes/Reserva'

class ReservaService {
  async buscarReservas(
    request: PagReqModel<ReservaRequestModel>
  ): Promise<PagResModel<ReservaResponseModel>> {
    const response = await axios.post<PagResModel<ReservaResponseModel>>(
      API_URL + '/reserva/obtenerReservas',
      request
    )
    return response.data
  }

  async calificar(request: ReseniaReq): Promise<void> {
    const response = await axios.post<void>(API_URL + '/reserva/calificar', request)
    return response.data
  }

  async getLibrosDisponiblesReserva(
    userId: number,
    req: PagReqModel<FiltrosLibro>
  ): Promise<PagResModel<ReservaLibroHomeJSON>> {
    const filtros = req.filter ?? {}
    const params = { ...filtros, numeroPagina: req.pageNumber, tamanoPaginado: req.pageSize }
    const queryLibros = () =>
      axios.get<PagResModel<ReservaLibroHomeJSON>>(
        `${API_URL}/reserva/librosDisponibles/${userId}`,
        {
          params,
          // sobre escritura de los params con paramsSerializer, URL a mano
          paramsSerializer: (params) => {
            const searchParams = new URLSearchParams() // objeto apra constuir queries: clave=valor&clave=valor
            Object.entries(params).forEach(([key, value]) => {
              if (Array.isArray(value)) {
                // si es un array (generos) evita generos[] y lo arma como generos=X generos=X
                value.forEach((v) => searchParams.append(key, v))
                //para el resto de los valores solo los agrega a los filtros si existe como filtro no nulo
              } else if (value !== undefined && value !== null && value !== '') {
                searchParams.append(key, String(value))
              }
            })
            return searchParams.toString()
          }
        }
      )
    return await getAxiosData(queryLibros)
  }
  async getDetalleLibroByID(
    libroId: string,
    fechaDesde: string,
    fechaHasta: string
  ): Promise<DetalleReserva> {
    const user = authService.userDeStorage()
    const url = `${API_URL}/reserva/detalles/${libroId}`

    const response = await axios.get<DetalleReserva>(url, {
      headers: {
        userId: user.id
      },
      params: {
        fechaDesde: fechaDesde,
        fechaHasta: fechaHasta
      }
    })
    return response.data
  }

  async crearReserva(reserva: CrearReservaJSON) {
    const user = authService.userDeStorage()
    const url = `${API_URL}/reserva/crear`

    return await axios.post<CrearReservaJSON>(url, reserva, {
      headers: {
        userId: user.id
      }
    })
  }
}

export const reservaService = new ReservaService()
