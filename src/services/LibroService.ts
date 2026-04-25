import { API_URL } from "../env";
import type { LibroFormData } from "../classes/Libro";
import type { LibroDetalleResponse } from "../models/LibroResponseModel";
import { GENERO_OPTIONS, IDIOMA_OPTIONS, ESTADO_OPTIONS, getKeyFromLabel } from "../utils/formateEnumLibros";
import { api } from "../api/api";
import { authService } from "./AuthService";

class LibroService {
  async getByIdForUser(id: string): Promise<LibroDetalleResponse> {
    const token = authService.tokenDeStorage()
    const response = await api.get<LibroDetalleResponse>(`${API_URL}/libro/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    return response.data
  }

  async create(libroData: LibroFormData): Promise<void> {
    const token = authService.tokenDeStorage()
    await api.post(`${API_URL}/libro/nuevo`, this.mapToDTO(libroData), {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  async update(id: string, libroData: LibroFormData): Promise<void> {
    const token = authService.tokenDeStorage()
    await api.put(`${API_URL}/libro/${id}`, this.mapToDTO(libroData, id), {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private mapToDTO(libroData: LibroFormData, id: string | number | null = null) {
    return {
      id: id ? Number(id) : null,
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
      portada: libroData.portada
    }
  }
}

export const libroService = new LibroService()