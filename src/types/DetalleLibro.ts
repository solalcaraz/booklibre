//TODO: Esto probablemente cambie debido a que me falta la data de la reserva. Una vez definido bien el dominio del backend. 
//Ver como trabajar con la reserva lpm. 
//TODO: Ver como mapear los types para que en la UI se vea bien
export class DetalleLibro {
  id: number
  titulo: string
  tipo: string
  descripcion: string
  genero: string
  autor: string
  cantPaginas: number
  isbn13: string
  idioma: string
  editorial: string
  fechaPublicacion: Date
  urlImagen: string
  calificacionPromedio: number
  estado: string

  constructor(
    id: number,
    titulo: string,
    tipo: string,
    descripcion: string,
    genero: string,
    autor: string,
    cantPaginas: number,
    isbn13: string,
    idioma: string,
    editorial: string,
    fechaPublicacion: Date,
    urlImagen: string,
    calificacionPromedio: number,
    estado: string
  ) {
    // eslint-disable-next-line @typescript-eslint/no-unused-expressions
    ;((this.id = id),
      (this.titulo = titulo),
      (this.tipo = tipo),
      (this.descripcion = descripcion),
      (this.genero = genero),
      (this.autor = autor),
      (this.cantPaginas = cantPaginas),
      (this.isbn13 = isbn13),
      (this.idioma = idioma),
      (this.editorial = editorial),
      (this.fechaPublicacion = fechaPublicacion),
      (this.urlImagen = urlImagen),
      (this.calificacionPromedio = calificacionPromedio),
      (this.estado = estado))
  }

  static fromJSON(detalleLibroJSON: DetalleLibroJSON) {
    return new DetalleLibro(
      detalleLibroJSON.id,
      detalleLibroJSON.titulo,
      detalleLibroJSON.tipo,
      detalleLibroJSON.descripcion,
      detalleLibroJSON.genero,
      detalleLibroJSON.autor,
      detalleLibroJSON.cantPaginas,
      detalleLibroJSON.isbn13,
      detalleLibroJSON.idioma,
      detalleLibroJSON.editorial,
      new Date(detalleLibroJSON.fechaPublicacion), 
      detalleLibroJSON.urlImagen,
      detalleLibroJSON.calificacionPromedio,
      detalleLibroJSON.estado
    )
  }
}

export interface DetalleLibroJSON {
  id: number
  titulo: string
  tipo: TipoDeLibro
  descripcion: string
  genero: Genero
  autor: string
  cantPaginas: number
  isbn13: string
  idioma: Idioma
  editorial: string
  fechaPublicacion: string 
  urlImagen: string
  calificacionPromedio: number
  estado: Estado
}


export type TipoDeLibro = 'COMUN' | 'DEDICATORIA' | 'COLECCIONABLE'

export type Genero =
  | 'DRAMA'
  | 'CIENCIA_FICCION'
  | 'ROMANCE'
  | 'AUTOAYUDA'
  | 'DISENO'
  | 'LITERATURA_CLASICA'

export type Idioma = 'ESPANOL' | 'INGLES' | 'FRANCES' | 'PORTUGUES'

export type Estado = 'EXCELENTE' | 'MUY_BUENO' | 'BUENO' | 'MALO' | 'REGULAR'
