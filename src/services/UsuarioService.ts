import axios from 'axios'
import { API_URL } from '../env'
import type { PaginationResponseModel } from '../models/PaginationResponseModel'
import { authService } from './AuthService'

export type FiltroLibros = 'Todos' | 'Disponibles' | 'Prestados'

export type UsuarioResponse = {
  nombreCompleto: string
  email: string
  descripcion: string
  telefono: string
  anoUnido: number
  ciudad: string
  bibliokarmas: number
  tipoUsuario: string
  imagen: string
  librosLeidos: number
  librosPrestados: number
}

type LibroEstado = 'DISPONIBLE' | 'PRESTADO'

export type LibroUsuario = {
  id: number
  titulo: string
  autor: string
  genero: string
  estadoReserva: LibroEstado
  agregadoEl: string
  portada: string
}

export type EditProfileData = {
  nombreCompleto: string
  descripcion: string
  email: string
  telefono: string
  ciudad: string
  imagen: string
  tipoUsuario: string
}

class UsuarioService {
  async getUsuario(): Promise<UsuarioResponse> {
    const user = authService.userDeStorage()
    const response = await axios.get<UsuarioResponse>(API_URL + `/usuario/${user.id}`)

    return response.data
  }

  async getLibros(
    page = 0,
    filter: FiltroLibros = 'Todos'
  ): Promise<PaginationResponseModel<LibroUsuario>> {
    const user = authService.userDeStorage()
    const response = await axios.get<PaginationResponseModel<LibroUsuario>>(
      API_URL + `/misLibros/${user.id}?page=${page}&size=3&filter=${filter}`
    )

    return response.data
  }

  async patchUsuario(userPatch: EditProfileData) {
    const user = authService.userDeStorage()
    const response = await axios.patch(API_URL + `/usuario/${user.id}`, userPatch)

    return response.data
  }

  async deleteLibro(id: number) {
    const response = await axios.delete(API_URL + `/misLibros/${id}`)

    return response.data
  }
}

export const usuarioService = new UsuarioService()
