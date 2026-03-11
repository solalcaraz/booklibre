import { useState } from 'react'
import type { ReservaRequestModel, ReservaResponseModel } from '../../models/ReservaModel'
import { reservaService } from '../../services/ReservaService'
import DataGrid from '../DataGrid'
import { TAB_PRESTADOS_A_MI } from '../../pages/Prestamos'
import { ReservasCard } from './ReservaCard'

interface Props {
  tab: string
}

export const ReservasTab = ({ tab }: Props) => {
  const [input, setInput] = useState<string>('')
  const fetchFunciton = tab === TAB_PRESTADOS_A_MI ? reservaService.misReservas : reservaService.prestados

  return (
    <>
      <form className="mt-6">
        <div className="relative">

          <input
            type="search"
            placeholder="Buscar por título, autor..."
            className="w-full border border-gray-300 rounded-md px-4 py-4 pr-10 focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>
      </form>

      <DataGrid<ReservaResponseModel, ReservaRequestModel>
        fetchFunction={fetchFunciton}
        // filter={{inputSearch: input}}
        renderItem={(reserva) => ( <ReservasCard reserva={reserva} />)}
      />
    </>
  )
}
