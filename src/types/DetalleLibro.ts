export interface DetalleLibro {
  id:                   number;
  titulo:               string;
  tipo:                 string;
  descripcion:          string;
  genero:               string;
  autor:                string;
  cantPaginas:          number;
  isbn13:               string;
  idioma:               string;
  editorial:            string;
  fechaPublicacion:     string;
  urlImagen:            string;
  calificacionPromedio: number;
  estado:               string;
  resenias:             Resenia[];
}

export interface Resenia {
  nombreUsuario: string;
  puntuacion:    number;
  comentario:    string;
}

