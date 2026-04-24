import axios from 'axios'
import { API_URL } from '../env'
import { authService, type AuthResponse } from './AuthService'
import type { PagResModel } from '../models/PaginationModel'
import { api } from '../api/api'

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

export type LibroSort = {
  field: string
  direction: string
}

class UsuarioService {
  async getUsuario(): Promise<UsuarioResponse> {
    const token = authService.tokenDeStorage()

    const response = await api.get<UsuarioResponse>(`${API_URL}/usuario/me`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    return response.data
  }

  async getLibros(
    page = 0,
    filter: FiltroLibros = 'Todos',
    sort?: LibroSort
  ): Promise<PagResModel<LibroUsuario>> {
    const token = authService.tokenDeStorage()

    const response = await api.get<PagResModel<LibroUsuario>>(
      `${API_URL}/misLibros/me?page=${page}&size=3&filter=${filter}${sort ? `&sort=${sort.field},${sort.direction}` : ''}`,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    )

    return response.data
  }

  async patchUsuario(userPatch: EditProfileData): Promise<AuthResponse> {
    const token = authService.tokenDeStorage()

    const response = await api.patch(API_URL + `/usuario/me`, userPatch, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    window.localStorage.setItem('accessToken', response.data.accessToken)
    window.localStorage.setItem('refreshToken', response.data.refreshToken)

    return response.data
  }

  async deleteLibro(id: number) {
    const token = authService.tokenDeStorage()

    const response = await api.delete(API_URL + `/misLibros/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    return response.data
  }

  async getBibliokarmas(): Promise<number> {
    const user = authService.userDeStorage()
    const response = await axios.get<number>(API_URL + `/usuario/bibliokarmas/${user.sub}`)

    return response.data
  }
}

export const usuarioService = new UsuarioService()
