import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import { authService } from '../../services/AuthService'
import type { AuthResponse } from '../../services/AuthService'

// mock de axios
vi.mock('axios')
const mockedAxios = vi.mocked(axios, true)

const USUARIO_MOCK: AuthResponse = {
  id: 1,
  nombreCompleto: 'Diego Maradona',
  imagen: 'imagenMock',
  bibliokarmas: 1986
}

describe('AuthService', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  it('login - happy path', async () => {
    mockedAxios.post.mockResolvedValue({ data: USUARIO_MOCK })

    const result = await authService.login({
      email: 'diego@maradona.com',
      password: 'admin'
    })

    expect(mockedAxios.post).toHaveBeenCalledWith(expect.stringContaining('/auth/login'), {
      email: 'diego@maradona.com',
      password: 'admin'
    })
    expect(result).toEqual(USUARIO_MOCK)
  })

  it('login - sad path', async () => {
    mockedAxios.post.mockRejectedValueOnce(new Error('401 Unauthorized'))

    await expect(
      authService.login({
        email: 'diego@maradona.com',
        password: 'wrongpassword'
      })
    ).rejects.toThrow()
  })

  it('register - happy path', async () => {
    mockedAxios.post.mockResolvedValue({ data: USUARIO_MOCK })

    const result = await authService.register({
      nombreCompleto: "Diego Maradona",
      email: 'diego@maradona.com',
      password: 'admin'
    })

    expect(mockedAxios.post).toHaveBeenCalledWith(expect.stringContaining('/auth/register'), {
      nombreCompleto: "Diego Maradona",
      email: 'diego@maradona.com',
      password: 'admin'
    })
    expect(result).toEqual(USUARIO_MOCK)
  })

  it('register - sad path', async () => {
    mockedAxios.post.mockRejectedValueOnce(new Error('400 Bad Request'))

    await expect(
      authService.register({
        nombreCompleto: '',
        email: 'diego@maradona.com',
        password: 'admin'
      })
    ).rejects.toThrow()
  })

  it('estaAutenticado retorna true cuando hay user en localStorage', () => {
    window.localStorage.setItem('user', '123')

    const result = authService.estaAutenticado()

    expect(result).toBe(true)
  })

  it('estaAutenticado retorna false cuando no hay user en localStorage', () => {
    window.localStorage.clear()

    const result = authService.estaAutenticado()

    expect(result).toBe(false)
  })
})
