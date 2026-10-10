import type { ReservaRequestModel, ReservaResponseModel } from '../models/ReservaModel'
import type { ReservaLibroHomeJSON, FiltrosLibro } from '../types/ReservaLibroHome'
import type { PagReqModel, PagResModel } from '../models/PaginationModel'
import type { ReseniaReq } from '../models/ReseniaModel'
import type { DetalleReserva } from '../types/DetalleReserva'
import type { CrearReservaJSON } from '../classes/Reserva'
import { api } from '../api/api'

class ReservaService {
  async buscarPrestadosAmi(
    request: PagReqModel<ReservaRequestModel>
  ): Promise<PagResModel<ReservaResponseModel>> {
    const response = await api.post<PagResModel<ReservaResponseModel>>('/reserva/obtenerMisReservas', request)
    return response.data
  }

  async buscarPrestadosPorMi(
    request: PagReqModel<ReservaRequestModel>
  ): Promise<PagResModel<ReservaResponseModel>> {
    const response = await api.post<PagResModel<ReservaResponseModel>>('/reserva/obtenerPrestadosPorMi', request)
    return response.data
  }

  async calificar(request: ReseniaReq): Promise<string> {
    const response = await api.post<string>('/reserva/calificar', request)
    return response.data
  }

  async getLibrosDisponiblesReserva(
    req: PagReqModel<FiltrosLibro>
  ): Promise<PagResModel<ReservaLibroHomeJSON>> {
    const params = { ...req.filter, numeroPagina: req.pageNumber, tamanoPaginado: req.pageSize }
    const response = await api.get<PagResModel<ReservaLibroHomeJSON>>('/reserva/librosDisponibles', { params })
    return response.data
  }

  async getLibrosPopularesHome(): Promise<ReservaLibroHomeJSON[]> {
    const response = await api.get<ReservaLibroHomeJSON[]>('/reserva/librosPopulares')
    return response.data
  }

  async getDetalleLibroByID(
    libroId: string,
    fechaDesde: string,
    fechaHasta: string
  ): Promise<DetalleReserva> {
    const response = await api.get<DetalleReserva>(`/reserva/detalles/${libroId}`, {
      params: { fechaDesde, fechaHasta }
    })
    return response.data
  }

  async crearReserva(reserva: CrearReservaJSON) {
    return await api.post<CrearReservaJSON>('/reserva/crear', reserva)
  }
}

export const reservaService = new ReservaService()
