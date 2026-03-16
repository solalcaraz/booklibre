export interface DetalleReserva {
  libroDetalleDTO:      LibroDetalleDTO;
  bibliokarmasTotales:  number;
  calificacionPromedio: number;
  ultimasResenias:      UltimasResenia[];
  rangosFechasOcupadas: RangosFechasOcupada[];
}

export interface LibroDetalleDTO {
  id:               number;
  titulo:           string;
  tipo:             string;
  genero:           string;
  autor:            string;
  idioma:           string;
  estado:           string;
  editorial:        string;
  fechaPublicacion: string;
  paginas:          number;
  isbn13:           string;
  descripcion:      string;
  portada:          string;
}

export interface RangosFechasOcupada {
  fechaDesde: string;
  fechaHasta: string;
}

export interface UltimasResenia {
  id:            number;
  nombreUsuario: string;
  puntuacion:    number;
  comentario:    string;
}
