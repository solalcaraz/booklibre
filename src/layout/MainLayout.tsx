import { Outlet } from 'react-router-dom'
import Navbar from '../components/Navbar'

export function MainLayout() {
  return (
    <>
      <Navbar />
      <div className="pt-15 px-20">
        <Outlet />
      </div>
    </>
  )
}
