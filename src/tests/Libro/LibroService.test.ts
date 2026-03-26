import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import { libroService } from '../../services/LibroService'
import { authService } from '../../services/AuthService'
import { Genero, Idioma, Estado, TipoDeLibro } from '../../models/LibroResponseModel'

vi.mock('axios')
const mockedAxios = vi.mocked(axios, true)

vi.mock('../services/AuthService', () => ({
    authService: {
        userDeStorage: vi.fn()
    }
}))

const LIBRO_FORM_MOCK = {
    titulo: "Rayuela",
    tipo: TipoDeLibro.COMUN,
    genero: Genero.LITERATURA_CLASICA,
    autor: "Cortázar",
    idioma: Idioma.ESPANOL,
    estado: Estado.BUENO,
    editorial: "Alfaguara",
    fecha_publicacion: "1963-01-01",
    paginas: "600",
    isbn13: "9788437604947",
    descripcion: "Novela",
    portada: "url_portada"
}

describe('LibroService', () => {
    beforeEach(() => {
        vi.clearAllMocks()
        const userStorageSpy = vi.spyOn(authService, 'userDeStorage')
        userStorageSpy.mockReturnValue({ id: 10, nombreCompleto: "Julio" })
    })

    it('create - envía los Enums correctamente al backend', async () => {
        mockedAxios.post.mockResolvedValue({ data: {} })

        await libroService.create(LIBRO_FORM_MOCK)

        expect(mockedAxios.post).toHaveBeenCalledWith(
        expect.stringContaining('/libro/nuevo'),
        expect.objectContaining({
            tipo: "COMUN",
            genero: "LITERATURA_CLASICA",
            idDueno: 10
        })
        )
    })

    it('update - envía el ID y los datos actualizados correctamente', async () => {
        mockedAxios.put.mockResolvedValue({ data: {} })
        const idLibro = "123"

        await libroService.update(idLibro, LIBRO_FORM_MOCK)

        expect(mockedAxios.put).toHaveBeenCalledWith(
            expect.stringContaining(`/libro/${idLibro}`),
            expect.objectContaining({
                id: 123,
                titulo: "Rayuela",
                idDueno: 10
            })
        )
    })

    it('getById - retorna los datos del libro correctamente', async () => {
        const mockResponse = { ...LIBRO_FORM_MOCK, id: 1, idDueno: 10 }
        mockedAxios.get.mockResolvedValue({ data: mockResponse })

        const result = await libroService.getById("1")

        expect(mockedAxios.get).toHaveBeenCalledWith(expect.stringContaining('/libro/1'))
        expect(result).toEqual(mockResponse)
    })

    it('create - lanza una excepción si el servidor devuelve un error', async () => {
        mockedAxios.post.mockRejectedValue(new Error('Error interno del servidor'))

        await expect(libroService.create(LIBRO_FORM_MOCK)).rejects.toThrow('Error interno del servidor')
        
        expect(mockedAxios.post).toHaveBeenCalledTimes(1)
    })

    it('mapToDTO - convierte correctamente las páginas de String a Number', async () => {
        mockedAxios.post.mockResolvedValue({ data: {} })
        const dataConPinasComoString = { ...LIBRO_FORM_MOCK, paginas: "500" }

        await libroService.create(dataConPinasComoString)

        expect(mockedAxios.post).toHaveBeenCalledWith(
            expect.any(String),
            expect.objectContaining({
                paginas: 500
            })
        )
    })
})