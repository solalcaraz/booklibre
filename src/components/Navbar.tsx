import logoImg from '../assets/icons/logo.png'
import bibliokarmaIcon from '../assets/icons/bibliokarma.png'
import defaultUser from '../assets/images/user.png'
import { useState } from 'react';
import { NavLink } from 'react-router-dom'

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
  const [bibliokarma, setBibliokarma] = useState<number>(0)

  const items: NavItemProps[] = [
    { to: '/home', label: 'Inicio' },
    { to: `/prestamos`, label: 'Mis Préstamos' },
    { to: '/profile', label: 'Perfil' }
  ]

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

            <img
              src={defaultUser}
              alt=""
              className="w-10 h-10 rounded-full object-cover"
            />
          </div>
        </div>
      </div>
    </nav>
  );
}
