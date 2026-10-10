import React, { useState } from 'react'
import type { ReservaRequestModel, ReservaResponseModel } from '../../models/ReservaModel'
import DataGrid, { type FetchFunction } from '../DataGrid'
import { ReservasCard } from './ReservaCard'
import { authService } from '../../services/AuthService'
import { Spinner } from '../Spinner'

interface Props {
  tab: string
  fetchFunction: FetchFunction<ReservaResponseModel, ReservaRequestModel>
}

export const ReservasTab = ({ tab, fetchFunction }: Props) => {
  const user = authService.userDeStorage()
  const [input, setInput] = useState<string>('')
  const [loading, setLoading] = useState(false);
  const [filtro, setFiltro] = useState<ReservaRequestModel>({
    inputSearch: '',
    usuarioId: user?.sub ?? ''
  })

  const filtrar = (e?: any) => {
    e?.preventDefault()
    setFiltro(prev => ({
      ...prev,
      inputSearch: input,
    }))
  }

  const reload = () => {
    setLoading(true)
    filtrar()
    setLoading(false)
  }
  
  const changeInput = (e: React.ChangeEvent<HTMLInputElement, HTMLInputElement>) => {
    const value = e.target.value
    setInput(value)
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

      {loading ? (
        <Spinner />
      ) : (  
        <DataGrid<ReservaResponseModel, ReservaRequestModel>
          fetchFunction={fetchFunction}
          filter={filtro}
          renderItem={(reserva) => ( <ReservasCard reserva={reserva} tab={tab} reload={reload} />)}
          pageSize={4}
        />
      )}
    </>
  )
}
