import logoImg from '../assets/icons/logo.png'
import bibliokarmaIcon from '../assets/icons/bibliokarma.png'
import logoutIcon from '../assets/icons/logout-icon.svg'
import defaultUser from '../assets/images/user.png'
import { useUser } from '../contexts/UserContext'
import { NavLink, useNavigate } from 'react-router-dom'
import { useEffect, useRef, useState } from 'react'

interface NavItemProps {
  to: string
  label: string
}
export const NavItem = ({ to, label }: NavItemProps) => {
  return (
    <li className="list-none">
      <NavLink to={to} className="flex flex-col items-center justify-center mr-6">
        {({ isActive }) => (
          <span className={isActive ? "text-blue-500" : "text-gray-600"}>
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
    { to: '/home', label: 'Inicio' },
    { to: `/prestamos`, label: 'Mis Préstamos' },
    { to: '/profile', label: 'Perfil' }
  ]

  const handleLogout = () => {
    logout()
    navigate('/auth/login')
  }

  // cerrar dropdown de logout cuando hace click fuera 
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
    <nav className="w-full bg-white">
      <div className="mx-auto px-6">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-2">
            <div className="flex items-center mr-6">
              <img src={logoImg} alt="" style={{ height: "2rem", width: "3rem" }} />
              <span className="text-3xl font-extrabold tracking-tight text-slate-800">BookLibre</span>
            </div>

            <div className="flex text-gray-600 font-medium">
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
            <div className="flex items-center gap-2 text-gray-600">
              <span className="text-xl">
                <img src={bibliokarmaIcon} alt="" className="w-6 h-6" />
              </span>
              <span className="font-medium">
                Bibliokarma {bibliokarma}
              </span>
            </div>

            <div className="relative" ref={ref}>
              <img
                src={user?.img || defaultUser}
                alt=""
                onClick={() => setShowDropdwn(!showDropdwn)}
                className="w-10 h-10 rounded-full object-cover"
              />

              {showDropdwn && (
                <div className="absolute right-0 mt-2 w-40 bg-white overflow-hidden cursor-pointer rounded-lg shadow-md">
                  <button
                    onClick={handleLogout}
                    className="w-full text-left flex justify-between px-4 py-2 hover:bg-gray-100"
                  >
                    Cerrar sesion
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
