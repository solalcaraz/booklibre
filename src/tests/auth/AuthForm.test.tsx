import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, waitFor, cleanup } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { AuthForm } from '../../pages/AuthForm'
import { authService } from '../../services/AuthService'
import { Auth } from '../../classes/Auth'

const mockNavigate = vi.fn()
const mockUseParams = vi.fn()

/* Mockeo react-router-dom */
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => mockUseParams()
  }
})

/* Mockeo las funciones que voy a usar del service */
vi.mock('../../services/AuthService', () => ({
  authService: {
    estaAutenticado: vi.fn(),
    login: vi.fn(),
    register: vi.fn()
  }
}))

/* Mockeo la clase Auth para controlar validaciones */
const mockValidateLogin = vi.fn()
const mockValidateRegister = vi.fn()
const mockToLoginData = vi.fn()
const mockToRegisterData = vi.fn()
const mockHasErrors = vi.fn()

vi.mock('../../classes/Auth', () => {
  return {
    Auth: vi
      .fn()
      .mockImplementation(
        (email: string, password: string, confirmPassword: string, fullName: string) => ({
          email,
          password,
          confirmPassword,
          fullName,
          validateLogin: mockValidateLogin,
          validateRegister: mockValidateRegister,
          toLoginData: mockToLoginData,
          toRegisterData: mockToRegisterData
        })
      )
  }
})

/* Renderizo la pagina dentro de un Router en memoria */
const renderComponent = () => {
  return render(
    <MemoryRouter initialEntries={['/auth/login']}>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />
      </Routes>
    </MemoryRouter>
  )
}

describe('AuthForm', () => {
  beforeEach(() => {
    vi.clearAllMocks()

    /* Mockeo el metodo estatico en false para que no corte el happy path */
    mockHasErrors.mockReturnValue(false)
    ;(Auth.hasErrors as unknown as ReturnType<typeof vi.fn>) = mockHasErrors

    vi.spyOn(Storage.prototype, 'setItem')
  })

  afterEach(() => {
    cleanup()
  })

  it('renderiza el formulario de login correctamente', () => {
    mockUseParams.mockReturnValue({ mode: 'login' })

    renderComponent()

    expect(screen.getByRole('heading', { name: /booklibre/i })).toBeInTheDocument()
    expect(screen.getByText(/por favor ingresa tus datos/i)).toBeInTheDocument()

    expect(screen.getByLabelText(/correo electrónico/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^contraseña$/i)).toBeInTheDocument()
    expect(screen.queryByLabelText(/nombre completo/i)).not.toBeInTheDocument()
    expect(screen.queryByLabelText(/confirmar contraseña/i)).not.toBeInTheDocument()

    expect(screen.getByRole('checkbox', { name: /recordarme/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /ingresar/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /regístrate gratis/i })).toHaveAttribute(
      'href',
      '/auth/register'
    )
  })

  it('renderiza el formulario de register correctamente', () => {
    mockUseParams.mockReturnValue({ mode: 'register' })

    renderComponent()

    expect(screen.getByRole('heading', { name: /crear una cuenta/i })).toBeInTheDocument()
    expect(screen.getByText(/únete a nuestra comunidad/i)).toBeInTheDocument()

    expect(screen.getByLabelText(/nombre completo/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/correo electrónico/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^contraseña$/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/confirmar contraseña/i)).toBeInTheDocument()

    expect(screen.queryByRole('checkbox', { name: /recordarme/i })).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: /crear cuenta/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /inicia sesión/i })).toHaveAttribute(
      'href',
      '/auth/login'
    )
  })

  it('redirige a /home si el usuario ya está autenticado', () => {
    vi.mocked(authService.estaAutenticado).mockReturnValue(true)

    renderComponent()

    expect(mockNavigate).toHaveBeenCalledWith('/home')
  })

  it('redirige a /auth/login si el mode es inválido', () => {
    mockUseParams.mockReturnValue({ mode: 'cualquier-cosa' })

    renderComponent()

    expect(mockNavigate).toHaveBeenCalledWith('/auth/login')
  })

  it('actualiza los inputs cuando el usuario escribe', async () => {
    const user = userEvent.setup()
    mockUseParams.mockReturnValue({ mode: 'register' })

    renderComponent()

    const fullNameInput = screen.getByLabelText(/nombre completo/i)
    const emailInput = screen.getByLabelText(/correo electrónico/i)
    const passwordInput = screen.getByLabelText(/^contraseña$/i)
    const confirmPasswordInput = screen.getByLabelText(/confirmar contraseña/i)

    await user.type(fullNameInput, 'Thomas')
    await user.type(emailInput, 'thomas@mail.com')
    await user.type(passwordInput, '123456')
    await user.type(confirmPasswordInput, '123456')

    expect(fullNameInput).toHaveValue('Thomas')
    expect(emailInput).toHaveValue('thomas@mail.com')
    expect(passwordInput).toHaveValue('123456')
    expect(confirmPasswordInput).toHaveValue('123456')
  })

  it('no envía login si hay errores de validación y muestra mensajes de error', async () => {
    mockUseParams.mockReturnValue({ mode: 'login' })
    vi.mocked(authService.estaAutenticado).mockReturnValue(false)
    mockValidateLogin.mockReturnValue({
      email: 'Email requerido',
      password: 'Contraseña requerida'
    })
    mockHasErrors.mockReturnValue(true)

    renderComponent()

    const submitButton = screen.getByTestId('authButton')
    await userEvent.click(submitButton)

    await waitFor(() => {
      expect(mockValidateLogin).toHaveBeenCalled()
      expect(authService.login).not.toHaveBeenCalled()
      expect(mockNavigate).not.toHaveBeenCalledWith('/home')
      expect(screen.getByTestId('emailError')).toBeInTheDocument()
      expect(screen.getByTestId('passwordError')).toBeInTheDocument()
    })
  })

  it('envía login correctamente, guarda el user en localStorage y navega a /home', async () => {
    const user = userEvent.setup()
    mockUseParams.mockReturnValue({ mode: 'login' })

    mockToLoginData.mockReturnValue({
      email: 'test@mail.com',
      password: '123456'
    })

    const fakeUser = {
      id: 1,
      nombreCompleto: 'Thomas',
      imagen: 'imagenMock',
      bibliokarmas: 1986
    }

    vi.mocked(authService.login).mockResolvedValue(fakeUser)

    renderComponent()

    await user.type(screen.getByLabelText(/correo electrónico/i), 'test@mail.com')
    await user.type(screen.getByLabelText(/^contraseña$/i), '123456')
    await user.click(screen.getByRole('button', { name: /ingresar/i }))

    await waitFor(() => {
      expect(authService.login).toHaveBeenCalledWith({
        email: 'test@mail.com',
        password: '123456'
      })
    })

    expect(localStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify(fakeUser))
    expect(mockNavigate).toHaveBeenCalledWith('/home')
  })

  it('envía register correctamente, guarda el user en localStorage y navega a /home', async () => {
    const user = userEvent.setup()
    mockUseParams.mockReturnValue({ mode: 'register' })
    mockValidateRegister.mockReturnValue({})
    mockToRegisterData.mockReturnValue({
      fullName: 'Thomas',
      email: 'test@mail.com',
      password: '123456',
      confirmPassword: '123456'
    })

    const fakeUser = {
      id: 1,
      nombreCompleto: 'Thomas',
      imagen: 'imagenMock',
      bibliokarmas: 1986
    }

    vi.mocked(authService.register).mockResolvedValue(fakeUser)

    renderComponent()

    await user.type(screen.getByLabelText(/nombre completo/i), 'Thomas')
    await user.type(screen.getByLabelText(/correo electrónico/i), 'test@mail.com')
    await user.type(screen.getByLabelText(/^contraseña$/i), '123456')
    await user.type(screen.getByLabelText(/confirmar contraseña/i), '123456')
    await user.click(screen.getByRole('button', { name: /crear cuenta/i }))

    await waitFor(() => {
      expect(authService.register).toHaveBeenCalledWith({
        fullName: 'Thomas',
        email: 'test@mail.com',
        password: '123456',
        confirmPassword: '123456'
      })
    })

    expect(localStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify(fakeUser))
    expect(mockNavigate).toHaveBeenCalledWith('/home')
  })

  it('limpia el error del campo al escribir nuevamente', async () => {
    const user = userEvent.setup()
    mockUseParams.mockReturnValue({ mode: 'register' })

    mockValidateRegister.mockReturnValue({
      fullName: 'El nombre es obligatorio'
    })
    mockHasErrors.mockReturnValue(true)

    renderComponent()

    await user.click(screen.getByRole('button', { name: /crear cuenta/i }))

    expect(screen.getByText(/el nombre es obligatorio/i)).toBeInTheDocument()

    await user.type(screen.getByLabelText(/nombre completo/i), 'T')

    await waitFor(() => {
      expect(screen.queryByText(/el nombre es obligatorio/i)).not.toBeInTheDocument()
    })
  })
})
