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
  clicks: number
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

export const initialSort: LibroSort = {
  field: 'titulo',
  direction: 'asc'
}

class UsuarioService {
  async getUsuario(): Promise<UsuarioResponse> {
    const response = await api.get<UsuarioResponse>('/usuario/me')
    return response.data
  }

  async getLibros(
    page = 0,
    filter: FiltroLibros = 'Todos',
    sort: LibroSort = initialSort
  ): Promise<PagResModel<LibroUsuario>> {
    const response = await api.get<PagResModel<LibroUsuario>>(
      `/misLibros/me?page=${page}&size=3&filter=${filter}&sort=${sort.field},${sort.direction}`
    )
    return response.data
  }

  async patchUsuario(userPatch: EditProfileData): Promise<AuthResponse> {
    const response = await api.patch<AuthResponse>('/usuario/me', userPatch)
    // El back emite tokens nuevos porque el rol del usuario pudo cambiar
    window.localStorage.setItem('accessToken', response.data.accessToken)
    return response.data
  }

  async deleteLibro(id: number) {
    const response = await api.delete(`/misLibros/${id}`)
    return response.data
  }

  async getBibliokarmas(): Promise<number> {
    const user = authService.userDeStorage()

    if (!user) {
      throw new Error('No hay usuario autenticado')
    }

    const response = await api.get<number>(`/usuario/bibliokarmas/${user.sub}`)
    return response.data
  }
}

export const usuarioService = new UsuarioService()
