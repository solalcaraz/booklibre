import { Outlet } from 'react-router'
import Navbar from '../components/Navbar'

export function MainLayout() {
  return (
    <>
      <Navbar />
      <div className="pt-10">
        <Outlet />
      </div>
    </>
  )
}
