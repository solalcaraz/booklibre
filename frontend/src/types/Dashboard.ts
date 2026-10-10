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

export interface PromedioCalificacionPorTipo {
  tipo: 'COMUN' | 'DEDICATORIA' | 'COLECCIONABLE'
  puntajePromedio: number
}

export interface PromedioCalificacionData {
  analisisCalificaciones: PromedioCalificacionPorTipo[]
}

//Punto 5
export interface ActividadReciente {
  fecha: string
  tipoEvento: 'ALTA_LIBRO' | 'NUEVA_RESERVA'
  usuario: string
  libroId?: string
  titulo?: string
  reservaId?: number
}

export interface FeedActividadData {
  feedActividadReciente: ActividadReciente[]
}
