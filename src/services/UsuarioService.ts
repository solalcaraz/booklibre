import axios from 'axios'
import type { LibroUsuario } from '../components/PerfilUsuario/PerfilLibro'
import { API_URL } from '../env'

export type UsuarioResponse = {
  nombreCompleto: string
  email: string
  descripcion: string
  telefono: string
  anoUnido: number
  ciudad: string
  bibliokarmas: number
  tipoUsuario: string
}

class UsuarioService {
  async getUsuario(): Promise<UsuarioResponse> {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const response = await axios.get<UsuarioResponse>(API_URL + `/usuario/${user.id}`)

    return response.data
  }

  async getLibros(): Promise<LibroUsuario[]> {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const response = await axios.get<LibroUsuario[]>(API_URL + `/misLibros/${user.id}`)

    return response.data
  }
}

export const usuarioService = new UsuarioService()
