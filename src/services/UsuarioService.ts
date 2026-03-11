import axios from 'axios'
import type { LibroUsuario } from '../components/PerfilUsuario/PerfilLibro'
import { API_URL } from '../env'
import type { EditProfileData } from '../components/PerfilUsuario/PerfilEditModal'

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

  async patchUsuario(userPatch: EditProfileData) {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const response = await axios.patch(API_URL + `/usuario/${user.id}`, userPatch)

    return response.data
  }

  async deleteLibro(id: number) {
    const response = await axios.delete(API_URL + `/misLibros/${id}`)

    return response.data
  }
}

export const usuarioService = new UsuarioService()
