import logoImg from '../assets/icons/logo.png'
import bibliokarmaIcon from '../assets/icons/bibliokarma.png'
import defaultUser from '../assets/images/user.png'
import { useState } from 'react';

export default function Navbar() {
  const [bibliokarma, setBibliokarma] = useState<number>(0)
  
  return (
    <nav className="w-full border-b bg-white">
      <div className="max-w-7xl mx-auto px-6">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-blue-500 rounded-full">
                <img src={logoImg} alt="BookLibre" />
            </div>
          </div>

          <div className="flex gap-10 text-gray-600 font-medium">
            <a className="hover:text-blue-500 cursor-pointer">
              Inicio
            </a>
            <a className="text-blue-500">
              Mis Préstamos
            </a>
            <a className="hover:text-blue-500 cursor-pointer">
              Perfil
            </a>
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
