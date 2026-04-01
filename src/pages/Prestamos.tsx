import { useState } from "react"
import { Tabs, type Tab } from "../components/Tabs"
import { ReservasTab } from "../components/Prestamos/ReservasTab"
import { TAB_PRESTADOS_A_MI, TAB_PRESTADOS_POR_MI } from "../constants/ReservasConstants"
import { reservaService } from "../services/ReservaService"

export const Prestamos = () => {
  const [activeTab, setActiveTab] = useState(TAB_PRESTADOS_A_MI)

  const prestamosTabs: Tab[] = [
    { id: TAB_PRESTADOS_A_MI, label: 'Prestados a mí' },
    { id: TAB_PRESTADOS_POR_MI, label: 'Prestados por mí' }
  ]

  const handleTabChange = (tab: string) => {
    setActiveTab(tab)
  }

  return (
    <div className="px-20">
      <div>
        <h1 className="text-3xl font-bold text-gray-800">Prestamos de libros</h1>
        <p className="text-gray-500 mt-2">Aqui vas a encontrar toda la informacion sobre el intercambio de libros con otros usuarios.</p>
      </div>

      <Tabs tabs={prestamosTabs} active={activeTab} onChange={handleTabChange} />

      {activeTab === TAB_PRESTADOS_A_MI && (
        <div className="flex flex-col gap-6 px-4 my-7">
          <ReservasTab tab={activeTab} fetchFunction={reservaService.buscarPrestadosAmi}/>
        </div>
      )}
      {activeTab === TAB_PRESTADOS_POR_MI && (
        <div className="flex flex-col gap-6 px-4 my-7">
          <ReservasTab tab={activeTab} fetchFunction={reservaService.buscarPrestadosPorMi}/>
        </div>
      )}
    </div>
  )
}
