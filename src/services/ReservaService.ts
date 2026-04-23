import axios from 'axios'
import { API_URL } from '../env'
import type { ReservaRequestModel, ReservaResponseModel } from '../models/ReservaModel'
import type { ReservaLibroHomeJSON, FiltrosLibro } from '../types/ReservaLibroHome'
import { customAxios, getAxiosData } from './common'
import type { PagReqModel, PagResModel } from '../models/PaginationModel'
import type { ReseniaReq } from '../models/ReseniaModel'
import type { DetalleReserva } from '../types/DetalleReserva'
import { authService } from './AuthService'
import type { CrearReservaJSON } from '../classes/Reserva'

class ReservaService {
  async buscarPrestadosAmi(request: PagReqModel<ReservaRequestModel>): Promise<PagResModel<ReservaResponseModel>> {
    const response = await axios.post<PagResModel<ReservaResponseModel>>( API_URL + '/reserva/obtenerMisReservas', request )
    return response.data
  }

  async buscarPrestadosPorMi(request: PagReqModel<ReservaRequestModel>): Promise<PagResModel<ReservaResponseModel>> {
    const response = await axios.post<PagResModel<ReservaResponseModel>>( API_URL + '/reserva/obtenerPrestadosPorMi', request )
    return response.data
  }

  async calificar(request: ReseniaReq): Promise<string> {
    const response = await axios.post<string>(API_URL + '/reserva/calificar', request)
    return response.data
  }

  async getLibrosDisponiblesReserva(
    req: PagReqModel<FiltrosLibro>
  ): Promise<PagResModel<ReservaLibroHomeJSON>> {
    const filtros = req.filter ?? {}
    const params = { ...filtros, numeroPagina: req.pageNumber, tamanoPaginado: req.pageSize }
    const queryLibros = () =>
      customAxios.get(
        '/reserva/librosDisponibles',{ params })
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
