import type { LibroResumenResponseModel } from './LibroResponseModel';

export interface ReservaResponseModel {
  libroResumen: LibroResumenResponseModel,
  prestadoPor: string;
  bibliokarma: number;
  fechaDesde: string;
  fechaHasta: string;
}

export interface ReservaRequestModel {
  inputSearch: string;
}
