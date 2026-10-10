import type { LibroFormData } from '../classes/Libro'
import type { LibroDetalleResponse } from '../models/LibroResponseModel'
import {
  GENERO_OPTIONS,
  IDIOMA_OPTIONS,
  ESTADO_OPTIONS,
  getKeyFromLabel
} from '../utils/formateEnumLibros'
import { api } from '../api/api'

class LibroService {
  async getByIdForUser(id: string): Promise<LibroDetalleResponse> {
    const response = await api.get<LibroDetalleResponse>(`/libro/${id}`)
    return response.data
  }

  async create(libroData: LibroFormData): Promise<void> {
    await api.post('/libro/nuevo', this.mapToDTO(libroData))
  }

  async update(id: string, libroData: LibroFormData): Promise<void> {
    await api.put(`/libro/${id}`, this.mapToDTO(libroData, id))
  }

  private mapToDTO(libroData: LibroFormData, id: string | number | null = null) {
    return {
      id: id ? String(id) : null,
      titulo: libroData.titulo,
      tipo: libroData.tipo,
      genero: getKeyFromLabel(GENERO_OPTIONS, libroData.genero),
      autor: libroData.autor,
      idioma: getKeyFromLabel(IDIOMA_OPTIONS, libroData.idioma),
      estado: getKeyFromLabel(ESTADO_OPTIONS, libroData.estado),
      editorial: libroData.editorial,
      fechaPublicacion: libroData.fecha_publicacion,
      paginas: Number(libroData.paginas),
      isbn13: libroData.isbn13,
      descripcion: libroData.descripcion,
      portada: libroData.portada,

      duenoId: null,
      duenoNombre: '',
      duenoImagen: ''
    }
  }
}

export const libroService = new LibroService()
