import axios from 'axios'
import { API_URL } from '../serverConstants'
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
  accessToken: string
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

// Con "Recordarme" los tokens van a localStorage y la sesión sobrevive al cerrar el navegador;
// sin tildarlo van a sessionStorage y se pierden al cerrar la pestaña
const leerToken = (clave: string) =>
  window.sessionStorage.getItem(clave) ?? window.localStorage.getItem(clave)

const almacenDeLaSesion = () =>
  window.sessionStorage.getItem('refreshToken') !== null ? window.sessionStorage : window.localStorage

class AuthService {
  async login(loginData: LoginData, recordarme = true): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(API_URL + '/auth/login', loginData)

    this.cerrarSesion()
    const almacen = recordarme ? window.localStorage : window.sessionStorage
    almacen.setItem('accessToken', response.data.accessToken)
    almacen.setItem('refreshToken', response.data.refreshToken)

    return response.data
  }

  async register(registerData: RegisterData): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(API_URL + '/auth/register', registerData)

    return response.data
  }

  estaAutenticado(): boolean {
    const token = leerToken('accessToken')

    return token !== null && token !== ''
  }

  tieneRefreshToken(): boolean {
    const token = leerToken('refreshToken')

    return token !== null && token !== ''
  }

  async renovarAccessToken(): Promise<string | null> {
    const refreshToken = leerToken('refreshToken')

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

      this.guardarAccessToken(accessToken)

      return accessToken
    } catch {
      this.cerrarSesion()
      return null
    }
  }

  guardarAccessToken(accessToken: string) {
    almacenDeLaSesion().setItem('accessToken', accessToken)
  }

  cerrarSesion() {
    for (const almacen of [window.localStorage, window.sessionStorage]) {
      almacen.removeItem('accessToken')
      almacen.removeItem('refreshToken')
    }
    window.localStorage.removeItem('user')
  }

  tokenDeStorage(): string {
    return leerToken('accessToken') || ''
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
