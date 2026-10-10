import { describe, it, expect, vi, beforeEach } from 'vitest'
import { api } from '../../api/api'
import {
  usuarioService,
  type LibroUsuario,
  type UsuarioResponse
} from '../../services/UsuarioService'
import { authService } from '../../services/AuthService'
import type { PagResModel } from '../../models/PaginationModel'

vi.mock('../../api/api', () => ({
  api: {
    get: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn()
  }
}))

const mockedApi = vi.mocked(api, true)

vi.mock('../../services/AuthService', () => ({
  authService: {
    userDeStorage: vi.fn(),
    tokenDeStorage: vi.fn()
  }
}))

const USUARIO_COMPLETO_MOCK: UsuarioResponse = {
  nombreCompleto: 'Diego Maradona',
  email: 'diego@maradona.com',
  descripcion: 'La Mano de Dios',
  telefono: '1166666666',
  anoUnido: 2026,
  ciudad: 'Lanus, BA',
  bibliokarmas: 1986,
  tipoUsuario: 'Publicador',
  imagen: 'foto.jpg',
  librosLeidos: 5,
  librosPrestados: 2
}

const MIS_LIBROS_MOCK: PagResModel<LibroUsuario> = {
  items: [
    {
      id: 1,
      titulo: 'Clean Code',
      autor: 'Robert C. Martin',
      genero: 'Programación',
      estadoReserva: 'DISPONIBLE',
      agregadoEl: '2026-03-18',
      portada: 'clean-code.jpg',
      clicks: 42
    }
  ],
  page: 0,
  size: 3,
  totalItems: 1,
  totalPages: 1
}

describe('UsuarioService', () => {
  beforeEach(() => {
    vi.clearAllMocks()

    vi.mocked(authService.userDeStorage).mockReturnValue({
      sub: '1',
      role: 'LECTOR_PUBLICADOR',
      iat: 222,
      exp: 444
    })
  })

  it('get usuario - happy path: obtiene el usuario y retorna response.data', async () => {
    mockedApi.get.mockResolvedValue({ data: USUARIO_COMPLETO_MOCK })

    const result = await usuarioService.getUsuario()

    expect(mockedApi.get).toHaveBeenCalledTimes(1)
    expect(mockedApi.get).toHaveBeenCalledWith('/usuario/me')
    expect(result).toEqual(USUARIO_COMPLETO_MOCK)
  })

  it('get usuario - sad path: lanza error si falla la request', async () => {
    mockedApi.get.mockRejectedValueOnce(new Error('Usuario no encontrado'))

    await expect(usuarioService.getUsuario()).rejects.toThrow('Usuario no encontrado')

    expect(mockedApi.get).toHaveBeenCalledTimes(1)
  })

  it('get libros - happy path: default filter & page', async () => {
    mockedApi.get.mockResolvedValue({ data: MIS_LIBROS_MOCK })

    const result = await usuarioService.getLibros()

    expect(mockedApi.get).toHaveBeenCalledTimes(1)
    expect(mockedApi.get).toHaveBeenCalledWith(
      '/misLibros/me?page=0&size=3&filter=Todos&sort=titulo,asc'
    )
    expect(result).toEqual(MIS_LIBROS_MOCK)
  })

  it('get libros - sad path: lanza error si falla la request', async () => {
    mockedApi.get.mockRejectedValue(new Error('Error al obtener libros'))

    await expect(usuarioService.getLibros()).rejects.toThrow('Error al obtener libros')

    expect(mockedApi.get).toHaveBeenCalledTimes(1)
  })

  it('patch usuario - happy path: actualiza usuario y no devuelve nada', async () => {
    const userPatch = {
      nombreCompleto: 'Thomas Reemeerie',
      descripcion: 'Fullstack Developer',
      email: 'thomas@mail.com',
      telefono: '1122334455',
      ciudad: 'Buenos Aires',
      imagen: 'avatar.jpg',
      tipoUsuario: 'Lector'
    }

    const authResponse = {
      accessToken: 'new-access-token',
      refreshToken: 'refresh-token'
    }

    vi.spyOn(Storage.prototype, 'setItem')
    mockedApi.patch.mockResolvedValue({ data: authResponse })

    const result = await usuarioService.patchUsuario(userPatch)

    expect(mockedApi.patch).toHaveBeenCalledTimes(1)
    expect(mockedApi.patch).toHaveBeenCalledWith('/usuario/me', userPatch)
    expect(localStorage.setItem).toHaveBeenCalledWith('accessToken', authResponse.accessToken)
    expect(result).toEqual(authResponse)
  })

  it('patch usuario - sad path: lanza error si falla la actualización', async () => {
    const userPatch = {
      nombreCompleto: 'Thomas Reemeerie',
      descripcion: 'Fullstack Developer',
      email: 'thomas@mail.com',
      telefono: '1122334455',
      ciudad: 'Buenos Aires',
      imagen: 'avatar.jpg',
      tipoUsuario: 'Lector'
    }

    mockedApi.patch.mockRejectedValue(new Error('Error al actualizar usuario'))

    await expect(usuarioService.patchUsuario(userPatch)).rejects.toThrow(
      'Error al actualizar usuario'
    )

    expect(mockedApi.patch).toHaveBeenCalledTimes(1)
  })

  it('delete libro - happy path: elimina libro y no devuelve nada', async () => {
    mockedApi.delete.mockResolvedValue({ data: {} })

    const result = await usuarioService.deleteLibro(15)

    expect(mockedApi.delete).toHaveBeenCalledTimes(1)
    expect(mockedApi.delete).toHaveBeenCalledWith('/misLibros/15')
    expect(result).toEqual({})
  })

  it('delete libro - sad path: lanza error si falla el borrado', async () => {
    mockedApi.delete.mockRejectedValue(new Error('Error al eliminar el libro'))

    await expect(usuarioService.deleteLibro(15)).rejects.toThrow('Error al eliminar el libro')

    expect(mockedApi.delete).toHaveBeenCalledTimes(1)
  })
})
