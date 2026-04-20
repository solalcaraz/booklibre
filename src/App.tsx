import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { MainLayout } from './layout/MainLayout'
import { AuthLayout } from './layout/AuthLayout'
import { AuthForm } from './pages/AuthForm'
import { Prestamos } from './pages/Prestamos'
import { PerfilUsuario } from './pages/PerfilUsuario'
import { Home } from './pages/Home'
import LibroForm from './pages/LibroForm'
import './App.css'
import { DetalleReservaPage } from './pages/DetalleReservaPage'
import { UserProvider } from './contexts/UserContext'
import { Toaster } from 'react-hot-toast';

function App() {
  return (
    <UserProvider>
      <BrowserRouter>
        <Toaster position="top-right" reverseOrder={false} />
        
        <Routes>
          <Route path="/auth/:mode" element={<AuthForm />} />

          <Route element={<AuthLayout />}>
            <Route path="/" element={<Navigate to="/auth/login" />} />
            <Route />
            <Route element={<MainLayout />}>
              <Route path="/home" element={<Home />} />
              <Route path="/prestamos" element={<Prestamos />} />
              <Route path="/profile" element={<PerfilUsuario />} />
              <Route path="/libro/nuevo" element={<LibroForm />} />
              <Route path="/libros/editar/:id" element={<LibroForm />} />
              <Route path="/detalles/:id" element={<DetalleReservaPage />} />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </UserProvider>
  )
}

export default App
