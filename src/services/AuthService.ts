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

type RefreshResponse = {
  token?: string
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

  tieneRefreshToken(): boolean {
    const token = window.localStorage.getItem('refreshToken')

    return token !== null && token !== ''
  }

  async renovarAccessToken(): Promise<string | null> {
    const refreshToken = window.localStorage.getItem('refreshToken')

    if (!refreshToken) {
      this.cerrarSesion()
      return null
    }

    try {
      const response = await axios.post<RefreshResponse>(API_URL + '/auth/refresh', {
        token: refreshToken
      })
      const accessToken = response.data.token

      if (!accessToken) {
        this.cerrarSesion()
        return null
      }

      window.localStorage.setItem('accessToken', accessToken)

      return accessToken
    } catch {
      this.cerrarSesion()
      return null
    }
  }

  cerrarSesion() {
    window.localStorage.removeItem('accessToken')
    window.localStorage.removeItem('refreshToken')
    window.localStorage.removeItem('user')
  }

  tokenDeStorage(): string {
    const token = window.localStorage.getItem('accessToken') || ''

    return token
  }

  userDeStorage(): AuthPayload | null {
    const token = this.tokenDeStorage()

    if (!token) {
      return null
    }

    try {
      return jwtDecode<AuthPayload>(token)
    } catch {
      this.cerrarSesion()
      return null
    }
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
