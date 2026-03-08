import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthForm } from './pages/AuthForm'
import BookForm from './pages/BookForm'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />
        <Route path="/" element={<Navigate to="/auth/login" />} />

        <Route path="/libros/nuevo" element={<BookForm />} />
        <Route path="/libros/editar/:id" element={<BookForm />} />

        <Route />
        <Route />
      </Routes>
    </BrowserRouter>
  )
}

export default App
