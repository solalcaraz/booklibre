import React, { useEffect, useState } from 'react'
import type { ReservaRequestModel, ReservaResponseModel } from '../../models/ReservaModel'
import { reservaService } from '../../services/ReservaService'
import DataGrid from '../DataGrid'
import { ReservasCard } from './ReservaCard'

interface Props {
  tab: string
}

export const ReservasTab = ({ tab }: Props) => {
  const [input, setInput] = useState<string>('')
  const [filtro, setFiltro] = useState<ReservaRequestModel>({
    inputSearch: '',
    busquedaPor: tab,
    usuarioId: '1' //Todo: consumir usuario logeado
  })

  const filtrar = (e?: any) => {
    e?.preventDefault()
    setFiltro(prev => ({
      ...prev,
      inputSearch: input,
    }))
  }

  useEffect(() => {
    setFiltro(prev => ({
      ...prev,
      busquedaPor: tab
    }))
  }, [tab])
  
  const changeInput = (e: React.ChangeEvent<HTMLInputElement, HTMLInputElement>) => {
    const value = e.target.value
    setInput(value)
    
    //Todo: cuadno se borra el filtro con la x resetear pero no funca
    // if (value.length === 0) {
    //   filtrar()
    // }
  }

  return (
    <>
      <form className="mt-6" onSubmit={filtrar}>
        <div className="relative">
          <input
            type="search"
            value={input}
            onChange={(e) => changeInput(e)}
            placeholder="Buscar por título, autor..."
            className="w-full border border-gray-300 rounded-md px-4 py-4 pr-10 focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>
      </form>

      <DataGrid<ReservaResponseModel, ReservaRequestModel>
        fetchFunction={reservaService.buscarReservas}
        filter={filtro}
        renderItem={(reserva) => ( <ReservasCard reserva={reserva} tab={tab} reload={filtrar} />)}
        pageSize={4}
      />
    </>
  )
}
