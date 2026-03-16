import axios from 'axios';
import { API_URL } from '../env';
import type { PaginationResponseModel } from '../models/PaginationResponseModel';
import type { ReservaRequestModel, ReservaResponseModel } from '../models/ReservaModel';
import type { PaginationRequestModel } from '../models/PaginationRequestModel';
import type { DetalleReserva } from '../types/DetalleReserva';
import { authService } from './AuthService';

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
  //Momentaneo de la vista de sol para probar el mio. 
  
    async getDetalleLibroByID(libroId: string, fechaDesde: string, fechaHasta: string): Promise<DetalleReserva> {
      const user = authService.userDeStorage()
      const url = `${API_URL}/detalles/${libroId}`

      const response = await axios.get<DetalleReserva>(url, {
        headers:{
          "userId": user.id
        },
        params: {
          fechaDesde: fechaDesde,
          fechaHasta: fechaHasta
        }
      })
      return response.data
    }
}

export const reservaService = new ReservaService()
