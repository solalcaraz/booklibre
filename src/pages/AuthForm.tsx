import type { ChangeEvent, SubmitEvent } from 'react'
import { useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { HandWavingIcon, UserPlusIcon } from '@phosphor-icons/react'
import { FormInput } from '../components/FormInput'
import { type AuthErrors, type AuthFormData, Auth } from '../classes/Auth'
import { useOnInit } from '../utils/hooks'
import { authService } from '../services/AuthService'
import { getMensajeError, type ErrorCustom, type ErrorResponse } from '../utils/errorHandler'

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
  const navigate = useNavigate()
  const { mode } = useParams()

  const isValidMode = mode === 'login' || mode === 'register'
  const isLogin = mode === 'login'

  useOnInit(() => {
    /* Si estoy logeado me manda al home */
    if (authService.estaAutenticado()) {
      navigate('/home')
    }
    /* Si quiero entrar a una subruta invalida de auth me redirecciona al login */
    if (!isValidMode) {
      navigate('/auth/login')
    }
  })

  const content = isLogin
    ? {
        title: 'BookLibre',
        subtitle: 'Por favor ingresa tus datos para continuar leyendo.',
        buttonText: 'Ingresar',
        footerText: '¿Aún no tienes una cuenta?',
        footerLinkText: 'Regístrate gratis',
        footerLinkTo: '/auth/register',
        icon: <HandWavingIcon color="#61b6ff" />
      }
    : {
        title: 'Crear una cuenta',
        subtitle: 'Únete a nuestra comunidad y empieza a leer.',
        buttonText: 'Crear cuenta',
        footerText: '¿Ya tienes una cuenta?',
        footerLinkText: 'Inicia sesión',
        footerLinkTo: '/auth/login',
        icon: <UserPlusIcon color="#61b6ff" />
      }

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target

    setFormData((prev) => ({
      ...prev,
      [name]: value
    }))

    /* Limpio errores onChange y despues valido onSubmit */
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
        const user = await authService.login(loginData)

        if (user.id) {
          window.localStorage.setItem('user', JSON.stringify(user))
        }

        navigate('/home')
        return
      }

      const registerData = auth.toRegisterData()
      const user = await authService.register(registerData)

      if (user.id) {
        window.localStorage.setItem('user', JSON.stringify(user))
      }

      navigate('/home')
    } catch (error) {
      setResponseErrors(getMensajeError(error as ErrorResponse))
      console.log(error)
    }
  }

  return (
    <section className="flex min-h-screen flex-col items-center justify-center bg-[#f8f7f4] px-4 py-10">
      <section className="w-full max-w-md overflow-hidden rounded-2xl bg-white shadow-[0_10px_30px_rgba(15,23,42,0.08)]">
        <header className="flex flex-col items-center bg-[#f3f9ff] px-8 py-8 text-center">
          <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-white text-2xl shadow-sm">
            <span>{content.icon}</span>
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-800">{content.title}</h1>
          <p className="mt-2 text-sm text-slate-500">{content.subtitle}</p>
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
              <label className="flex items-center gap-2 text-sm text-slate-500">
                <input
                  type="checkbox"
                  name="rememberMe"
                  /* checked={formData.rememberMe} */
                  onChange={handleChange}
                  className="h-4 w-4 rounded border-slate-300 text-emerald-400 focus:ring-emerald-300"
                />
                Recordarme
              </label>
            )}

            <button
              type="submit"
              className="w-full rounded-lg bg-[#7bedac] px-4 py-3 text-sm font-bold text-slate-800 transition hover:brightness-95 cursor-pointer"
              data-testid="authButton"
            >
              {content.buttonText}
            </button>
            {responseErrors && (
              <div className="text-red-500 text-center">{responseErrors.message}</div>
            )}
          </form>

          <p className="mt-6 text-center text-sm text-slate-500">
            {content.footerText}{' '}
            <Link to={content.footerLinkTo} className="font-semibold text-sky-500 hover:underline">
              {content.footerLinkText}
            </Link>
          </p>
        </div>
      </section>

      <footer className="mt-10 text-center text-xs text-slate-400">
        © 2026 PastelReads. Todos los derechos reservados.
      </footer>
    </section>
  )
}
