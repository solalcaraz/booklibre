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
