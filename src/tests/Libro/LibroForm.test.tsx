import { render, screen, fireEvent, waitFor, within } from '@testing-library/react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import LibroForm from '../../pages/LibroForm'
import { libroService } from '../../services/LibroService'
import { authService } from '../../services/AuthService'
import { Genero, Idioma, Estado, TipoDeLibro } from '../../models/LibroResponseModel'

const mockNavigate = vi.fn()

vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom')
    return {
        ...actual,
        useNavigate: () => mockNavigate
    }
})
vi.mock('../services/LibroService')
vi.mock('../services/AuthService')

describe('LibroForm - Casos de Borde', () => {
    beforeEach(() => {
        vi.clearAllMocks()
        vi.spyOn(authService, 'userDeStorage').mockReturnValue({ id: 1 })
    })

    it('muestra mensaje de error si el libro no le pertenece al usuario logueado', async () => {
        vi.spyOn(libroService, 'getById').mockResolvedValue({
            id: 123,
            titulo: "Libro Prohibido",
            tipo: TipoDeLibro.COLECCIONABLE,
            genero: Genero.CIENCIA_FICCION,
            autor: "Isaac Asimov",
            idioma: Idioma.INGLES,
            estado: Estado.EXCELENTE,
            editorial: "Minotauro",
            fechaPublicacion: "1951-01-01",
            paginas: 250,
            isbn13: "9780553293357",
            descripcion: "Fundación",
            portada: "portada.jpg",
            idDueno: 99
        })

        render(
            <MemoryRouter initialEntries={['/libros/editar/123']}>
                <Routes>
                    <Route path="/libros/editar/:id" element={<LibroForm />} />
                </Routes>
            </MemoryRouter>
        )

        const errorMessage = await screen.findByText(/No tienes permisos para editar este libro/i)
        expect(errorMessage).toBeInTheDocument()
    })
})

describe('LibroForm - Validaciones de Formulario', () => {
    let createSpy: any;

    beforeEach(() => {
        vi.clearAllMocks()
        vi.spyOn(authService, 'userDeStorage').mockReturnValue({ id: 1 })
        createSpy = vi.spyOn(libroService, 'create').mockResolvedValue(undefined)
    })

    it('al intentar enviar un formulario vacío, se muestran los mensajes de error de validación', async () => {
        render(
            <MemoryRouter initialEntries={['/libros/nuevo']}>
                <Routes>
                    <Route path="/libros/nuevo" element={<LibroForm />} />
                </Routes>
            </MemoryRouter>
        )

        const submitBtn = screen.getByRole('button', { name: /Añadir Libro/i })
        fireEvent.click(submitBtn)

        await waitFor(() => {
            const errorMessages = screen.getAllByText(/Este campo es requerido/i)
            expect(errorMessages.length).toBeGreaterThan(5)
            
            expect(screen.getByText(/La cantidad de páginas debe ser mayor a 0/i)).toBeInTheDocument()
        })

        expect(createSpy).not.toHaveBeenCalled()
    })

    it('si el usuario escribe en un campo con error, el mensaje de error desaparece', async () => {
        render(
            <MemoryRouter initialEntries={['/libros/nuevo']}>
                <Routes>
                    <Route path="/libros/nuevo" element={<LibroForm />} />
                </Routes>
            </MemoryRouter>
        )

        const submitBtn = screen.getByRole('button', { name: /Añadir Libro/i })
        fireEvent.click(submitBtn)

        const allErrors = await screen.findAllByText(/Este campo es requerido/i)
        expect(allErrors.length).toBeGreaterThan(1)

        const inputTitulo = screen.getByLabelText(/Título/i)
        
        const containerTitulo = inputTitulo.parentElement!

        fireEvent.change(inputTitulo, { target: { value: 'Rayuela', name: 'titulo' } })

        const errorEnTitulo = within(containerTitulo).queryByText(/Este campo es requerido/i)
        expect(errorEnTitulo).not.toBeInTheDocument()
    })

    it('cuando se completan todos los campos y se envía, llama al service y redirige', async () => {
        render(
            <MemoryRouter initialEntries={['/libros/nuevo']}>
                <Routes>
                    <Route path="/libros/nuevo" element={<LibroForm />} />
                </Routes>
            </MemoryRouter>
        )

        fireEvent.change(screen.getByLabelText(/Título/i), { target: { value: 'El Aleph', name: 'titulo' } })
        fireEvent.change(screen.getByLabelText(/Autor/i), { target: { value: 'Borges', name: 'autor' } })
        fireEvent.change(screen.getByLabelText(/Páginas/i), { target: { value: '200', name: 'paginas' } })
        fireEvent.change(screen.getByLabelText(/ISBN-13/i), { target: { value: '9781234567890', name: 'isbn13' } })
        fireEvent.change(screen.getByLabelText(/Descripción/i), { target: { value: 'Cuentos geniales', name: 'descripcion' } })
        
        fireEvent.change(screen.getByLabelText(/Tipo/i), { target: { value: 'COMUN', name: 'tipo' } })
        fireEvent.change(screen.getByLabelText(/Género/i), { target: { value: 'DRAMA', name: 'genero' } })
        fireEvent.change(screen.getByLabelText(/Idioma/i), { target: { value: 'ESPANOL', name: 'idioma' } })
        fireEvent.change(screen.getByLabelText(/Estado/i), { target: { value: 'BUENO', name: 'estado' } })
        fireEvent.change(screen.getByLabelText(/Editorial/i), { target: { value: 'Emecé', name: 'editorial' } })
        fireEvent.change(screen.getByLabelText(/Fecha Publicación/i), { target: { value: '1945-01-01', name: 'fecha_publicacion' } })

        fireEvent.click(screen.getByRole('button', { name: /Añadir Libro/i }))

        await waitFor(() => {
            expect(createSpy).toHaveBeenCalled()
            expect(mockNavigate).toHaveBeenCalledWith('/profile')
        })
    })
})