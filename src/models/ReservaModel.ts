import type { LibroResumenResponseModel } from './LibroResponseModel';

export interface ReservaResponseModel {
  id: number;
  libroResumen: LibroResumenResponseModel,
  prestadoPor: string;
  bibliokarma: number;
  fechaDesde: string;
  fechaHasta: string;
  estado: string;
  puedeCalificar: boolean;
  reseniaPuntuacion: number;
}

export interface ReservaRequestModel {
  inputSearch: string;
  usuarioId: string;
}
