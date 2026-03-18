import axios from "axios";
import type { LibroDetalleResponse } from "../responses/LibroResponse";
import { API_URL } from "../env";
import type { DetalleLibro } from '../types/DetalleLibro'
import type { LibroFormData } from "../classes/Libro";

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
