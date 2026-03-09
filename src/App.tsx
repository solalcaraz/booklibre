import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthForm } from './pages/AuthForm'
import { Home } from './pages/Home'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />
        <Route path="/" element={<Navigate to="/auth/login" />} />
        <Route path="/home" element={<Home />}/>
        
      </Routes>
    </BrowserRouter>
  )
}

export default App
