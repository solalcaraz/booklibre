import type { ChangeEvent, SubmitEvent } from 'react'
import { useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import logoImg from '../assets/icons/logo.png'
import { FormInput } from '../components/FormInput'
import { type AuthErrors, type AuthFormData, Auth } from '../classes/Auth'
import { useOnInit } from '../utils/hooks'
import { authService } from '../services/AuthService'
import { getMensajeError, type ErrorCustom, type ErrorResponse } from '../utils/errorHandler'
import { useUser } from '../contexts/UserContext'

const initialFormData: AuthFormData = {
  fullName: '',
  email: '',
  password: '',
  confirmPassword: ''
}

export const AuthForm = () => {
  const [formData, setFormData] = useState<AuthFormData>(initialFormData)
  const [formErrors, setFormErrors] = useState<AuthErrors>({})
  const [responseErrors, setResponseErrors] = useState<ErrorCustom | null>(null)
  const [recordarme, setRecordarme] = useState(false)
  const navigate = useNavigate()
  const { mode } = useParams()
  const { fetchUser } = useUser()

  const isValidMode = mode === 'login' || mode === 'register'
  const isLogin = mode === 'login'

  useOnInit(() => {
    if (authService.estaAutenticado()) {
      navigate('/home')
    }
    if (!isValidMode) {
      navigate('/auth/login')
    }
  })

  const content = isLogin
    ? {
        title: 'BookLibre',
        subtitle: 'Ingresá para ver tus préstamos y seguir sumando bibliokarma.',
        buttonText: 'Ingresar',
        footerText: '¿Todavía no tenés cuenta?',
        footerLinkText: 'Registrate',
        footerLinkTo: '/auth/register'
      }
    : {
        title: 'Creá tu cuenta',
        subtitle: 'Prestá los libros que ya leíste y pedí los que te faltan.',
        buttonText: 'Crear cuenta',
        footerText: '¿Ya tenés cuenta?',
        footerLinkText: 'Ingresá',
        footerLinkTo: '/auth/login'
      }

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target

    setFormData((prev) => ({
      ...prev,
      [name]: value
    }))

    setFormErrors((prev) => ({
      ...prev,
      [name]: undefined
    }))
  }

  const handleSubmit = async (event: SubmitEvent) => {
    event.preventDefault()

    const auth = new Auth(
      formData.email,
      formData.password,
      formData.confirmPassword,
      formData.fullName
    )

    const validationErrors = isLogin ? auth.validateLogin() : auth.validateRegister()

    setFormErrors(validationErrors)

    if (Auth.hasErrors(validationErrors)) return

    try {
      if (isLogin) {
        const loginData = auth.toLoginData()
        await authService.login(loginData, recordarme)
        await fetchUser()

        if(authService.puedeReservar()){
          navigate('/home')
        } else {
          navigate('/profile')
        }

        return
      }

      const registerData = auth.toRegisterData()
      await authService.register(registerData)
      window.location.reload()

      navigate('/auth/login')
    } catch (error) {
      setResponseErrors(getMensajeError(error as ErrorResponse))
    }
  }

  return (
    <section className="flex min-h-screen flex-col items-center justify-center bg-papel px-4 py-10">
      <section className="w-full max-w-md overflow-hidden rounded-2xl border border-stone-200 bg-white shadow-sm">
        <header className="flex flex-col items-center border-b border-dashed border-stone-200 px-8 pb-6 pt-8 text-center">
          <img src={logoImg} alt="" className="mb-3 h-9 w-12 object-contain" />
          <h1 className="font-display text-3xl font-semibold text-tinta-900">{content.title}</h1>
          <p className="mt-2 text-sm text-stone-600">{content.subtitle}</p>
        </header>

        <div className="px-6 py-8 sm:px-8">
          <form onSubmit={handleSubmit} className="space-y-5">
            {!isLogin && (
              <FormInput
                label="Nombre completo"
                type="text"
                name="fullName"
                placeholder="Ej. Juan Pérez"
                value={formData.fullName}
                onChange={handleChange}
                error={formErrors.fullName}
              />
            )}

            <FormInput
              label="Correo electrónico"
              type="email"
              name="email"
              placeholder="tu@ejemplo.com"
              value={formData.email}
              onChange={handleChange}
              error={formErrors.email}
            />

            <FormInput
              label="Contraseña"
              type="password"
              name="password"
              placeholder="••••••••"
              value={formData.password}
              onChange={handleChange}
              error={formErrors.password}
            />

            {!isLogin && (
              <FormInput
                label="Confirmar contraseña"
                type="password"
                name="confirmPassword"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleChange}
                error={formErrors.confirmPassword}
              />
            )}

            {isLogin && (
              <label className="flex items-center gap-2 text-sm text-stone-500">
                <input
                  type="checkbox"
                  name="rememberMe"
                  checked={recordarme}
                  onChange={(event) => setRecordarme(event.target.checked)}
                  className="h-4 w-4 rounded border-stone-300 accent-tinta-700"
                />
                Recordarme en este dispositivo
              </label>
            )}

            <button
              type="submit"
              className="w-full cursor-pointer rounded-lg bg-tinta-700 px-4 py-3 text-sm font-semibold text-white transition hover:bg-tinta-800"
              data-testid="authButton"
            >
              {content.buttonText}
            </button>
            {responseErrors && (
              <div className="text-center text-sm text-red-700">{responseErrors.message}</div>
            )}
          </form>

          <p className="mt-6 text-center text-sm text-stone-500">
            {content.footerText}{' '}
            <Link to={content.footerLinkTo} className="font-semibold text-tinta-700 hover:underline">
              {content.footerLinkText}
            </Link>
          </p>
        </div>
      </section>

      <footer className="mt-8 text-center text-xs text-stone-500">
        BookLibre · préstamo de libros entre lectores
      </footer>
    </section>
  )
}
