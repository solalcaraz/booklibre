import axios from 'axios'

const API_URL = 'http://localhost:9000'

export const api = axios.create({
  baseURL: API_URL
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status !== 401) {
      return Promise.reject(error)
    }

    const refreshToken = localStorage.getItem('refreshToken')
    if (!refreshToken) {
      return Promise.reject(error)
    }

    try {
      const { data } = await axios.post('http://localhost:9000/auth/refresh', {
        token: refreshToken
      })

      localStorage.setItem('accessToken', data.token)

      // Reintentar request original
      error.config.headers.Authorization = `Bearer ${data.token}`

      return api(error.config)
    } catch (err) {
      // refresh falló → logout
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')

      window.location.href = '/auth/login'

      return Promise.reject(err)
    }
  }
)