import axios from "axios";

const API_URL = 'http://localhost:9000'

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
  email: string,
  nombreCompleto: string
}

class AuthService {
  async login(loginData: LoginData): Promise<AuthResponse>{
    const response = await axios.post<AuthResponse>(API_URL + '/auth/login', loginData)

    return response.data
  }

  async register(registerData: RegisterData): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(API_URL + '/auth/register', registerData)

    return response.data
  }
}

export const authService = new AuthService()
