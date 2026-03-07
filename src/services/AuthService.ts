/* import axios from "axios"; */

/* const API_URL = 'http://localhost:9000' */

class AuthService {
  /* async login(usuario: LoginDTO): Promise<AuthResponse>{
    const response = await axios.post<AuthResponse>(API_URL + '/auth/usuario/login', credenciales)
    return response.data
  } */
  /* async register(usuario: RegisterDTO): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>(API_URL + '/auth/login', usuario)
    return response.data
  } */
}

export const authService = new AuthService()
