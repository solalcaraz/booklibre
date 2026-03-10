import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthForm } from './pages/AuthForm'
import { AuthLayout } from './layout/AuthLayout'
import { Home } from './pages/Home'
import { PerfilUsuario } from './pages/PerfilUsuario'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />
        <Route element={<AuthLayout />}>
          <Route path="/" element={<Navigate to="/auth/login" />} />
          <Route path="/home" element={<Home />} />
          <Route path="/profile" element={<PerfilUsuario />} />
          <Route />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
