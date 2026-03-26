import axios from "axios";
import { API_URL } from "../env";
import type { LibroFormData } from "../classes/Libro";
import type { LibroDetalleResponse } from "../models/LibroResponseModel";
import { authService } from "./AuthService";
import { GENERO_OPTIONS, IDIOMA_OPTIONS, ESTADO_OPTIONS, getKeyFromLabel } from "../utils/formateEnumLibros";

class LibroService {
  // async getById(id: string): Promise<LibroDetalleResponse> {
  //   const response = await axios.get<LibroDetalleResponse>(API_URL + '/libro/' + id)
  //   return response.data
  // }
  async getByIdFor(id: string): Promise<LibroDetalleResponse> {
    const user = authService.userDeStorage();
    const response = await axios.get<LibroDetalleResponse>(`${API_URL}/libro/${id}`, {
      headers: { 'X-User-Id': user.id }
    })
    return response.data
  }

  async create(libroData: LibroFormData): Promise<void> {
    await axios.post(`${API_URL}/libro/nuevo`, this.mapToDTO(libroData))
  }

  async update(id: string, libroData: LibroFormData): Promise<void> {
    await axios.put(`${API_URL}/libro/${id}`, this.mapToDTO(libroData, id))
  }

  private mapToDTO(libroData: LibroFormData, id: string | number | null = null) {
    const user = authService.userDeStorage();

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
      portada: libroData.portada,
      idDueno: user.id
    }
  }
}

export const libroService = new LibroService()