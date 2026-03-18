import axios from 'axios';
import { API_URL } from '../env';
import type { ReservaRequestModel, ReservaResponseModel } from '../models/ReservaModel';
import type { ReservaLibroHomeJSON, FiltrosLibro } from '../types/ReservaLibroHome';
import { getAxiosData } from './common';
import type { PagReqModel, PagResModel } from '../models/PaginationModel';
import type { ReseniaReq } from '../models/ReseniaModel';

class ReservaService {
  async buscarReservas(request: PagReqModel<ReservaRequestModel>): Promise<PagResModel<ReservaResponseModel>> {
    const response = await axios.post<PagResModel<ReservaResponseModel>>(API_URL + '/reserva/obtenerReservas', request)
    return response.data
  }

  async calificar(request: ReseniaReq): Promise<void> {
    const response = await axios.post<void>(API_URL + '/reserva/calificar', request)
    return response.data
  }

  async getLibrosDisponiblesReserva(
    userId: number,
    req: PaginationRequestModel<FiltrosLibro>
  ): Promise<PaginationResponseModel<ReservaLibroHomeJSON>> {
    const filtros = req.filter ?? {}
    const params = {
      ...filtros,
      numeroPagina: req.pageNumber,
      tamanoPaginado: req.pageSize
    }
    const queryLibros = () => axios.get<PaginationResponseModel<ReservaLibroHomeJSON>>(`${API_URL}/librosDisponibles/${userId}`, { params } )
    return await getAxiosData(queryLibros)
  }
}

export const reservaService = new ReservaService()
