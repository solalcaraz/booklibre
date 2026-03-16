import axios from 'axios';
import { API_URL } from '../env';
import type { ReservaRequestModel, ReservaResponseModel } from '../models/ReservaModel';
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
}

export const reservaService = new ReservaService()
