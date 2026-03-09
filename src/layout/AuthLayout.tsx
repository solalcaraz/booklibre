import { Outlet, useNavigate } from 'react-router'
import { authService } from '../services/AuthService'
import { useOnInit } from '../utils/hooks'

export const AuthLayout = () => {
  const navigate = useNavigate()

  useOnInit(() => {
    if (!authService.estaAutenticado()) {
      navigate('/auth/login')
    }
  })
  
  return <Outlet />
}
