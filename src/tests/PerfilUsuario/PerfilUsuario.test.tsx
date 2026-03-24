import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { cleanup, render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { PerfilUsuario } from '../../pages/PerfilUsuario'
import {
  usuarioService,
  type LibroUsuario,
  type UsuarioResponse
} from '../../services/UsuarioService'
import type { PagResModel } from '../../models/PaginationModel'

/* Mockeo useNavigate */
const mockNavigate = vi.fn()
const mockReload = vi.fn()

/* Mockeo react-router-dom */
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate
  }
})

/* Mockeo UsuarioService */
vi.mock('../../services/UsuarioService', () => ({
  usuarioService: {
    getUsuario: vi.fn(),
    getLibros: vi.fn(),
    patchUsuario: vi.fn(),
    deleteLibro: vi.fn()
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

const renderComponent = () => {
  return render(
    <MemoryRouter>
      <PerfilUsuario />
    </MemoryRouter>
  )
}

describe('PerfilUsuario', () => {
  beforeEach(() => {
    vi.clearAllMocks()

    vi.mocked(usuarioService.getUsuario).mockResolvedValue(USUARIO_COMPLETO_MOCK)
    vi.mocked(usuarioService.getLibros).mockResolvedValue(MIS_LIBROS_MOCK)

    /* Mockeo window.location.reload */
    Object.defineProperty(window, 'location', {
      value: { reload: mockReload },
      writable: true
    })
  })

  afterEach(() => {
    cleanup()
  })

  it('muestra spinner al iniciar', () => {
    vi.mocked(usuarioService.getUsuario).mockImplementation(() => new Promise(() => {}))
    vi.mocked(usuarioService.getLibros).mockImplementation(() => new Promise(() => {}))

    renderComponent()

    expect(screen.getByTestId('spinner')).toBeInTheDocument()
  })

  it('renderiza perfil y libros cuando las requests son exitosas', async () => {
    renderComponent()

    expect(await screen.findByTestId('perfil-header')).toBeInTheDocument()
    expect(screen.getByTestId('perfil-info')).toBeInTheDocument()
    expect(screen.getByTestId('perfil-stats')).toBeInTheDocument()
    expect(screen.getByTestId('perfil-gestor-libros')).toBeInTheDocument()

    expect(screen.getByText('Diego Maradona')).toBeInTheDocument()
    expect(screen.getByText('diego@maradona.com')).toBeInTheDocument()
    expect(screen.getByText('Clean Code')).toBeInTheDocument()

    expect(usuarioService.getUsuario).toHaveBeenCalledTimes(1)
    expect(usuarioService.getLibros).toHaveBeenCalledTimes(1)
  })

  it('muestra ErrorCard si falla la carga inicial', async () => {
    vi.mocked(usuarioService.getUsuario).mockRejectedValueOnce(new Error('boom'))

    renderComponent()

    expect(await screen.findByTestId('error-card')).toBeInTheDocument()
  })

  it('actualiza usuario y vuelve a pedir getUsuario', async () => {
    vi.mocked(usuarioService.patchUsuario).mockResolvedValue({ ok: true })

    renderComponent()

    expect(await screen.findByTestId('perfil-header')).toBeInTheDocument()

    screen.getByTestId('editar-perfil').click()

    expect(await screen.findByTestId('patch-usuario')).toBeInTheDocument()

    screen.getByTestId('patch-usuario').click()

    await waitFor(() => {
      expect(usuarioService.patchUsuario).toHaveBeenCalledTimes(1)
    })

    await waitFor(() => {
      expect(usuarioService.getUsuario).toHaveBeenCalledTimes(2)
    })
  })

  it('elimina libro y vuelve a pedir getLibros', async () => {
    vi.mocked(usuarioService.deleteLibro).mockResolvedValue({})

    renderComponent()

    expect(await screen.findByTestId('perfil-gestor-libros')).toBeInTheDocument()

    screen.getByTestId('delete-libro').click()

    await waitFor(() => {
      expect(usuarioService.deleteLibro).toHaveBeenCalledWith(1)
    })

    await waitFor(() => {
      expect(usuarioService.getLibros).toHaveBeenCalledTimes(2)
    })
  })

  it('desde ErrorCard, retry recarga la página y volver navega hacia atrás', async () => {
    vi.mocked(usuarioService.getUsuario).mockRejectedValue(new Error('boom'))

    renderComponent()

    expect(await screen.findByTestId('error-card')).toBeInTheDocument()

    screen.getByTestId('reintentar-button').click()
    expect(mockReload).toHaveBeenCalled()

    screen.getByTestId('volver-button').click()
    expect(mockNavigate).toHaveBeenCalledWith(-1)
  })
})
