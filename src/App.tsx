import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthForm } from './pages/AuthForm'
import { AuthLayout } from './layout/AuthLayout'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth/:mode" element={<AuthForm />} />
        <Route element={<AuthLayout />}>
          <Route path="/" element={<Navigate to="/auth/login" />} />
          <Route path='/home' element={<div>hola</div>}/>
          <Route />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
