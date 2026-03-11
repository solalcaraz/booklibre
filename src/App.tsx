import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthForm } from './pages/AuthForm'
import { Prestamos } from './pages/Prestamos'
import { MainLayout } from './layout/MainLayout'
import { AuthLayout } from './layout/AuthLayout'
import { Home } from './pages/Home'
import LibroForm from './pages/LibroForm'
import { PerfilUsuario } from './pages/PerfilUsuario'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />

        <Route element={<AuthLayout />}>
          <Route path="/" element={<Navigate to="/auth/login" />} />
          <Route />
          <Route element={<MainLayout />}>
            <Route path="/home" element={<Home />}/>
            <Route path="/prestamos" element={<Prestamos />} />
            <Route path="/profile" element={<PerfilUsuario />} />
            <Route path="/libro/nuevo" element={<LibroForm />} />
            <Route path="/libros/editar/:id" element={<LibroForm />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
