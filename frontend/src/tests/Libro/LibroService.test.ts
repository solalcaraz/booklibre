import { describe, it, expect, vi, beforeEach } from 'vitest'
import { libroService } from '../../services/LibroService'
import { authService } from '../../services/AuthService'
import { Genero, Idioma, Estado, TipoDeLibro } from '../../models/LibroResponseModel'

const mockApi = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn()
}

vi.mock('axios', () => ({
    default: {
        create: vi.fn(() => ({
            ...mockApi,
            interceptors: {
                request: { use: vi.fn() },
                response: { use: vi.fn() }
            }
        }))
    }
}))

vi.mock('../../services/AuthService', () => ({
    authService: {
        userDeStorage: vi.fn(),
        tokenDeStorage: vi.fn(() => 'fake-token')
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
        vi.mocked(authService.tokenDeStorage).mockReturnValue('fake-token')
    })

    it('create - envía los Enums correctamente al backend', async () => {
        mockApi.post.mockResolvedValue({ data: {} })

        await libroService.create(LIBRO_FORM_MOCK)

        expect(mockApi.post).toHaveBeenCalledWith(
        expect.stringContaining('/libro/nuevo'),
        expect.objectContaining({
            tipo: "COMUN",
            genero: "LITERATURA_CLASICA",
            duenoId: null
        }),
        expect.objectContaining({
            headers: { Authorization: 'Bearer fake-token' }
        })
        )
    })

    it('update - envía el ID y los datos actualizados correctamente', async () => {
        mockApi.put.mockResolvedValue({ data: {} })
        const idLibro = "123"

        await libroService.update(idLibro, LIBRO_FORM_MOCK)

        expect(mockApi.put).toHaveBeenCalledWith(
            expect.stringContaining(`/libro/${idLibro}`),
            expect.objectContaining({
                id: "123",
                titulo: "Rayuela",
                duenoId: null
            }),
            expect.objectContaining({
                headers: { Authorization: 'Bearer fake-token' }
            })
        )
    })

    it('getByIdForUser - retorna los datos del libro correctamente', async () => {
        const mockResponse = { ...LIBRO_FORM_MOCK, id: 1, idDueno: 10 }
        mockApi.get.mockResolvedValue({ data: mockResponse })

        const result = await libroService.getByIdForUser("1")

        expect(mockApi.get).toHaveBeenCalledWith(expect.stringContaining('/libro/1'),
        {
            headers: {
                Authorization: 'Bearer fake-token'
            }
        })
        expect(result).toEqual(mockResponse)
    })

    it('create - lanza una excepción si el servidor devuelve un error', async () => {
        mockApi.post.mockRejectedValue(new Error('Error interno del servidor'))

        await expect(libroService.create(LIBRO_FORM_MOCK)).rejects.toThrow('Error interno del servidor')
        
        expect(mockApi.post).toHaveBeenCalledTimes(1)
    })

    it('mapToDTO - convierte correctamente las páginas de String a Number', async () => {
        mockApi.post.mockResolvedValue({ data: {} })
        const dataConPinasComoString = { ...LIBRO_FORM_MOCK, paginas: "500" }

        await libroService.create(dataConPinasComoString)

        expect(mockApi.post).toHaveBeenCalledWith(
            expect.any(String),
            expect.objectContaining({
                paginas: 500
            })
        )
    })
})