import axios from "axios";
import { API_URL } from "../env";
import type { DetalleLibro } from '../types/DetalleLibro'
import type { LibroFormData } from "../classes/Libro";
import type { LibroDetalleResponse } from "../models/LibroResponseModel";
import { authService } from "./AuthService";

class LibroService {
    
  // REVISAR CUÁL TOMAMOS, ESTA (luis)
  async getDetalleLibroByID(id: string): Promise<DetalleLibro> {
    const url = `${API_URL}/libros/detalle/${id}`
    const response = await axios.get(url)
    return response.data
  }

  // O ESTA (ille)
  async getById(id: string): Promise<LibroDetalleResponse> {
    const response = await axios.get<LibroDetalleResponse>(API_URL + '/libro/' + id)
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
      genero: libroData.genero,
      autor: libroData.autor,
      idioma: libroData.idioma,
      estado: libroData.estado,
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
