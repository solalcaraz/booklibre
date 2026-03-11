import axios from 'axios';
import { API_URL } from '../env';
import type { PaginationResponseModel } from '../models/PaginationResponseModel';
import type { ReservaRequestModel, ReservaResponseModel } from '../models/ReservaModel';
import type { PaginationRequestModel } from '../models/PaginationRequestModel';

class ReservaService {
  //Todo: send params and filter
  async misReservas(request: PaginationRequestModel<ReservaRequestModel>): Promise<PaginationResponseModel<ReservaResponseModel>> {
    const response = await axios.post<PaginationResponseModel<ReservaResponseModel>>(API_URL + '/reserva/misReservas', request)
    return response.data
  }

  async prestados(request: PaginationRequestModel<ReservaRequestModel>): Promise<PaginationResponseModel<ReservaResponseModel>> {
    const response = await axios.post<PaginationResponseModel<ReservaResponseModel>>(API_URL + '/reserva/prestados', request)
    return response.data
  }
}

export const reservaService = new ReservaService()
