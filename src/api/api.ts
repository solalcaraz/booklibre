import axios from 'axios'
import { API_URL } from '../env'
import { authService } from '../services/AuthService'

export const api = axios.create({
  baseURL: API_URL
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status !== 401) {
      return Promise.reject(error)
    }

    const originalRequest = error.config

    if (originalRequest?._retry) {
      authService.cerrarSesion()
      window.location.href = '/auth/login'
      return Promise.reject(error)
    }

    originalRequest._retry = true

    if (!authService.tieneRefreshToken()) {
      authService.cerrarSesion()
      window.location.href = '/auth/login'
      return Promise.reject(error)
    }

    try {
      const accessToken = await authService.renovarAccessToken()

      if (!accessToken) {
        window.location.href = '/auth/login'
        return Promise.reject(error)
      }

      originalRequest.headers.Authorization = `Bearer ${accessToken}`

      return api(originalRequest)
    } catch (err) {
      authService.cerrarSesion()
      window.location.href = '/auth/login'

      return Promise.reject(err)
    }
  }
)
