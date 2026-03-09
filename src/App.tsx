import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthForm } from './pages/AuthForm'
import { DetalleLibroPage } from './pages/DetalleLibroPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />
        <Route path="/" element={<Navigate to="/auth/login" />} />
        <Route path='/libros/detalle/:id' element={<DetalleLibroPage/>} />
        <Route />
      </Routes>
    </BrowserRouter>
  )
}

export default App
