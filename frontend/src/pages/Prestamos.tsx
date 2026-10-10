import { useState } from "react"
import { Tabs, type Tab } from "../components/Tabs"
import { ReservasTab } from "../components/Prestamos/ReservasTab"
import { TAB_PRESTADOS_A_MI, TAB_PRESTADOS_POR_MI } from "../constants/ReservasConstants"
import { reservaService } from "../services/ReservaService"
import { authService } from "../services/AuthService"

export const Prestamos = () => {
  const puedeVerPrestadosAMi = authService.puedeReservar()
  const puedeVerPrestadosPorMi = authService.puedePublicar()
  
  const prestamosTabs: Tab[] = [
    puedeVerPrestadosAMi && { id: TAB_PRESTADOS_A_MI, label: 'Libros que me prestaron' },
    puedeVerPrestadosPorMi && { id: TAB_PRESTADOS_POR_MI, label: 'Libros que presté' }
  ].filter(Boolean) as Tab[]

  const [activeTab, setActiveTab] = useState(prestamosTabs[0].id)
  
  
  const handleTabChange = (tab: string) => {
    setActiveTab(tab)
  }

  return (
    <div className="mx-auto max-w-6xl px-8 pb-10">
      <div>
        <h1 className="font-display text-3xl font-semibold text-tinta-900">Mis préstamos</h1>
        <p className="mt-2 text-stone-600">
          Los libros que reservaste y los tuyos que reservaron otros. Cuando devolvés un libro, lo podés calificar.
        </p>
      </div>

      <Tabs tabs={prestamosTabs} active={activeTab} onChange={handleTabChange} />

      {activeTab === TAB_PRESTADOS_A_MI && puedeVerPrestadosAMi && (
        <div className="my-7 flex flex-col gap-6">
          <ReservasTab tab={activeTab} fetchFunction={reservaService.buscarPrestadosAmi}/>
        </div>
      )}
      {activeTab === TAB_PRESTADOS_POR_MI && puedeVerPrestadosPorMi && (
        <div className="my-7 flex flex-col gap-6">
          <ReservasTab tab={activeTab} fetchFunction={reservaService.buscarPrestadosPorMi}/>
        </div>
      )}
    </div>
  )
}
