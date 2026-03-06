import type { ChangeEvent, FormEvent } from 'react'
import { useMemo, useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router'

type AuthMode = 'login' | 'register'

type AuthFormData = {
  fullName: string
  email: string
  password: string
  confirmPassword: string
  rememberMe: boolean
}

const initialFormData: AuthFormData = {
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
  rememberMe: false
}

export const AuthForm = () => {
  const { mode } = useParams<{ mode: string }>()
  const [formData, setFormData] = useState<AuthFormData>(initialFormData)
  const navigate = useNavigate()

  const isValidMode = mode === 'login' || mode === 'register'

  if (!isValidMode) {
    navigate("/auth/login")
  }

  const authMode = mode as AuthMode
  const isLogin = authMode === 'login'

  const content = useMemo(() => {
    if (isLogin) {
      return {
        title: 'BookLibre',
        subtitle: 'Por favor ingresa tus datos para continuar leyendo.',
        buttonText: 'Ingresar',
        footerText: '¿Aún no tienes una cuenta?',
        footerLinkText: 'Regístrate gratis',
        footerLinkTo: '/auth/register',
        icon: '📘'
      }
    }

    return {
      title: 'Crear una cuenta',
      subtitle: 'Únete a nuestra comunidad y empieza a leer.',
      buttonText: 'Crear cuenta',
      footerText: '¿Ya tienes una cuenta?',
      footerLinkText: 'Inicia sesión',
      footerLinkTo: '/auth/login',
      icon: '👤'
    }
  }, [isLogin])

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = event.target

    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }))
  }

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    if (isLogin) {
      console.log('Login data:', {
        email: formData.email,
        password: formData.password,
        rememberMe: formData.rememberMe
      })
      return
    }

    console.log('Register data:', {
      fullName: formData.fullName,
      email: formData.email,
      password: formData.password,
      confirmPassword: formData.confirmPassword
    })
  }

  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-[#f8f7f4] px-4 py-10">
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
              <FormField
                label="Nombre completo"
                type="text"
                name="fullName"
                placeholder="Ej. Juan Pérez"
                value={formData.fullName}
                onChange={handleInputChange}
              />
            )}

            <FormField
              label="Correo electrónico"
              type="email"
              name="email"
              placeholder="tu@ejemplo.com"
              value={formData.email}
              onChange={handleInputChange}
            />

            <FormField
              label="Contraseña"
              type="password"
              name="password"
              placeholder="••••••••"
              value={formData.password}
              onChange={handleInputChange}
            />

            {!isLogin && (
              <FormField
                label="Confirmar contraseña"
                type="password"
                name="confirmPassword"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleInputChange}
              />
            )}

            {isLogin && (
              <label className="flex items-center gap-2 text-sm text-slate-500">
                <input
                  type="checkbox"
                  name="rememberMe"
                  checked={formData.rememberMe}
                  onChange={handleInputChange}
                  className="h-4 w-4 rounded border-slate-300 text-emerald-400 focus:ring-emerald-300"
                />
                Recordarme
              </label>
            )}

            <button
              type="submit"
              className="w-full rounded-lg bg-[#7bedac] px-4 py-3 text-sm font-bold text-slate-800 transition hover:brightness-95"
            >
              {content.buttonText}
            </button>
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
    </main>
  )
}

type FormFieldProps = {
  label: string
  type: string
  name: keyof AuthFormData
  placeholder: string
  value: string
  onChange: (event: ChangeEvent<HTMLInputElement>) => void
}

function FormField({ label, type, name, placeholder, value, onChange }: FormFieldProps) {
  return (
    <div>
      <label htmlFor={name} className="mb-2 block text-sm font-semibold text-slate-600">
        {label}
      </label>

      <input
        id={name}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className="w-full rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition placeholder:text-slate-400 focus:border-sky-300 focus:bg-white"
      />
    </div>
  )
}
