import axios from 'axios'
import { API_URL } from '../env'
import { authService } from '../services/AuthService'

export const api = axios.create({
  baseURL: API_URL,
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
