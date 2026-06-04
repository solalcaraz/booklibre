export interface TasaConversionLibro {
  libroId: string
  titulo: string
  autor: string
  clicks: number
  reservas: number
  tasaConversion: number
}

export interface GraphQLResponse<T> {
  data?: T
  errors?: { message: string }[]
}

export interface EstadoSaludCatalogo {
  total: number
  prestados: number
  disponiblesNuncaReservados: number
  disponiblesReservadosAFuturo: number
  disponiblesDevueltos: number
}

export interface EstadoSaludData {
  estadoSaludCatalogo: EstadoSaludCatalogo
}