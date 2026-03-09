import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthForm } from './pages/AuthForm'
import { Prestamos } from './pages/Prestamos'
import { MainLayout } from './layout/MainLayout'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />
        <Route path="/" element={<Navigate to="/auth/login" />} />
        <Route />
        <Route />

        <Route element={<MainLayout />}>
          <Route path="/prestamos" element={<Prestamos />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
