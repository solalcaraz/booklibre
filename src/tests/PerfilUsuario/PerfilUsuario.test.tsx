import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { cleanup, render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { PerfilUsuario } from '../../pages/PerfilUsuario'
import {
  usuarioService,
  type LibroUsuario,
  type UsuarioResponse
} from '../../services/UsuarioService'
import { authService } from '../../services/AuthService'
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
  initialSort: {
    field: 'titulo',
    direction: 'asc'
  },
  usuarioService: {
    getUsuario: vi.fn(),
    getLibros: vi.fn(),
    patchUsuario: vi.fn(),
    deleteLibro: vi.fn()
  }
}))

vi.mock('../../services/AuthService', () => ({
  authService: {
    puedePublicar: vi.fn()
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
    vi.mocked(authService.puedePublicar).mockReturnValue(true)

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
    expect(screen.getByText('42')).toBeInTheDocument()

    expect(usuarioService.getUsuario).toHaveBeenCalledTimes(1)
    expect(usuarioService.getLibros).toHaveBeenCalledTimes(1)
  })

  it('no muestra el boton de agregar libro cuando el usuario no puede publicar', async () => {
    vi.mocked(authService.puedePublicar).mockReturnValue(false)

    renderComponent()

    expect(await screen.findByTestId('perfil-gestor-libros')).toBeInTheDocument()

    expect(screen.queryByRole('link', { name: /agregar nuevo libro/i })).not.toBeInTheDocument()
  })

  it('no pide ni muestra los libros de mis libros cuando el usuario no puede publicar', async () => {
    vi.mocked(authService.puedePublicar).mockReturnValue(false)

    renderComponent()

    expect(await screen.findByTestId('perfil-gestor-libros')).toBeInTheDocument()

    expect(usuarioService.getUsuario).toHaveBeenCalledTimes(1)
    expect(usuarioService.getLibros).not.toHaveBeenCalled()
    expect(screen.queryByText('Clean Code')).not.toBeInTheDocument()
    expect(screen.queryByTestId('delete-libro')).not.toBeInTheDocument()
  })

  it('al cambiar de tab solo vuelve a pedir los libros', async () => {
    const user = userEvent.setup()

    renderComponent()

    expect(await screen.findByTestId('perfil-gestor-libros')).toBeInTheDocument()

    vi.mocked(usuarioService.getUsuario).mockClear()
    vi.mocked(usuarioService.getLibros).mockClear()

    await user.click(screen.getByRole('button', { name: 'Disponibles' }))

    await waitFor(() => {
      expect(usuarioService.getLibros).toHaveBeenCalledWith(0, 'Disponibles', {
        field: 'titulo',
        direction: 'asc'
      })
    })
    expect(usuarioService.getUsuario).not.toHaveBeenCalled()
    expect(screen.queryByTestId('spinner')).not.toBeInTheDocument()
  })

  it('muestra ErrorCard si falla la carga inicial', async () => {
    vi.mocked(usuarioService.getUsuario).mockRejectedValueOnce(new Error('boom'))

    renderComponent()

    expect(await screen.findByTestId('error-card')).toBeInTheDocument()
  })

  it('actualiza usuario y vuelve a pedir getUsuario', async () => {
    const user = userEvent.setup()

    vi.mocked(usuarioService.patchUsuario).mockResolvedValue({
      accessToken: 'accessToken',
      refreshToken: 'refreshToken'
    })

    renderComponent()

    expect(await screen.findByTestId('perfil-header')).toBeInTheDocument()

    await user.click(screen.getByTestId('editar-perfil'))

    expect(await screen.findByTestId('patch-usuario')).toBeInTheDocument()

    await user.click(screen.getByTestId('patch-usuario'))

    await waitFor(() => {
      expect(usuarioService.patchUsuario).toHaveBeenCalledTimes(1)
    })

    await waitFor(() => {
      expect(usuarioService.getUsuario).toHaveBeenCalledTimes(2)
    })
  })

  it('elimina libro y vuelve a pedir getLibros', async () => {
    const user = userEvent.setup()

    vi.mocked(usuarioService.deleteLibro).mockResolvedValue({})

    renderComponent()

    expect(await screen.findByTestId('perfil-gestor-libros')).toBeInTheDocument()

    await user.click(screen.getByTestId('delete-libro'))

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

    await userEvent.click(screen.getByTestId('reintentar-button'))
    expect(mockReload).toHaveBeenCalled()

    await userEvent.click(screen.getByTestId('volver-button'))
    expect(mockNavigate).toHaveBeenCalledWith(-1)
  })
})
