import { Outlet, useNavigate } from 'react-router-dom'
import { useOnInit } from '../utils/hooks'
import { usuarioService } from '../services/UsuarioService'

export const AuthLayout = () => {
  const checkSession = async () => {
    try {
      await usuarioService.getUsuario()
    } catch (e) {
      console.log(e)
      navigate('/auth/login')
    }
  }

  const navigate = useNavigate()

  useOnInit(() => {
    checkSession()
  })

  return <Outlet />
}
