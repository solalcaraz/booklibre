import axios from 'axios'
import { type LibroFormData, type LibroHomeJSON } from '../classes/Libro'
import { API_URL } from '../env'
import { getAxiosData } from './common'
import type { LibroDetalleResponse } from '../models/LibroResponseModel'

class LibroService {
  async getAll() {
    const queryLibros = () => axios.get<LibroHomeJSON[]>(`${API_URL}/libros`)
    return await getAxiosData(queryLibros)
  }

  // O ESTA (ille)
  async getById(id: string): Promise<LibroDetalleResponse> {
    const response = await axios.get<LibroDetalleResponse>(API_URL + '/libro/' + id)
    return response.data
  }
  async save(libroData: LibroFormData): Promise<void> {
    await axios.post(`${API_URL}/libro/nuevo`, this.mapToDTO(libroData), {
      params: { usuarioId: 1 } // Hardcodeado momentáneamente, tiene que tomar el id del usuario logueado
    })
  }

  async update(id: string, libroData: LibroFormData): Promise<void> {
    await axios.put(`${API_URL}/libro/${id}`, this.mapToDTO(libroData))
  }

  private mapToDTO(libroData: LibroFormData) {
    return {
      titulo: libroData.titulo,
      tipo: libroData.tipo,
      genero: libroData.genero,
      autor: libroData.autor,
      idioma: libroData.idioma,
      estado: libroData.estado,
      editorial: libroData.editorial,
      fechaPublicacion: libroData.fecha_publicacion,
      paginas: Number(libroData.paginas),
      isbn13: libroData.isbn13,
      descripcion: libroData.descripcion,
      portada: libroData.portada
    }
  }
}

export const libroService = new LibroService()
