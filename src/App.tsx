import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthForm } from './pages/AuthForm'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />
        <Route path="/" element={<Navigate to="/auth/login" />} />
        <Route />
        <Route />
      </Routes>
    </BrowserRouter>
  )
}

export default App
