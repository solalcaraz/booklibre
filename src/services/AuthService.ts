import axios from "axios";
import { API_URL } from "../env";

export type LoginData = {
  email: string,
  password: string
}

export type RegisterData = {
  nombreCompleto: string
  email: string
  password: string
}

type AuthResponse = {
  id: number,
  nombreCompleto: string,
  imagen: string,
  bibliokarmas: number
}

class AuthService {
  async login(loginData: LoginData): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(API_URL + '/auth/login', loginData)

    return response.data
  }

  async register(registerData: RegisterData): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(API_URL + '/auth/register', registerData)

    return response.data
  }

  estaAutenticado(): boolean {
    const user = window.localStorage.getItem('user')

    return user !== null && user !== ''
  }

  cerrarSesion(){
    window.localStorage.removeItem('user')
  }

  userDeStorage(){
    const user = JSON.parse(localStorage.getItem('user') || '{}')

    return user
  }
}

export const authService = new AuthService()
