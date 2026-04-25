import axios from 'axios'
import { API_URL } from '../env'
import { jwtDecode } from 'jwt-decode'

export type LoginData = {
  email: string
  password: string
}

export type RegisterData = {
  nombreCompleto: string
  email: string
  password: string
}

export type AuthResponse = {
  accessToken: string,
  refreshToken: string
}

export type AuthPayload = {
  sub: string
  role: 'LECTOR' | 'PUBLICADOR' | 'LECTOR_PUBLICADOR'
  iat: number
  exp: number
}

class AuthService {
  async login(loginData: LoginData): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(API_URL + '/auth/login', loginData)

    window.localStorage.setItem('accessToken', response.data.accessToken)
    window.localStorage.setItem('refreshToken', response.data.refreshToken)

    return response.data
  }

  async register(registerData: RegisterData): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(API_URL + '/auth/register', registerData)

    return response.data
  }

  estaAutenticado(): boolean {
    const token = window.localStorage.getItem('accessToken')

    return token !== null && token !== ''
  }

  cerrarSesion() {
    window.localStorage.removeItem('accessToken')
  }

  tokenDeStorage(): string {
    const token = window.localStorage.getItem('accessToken') || ''

    return token
  }

  userDeStorage(): AuthPayload {
    const token = this.tokenDeStorage()

    const payload = jwtDecode<AuthPayload>(token)

    return payload
  }

  puedePublicar(): boolean {
    const user = this.userDeStorage()

    return user?.role === 'PUBLICADOR' || user?.role === 'LECTOR_PUBLICADOR'
  }

  puedeReservar(): boolean {
    const user = this.userDeStorage()

    return user?.role === 'LECTOR' || user?.role === 'LECTOR_PUBLICADOR'
  }
}

export const authService = new AuthService()
