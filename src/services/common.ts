import type { AxiosResponse } from 'axios'
import axios from 'axios'
import { API_URL } from '../env'
import { authService } from './AuthService'

export const getAxiosData = async <T>(query: () => Promise<AxiosResponse<T>>): Promise<T> => {
  const response = await query()
  return response.data
}

export const customAxios = axios.create({
  baseURL: API_URL,
  withCredentials: true,
  paramsSerializer: (params) => {
    const urlParams = new URLSearchParams()

    Object.entries(params).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        value.forEach((v) => urlParams.append(key, String(v)))
      } else if (value !== undefined && value !== null && value !== '') {
        urlParams.append(key, String(value))
      }
    })
    return urlParams.toString()
  }
})

//TEMPORAL PARA QUE FUNCIONE MI VISTA
customAxios.interceptors.request.use(
  (config) => {
    // Antes de que salga la petición, buscamos el token
    const token = authService.tokenDeStorage()

    // Si hay un token, se lo inyectamos a los headers
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)
