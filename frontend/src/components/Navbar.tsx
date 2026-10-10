import logoImg from '../assets/icons/logo.png'
import bibliokarmaIcon from '../assets/icons/bibliokarma.png'
import logoutIcon from '../assets/icons/logout-icon.svg'
import defaultUser from '../assets/images/user.png'
import { useUser } from '../contexts/UserContext'
import { NavLink, useNavigate } from 'react-router-dom'
import { useEffect, useRef, useState } from 'react'
import { authService } from '../services/AuthService'

interface NavItemProps {
  to: string
  label: string
}
export const NavItem = ({ to, label }: NavItemProps) => {
  return (
    <li className="list-none">
      <NavLink to={to} className="flex h-16 items-center">
        {({ isActive }) => (
          <span
            className={`border-b-2 py-5 text-sm transition ${
              isActive
                ? 'border-tinta-700 font-semibold text-tinta-800'
                : 'border-transparent text-stone-600 hover:text-stone-900'
            }`}
          >
            {label}
          </span>
        )}
      </NavLink>
    </li>
  )
}

export default function Navbar() {
  const { user, logout } = useUser()
  const [showDropdwn, setShowDropdwn] = useState(false)
  const bibliokarma = user?.bibliokarmas || 0
  const navigate = useNavigate()

  const items: NavItemProps[] = [
    ...(authService.puedeReservar() ? [{ to: '/home', label: 'Inicio' }] : []),
    { to: `/prestamos`, label: 'Mis préstamos' },
    { to: '/profile', label: 'Perfil' },
    ...(authService.puedePublicar() ? [{ to: '/dashboard', label: 'Tablero' }] : [])
  ]

  const handleLogout = () => {
    logout()
    navigate('/auth/login')
  }

  const ref = useRef<HTMLDivElement>(null)
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setShowDropdwn(false)
      }
    }

    document.addEventListener("mousedown", handleClickOutside)
    return () => document.removeEventListener("mousedown", handleClickOutside)
  }, [])

  return (
    <nav className="w-full border-b border-stone-200 bg-white">
      <div className="mx-auto px-6">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-10">
            <div className="flex items-center gap-2">
              <img src={logoImg} alt="" className="h-7 w-9 object-contain" />
              <span className="font-display text-2xl font-semibold text-tinta-900">BookLibre</span>
            </div>

            <div className="flex gap-7">
              {items.map(item => (
                <NavItem
                  key={item.to}
                  to={item.to}
                  label={item.label}
                />
              ))}
            </div>
          </div>

          <div className="flex items-center gap-6">
            {authService.puedeReservar() && (
              <div
                className="flex items-center gap-2 rounded-full border border-lacre-100 bg-lacre-50 py-1 pl-1.5 pr-3 text-sm text-lacre-700"
                title="Los bibliokarmas se suman con cada reserva"
              >
                <img src={bibliokarmaIcon} alt="" className="h-5 w-5 rounded-full" />
                <span className="cifra font-semibold">{bibliokarma}</span>
                <span>bibliokarma</span>
              </div>
            )}

            <div className="relative" ref={ref}>
              <img
                src={user?.img || defaultUser}
                alt=""
                onClick={() => setShowDropdwn(!showDropdwn)}
                className="h-9 w-9 cursor-pointer rounded-full object-cover ring-2 ring-stone-100"
              />

              {showDropdwn && (
                <div className="absolute right-0 z-20 mt-2 w-44 cursor-pointer overflow-hidden rounded-lg border border-stone-200 bg-white shadow-md">
                  <button
                    onClick={handleLogout}
                    className="flex w-full items-center justify-between px-4 py-2 text-left text-sm text-stone-700 hover:bg-stone-50"
                  >
                    Cerrar sesión
                  <img src={logoutIcon} alt="" className="w-6 h-6" />
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
}
