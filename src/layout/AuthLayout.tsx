import { Outlet, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { authService } from '../services/AuthService'
import { useUser } from '../contexts/UserContext'

export const AuthLayout = () => {
  const navigate = useNavigate()
  const { fetchUser } = useUser()
  const [validandoSesion, setValidandoSesion] = useState(true)

  useEffect(() => {
    const validarSesion = async () => {
      if (authService.estaAutenticado()) {
        await fetchUser()
        setValidandoSesion(false)
        return
      }

      if (authService.tieneRefreshToken()) {
        const accessToken = await authService.renovarAccessToken()

        if (accessToken) {
          await fetchUser()
          setValidandoSesion(false)
          return
        }
      }

      navigate('/auth/login', { replace: true })
      setValidandoSesion(false)
    }

    validarSesion()
  }, [fetchUser, navigate])

  if (validandoSesion) {
    return null
  }

  return <Outlet />
}
