import axios from 'axios';
import { API_URL } from '../env';
import type { PaginationResponseModel } from '../models/PaginationResponseModel';
import type { ReservaRequestModel, ReservaResponseModel } from '../models/ReservaModel';
import type { PaginationRequestModel } from '../models/PaginationRequestModel';
import type { ReservaLibroHomeJSON, FiltrosLibro } from '../types/ReservaLibroHome';
import { getAxiosData } from './common';

class ReservaService {
  //Todo: send filter
  async misReservas(request: PaginationRequestModel<ReservaRequestModel>): Promise<PaginationResponseModel<ReservaResponseModel>> {
    const response = await axios.post<PaginationResponseModel<ReservaResponseModel>>(API_URL + '/reserva/misReservas', request)
    return response.data
  }

  async prestados(request: PaginationRequestModel<ReservaRequestModel>): Promise<PaginationResponseModel<ReservaResponseModel>> {
    const response = await axios.post<PaginationResponseModel<ReservaResponseModel>>(API_URL + '/reserva/prestados', request)
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
