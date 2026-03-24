import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import {
  usuarioService,
  type LibroUsuario,
  type UsuarioResponse
} from '../../services/UsuarioService'
import { authService } from '../../services/AuthService'
import type { PagResModel } from '../../models/PaginationModel'

vi.mock('axios')
const mockedAxios = vi.mocked(axios, true)

vi.mock('../../services/AuthService', () => ({
  authService: {
    userDeStorage: vi.fn()
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
      portada: 'clean-code.jpg'
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
      id: 1,
      nombreCompleto: 'Diego Maradona',
      imagen: 'http://localhost:9000/imagenes/diego.jpg',
      bibliokarmas: 1986
    })
  })

  it('get usuario - happy path: obtiene el usuario y retorna response.data', async () => {
    mockedAxios.get.mockResolvedValue({ data: USUARIO_COMPLETO_MOCK })

    const result = await usuarioService.getUsuario()

    expect(authService.userDeStorage).toHaveBeenCalled()
    expect(mockedAxios.get).toHaveBeenCalledTimes(1)
    expect(mockedAxios.get).toHaveBeenCalledWith(expect.stringContaining('/usuario/1'))
    expect(result).toEqual(USUARIO_COMPLETO_MOCK)
  })

  it('get usuario - sad path: lanza error si falla la request', async () => {
    mockedAxios.get.mockRejectedValueOnce(new Error('Usuario no encontrado'))

    await expect(usuarioService.getUsuario()).rejects.toThrow('Usuario no encontrado')

    expect(authService.userDeStorage).toHaveBeenCalled()
    expect(mockedAxios.get).toHaveBeenCalledTimes(1)
  })

  it('get libros - happy path: default filter & page', async () => {
    mockedAxios.get.mockResolvedValue({ data: MIS_LIBROS_MOCK })

    const result = await usuarioService.getLibros()

    expect(authService.userDeStorage).toHaveBeenCalled()
    expect(mockedAxios.get).toHaveBeenCalledTimes(1)
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining('/misLibros/1?page=0&size=3&filter=Todos')
    )
    expect(result).toEqual(MIS_LIBROS_MOCK)
  })

  it('get libros - sad path: lanza error si falla la request', async () => {
    mockedAxios.get.mockRejectedValue(new Error('Error al obtener libros'))

    await expect(usuarioService.getLibros()).rejects.toThrow('Error al obtener libros')

    expect(authService.userDeStorage).toHaveBeenCalled()
    expect(mockedAxios.get).toHaveBeenCalledTimes(1)
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

    mockedAxios.patch.mockResolvedValue({ data: {} })

    const result = await usuarioService.patchUsuario(userPatch)

    expect(authService.userDeStorage).toHaveBeenCalled()
    expect(mockedAxios.patch).toHaveBeenCalledTimes(1)
    expect(mockedAxios.patch).toHaveBeenCalledWith(expect.stringContaining('/usuario/1'), userPatch)
    expect(result).toEqual({})
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

    mockedAxios.patch.mockRejectedValue(new Error('Error al actualizar usuario'))

    await expect(usuarioService.patchUsuario(userPatch)).rejects.toThrow(
      'Error al actualizar usuario'
    )

    expect(authService.userDeStorage).toHaveBeenCalled()
    expect(mockedAxios.patch).toHaveBeenCalledTimes(1)
  })

  it('delete libro - happy path: elimina libro y no devuelve nada', async () => {
    mockedAxios.delete.mockResolvedValue({ data: {} })

    const result = await usuarioService.deleteLibro(15)

    expect(mockedAxios.delete).toHaveBeenCalledTimes(1)
    expect(mockedAxios.delete).toHaveBeenCalledWith(expect.stringContaining('/misLibros/15'))
    expect(result).toEqual({})
  })

  it('delete libro - sad path: lanza error si falla el borrado', async () => {
    mockedAxios.delete.mockRejectedValue(new Error('Error al eliminar el libro'))

    await expect(usuarioService.deleteLibro(15)).rejects.toThrow('Error al eliminar el libro')

    expect(mockedAxios.delete).toHaveBeenCalledTimes(1)
  })
})
