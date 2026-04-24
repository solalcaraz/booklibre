import { Outlet, useNavigate } from 'react-router-dom'
import { useOnInit } from '../utils/hooks'
import { authService } from '../services/AuthService'

export const AuthLayout = () => {
  const navigate = useNavigate()

  useOnInit(() => {
    if (!authService.estaAutenticado()) {
      navigate('/auth/login')
    }
  })

  return <Outlet />
}
