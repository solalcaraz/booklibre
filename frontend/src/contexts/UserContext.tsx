import React, { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import { authService } from '../services/AuthService';
import { usuarioService } from '../services/UsuarioService';

export type navbarUser = {
  bibliokarmas: number;
  img: string;
}

interface UserContextType {
  user: navbarUser | null;
  updateUser: (updates: Partial<navbarUser>) => void;
  logout: () => void;
  fetchUser: () => Promise<void>
}

const UserContext = createContext<UserContextType | undefined>(undefined)

export const useUser = () => {
  const context = useContext(UserContext)
  if (!context) {
    throw new Error('Algo salio mal al cargar el usuario')
  }
  return context
}

interface UserProviderProps {
  children: ReactNode;
}

export const UserProvider: React.FC<UserProviderProps> = ({ children }) => {
  const [user, setUser] = useState<navbarUser | null>(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })

  const fetchUser = useCallback(async () => {
    try {
      if (!authService.estaAutenticado()) {
        setUser(null)
        localStorage.removeItem('user')
        return
      }
      
      const user = await usuarioService.getUsuario()
      
      const mappedUser = {
        img: user.imagen,
        bibliokarmas: user.bibliokarmas
      }
      
      setUser(mappedUser)
      localStorage.setItem('user', JSON.stringify(mappedUser))
    } catch (error) {
      console.error("Error cargando usuario:", error)
    }
  }, [])
  
  useEffect(() => {
    fetchUser()
  }, [fetchUser])

  const updateUser = (usuario: Partial<navbarUser>) => {
    setUser((prev) => {
      const actualizado = prev ? { ...prev, ...usuario } : ({ ...usuario } as navbarUser)
      window.localStorage.setItem('user', JSON.stringify(actualizado))
      return actualizado
    })
  }

  const logout = () => {
    authService.cerrarSesion()
    setUser(null)
  }

  return (
    <UserContext.Provider value={{ user, updateUser, logout, fetchUser }}>
      {children}
    </UserContext.Provider>
  )
}
