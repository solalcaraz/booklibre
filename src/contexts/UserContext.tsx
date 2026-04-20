import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authService, type AuthResponse } from '../services/AuthService';
import { usuarioService } from '../services/UsuarioService';

interface UserContextType {
  user: AuthResponse | null;
  updateUser: (updates: Partial<AuthResponse>) => void;
  logout: () => void;
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
  const [user, setUser] = useState<AuthResponse | null>(null)

  // Cargar el usuario desde el localStorage al montar el componente
  useEffect(() => {
    let isMounted = true;

    const fetchData = async () => {
      try {
        if (!authService.estaAutenticado()) return

        const storedUser = authService.userDeStorage()
        const bibliokarmas = await usuarioService.getBibliokarmas()

        if (isMounted) {
          setUser({ ...storedUser, bibliokarmas })
        }
      } catch (error) {
        console.error("Error cargando usuario en el navbar:", error)
      }
    };

    fetchData()

    return () => {
      isMounted = false
    }
    }, [])

  const updateUser = (usuario: Partial<AuthResponse>) => {
    setUser((prev) => {
      const actualizado = prev ? { ...prev, ...usuario } : ({ ...usuario } as AuthResponse)
      window.localStorage.setItem('user', JSON.stringify(actualizado))
      return actualizado
    })
  }

  const logout = () => {
    authService.cerrarSesion()
    setUser(null)
  }

  return (
    <UserContext.Provider value={{ user, updateUser, logout }}>
      {children}
    </UserContext.Provider>
  )
}
