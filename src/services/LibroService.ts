import axios from 'axios'
import { DetalleLibro } from '../types/DetalleLibro'

const REST_SERVER_URL = 'http://localhost:9000'

class LibroService {
  async getDetalleLibroByID(id: string): Promise<DetalleLibro> {
    const url = `${REST_SERVER_URL}/libros/detalle/${id}`
    const response = await axios.get(url)
    return DetalleLibro.fromJSON(response.data)
  }
}

export const libroService = new LibroService()
