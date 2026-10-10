import { useState } from 'react'
import { ESTADO_RESERVA_A_VENCER, ESTADO_RESERVA_ACTIVO, ESTADO_RESERVA_DEVUELTO, TAB_PRESTADOS_A_MI } from '../../constants/ReservasConstants'
import type { ReservaResponseModel } from '../../models/ReservaModel'
import { CalificarPopup } from './CalificarPopup'

interface Props {
  reserva: ReservaResponseModel
  tab: string
  reload: () => void
}

export const ReservasCard = ({ reserva, tab, reload }: Props) => {
  const [openPopup, setOpenPopup] = useState(false)

  const getEstadoColor = (estado: string): string => {
    switch(estado) {
      case ESTADO_RESERVA_ACTIVO:
        return 'bg-blue-500'
      case ESTADO_RESERVA_A_VENCER:
        return 'bg-orange-500'
      case ESTADO_RESERVA_DEVUELTO:
        return 'bg-red-500'
      default:
        return ''
    }
  }

  const closePopup = (resetear: boolean) => {
    setOpenPopup(false)
    if (resetear){
      reload()
    }
  }
  
  return (
    <div className="bg-white rounded-2xl shadow-sm overflow-hidden w-full max-w-xs min-h-[600px]">
      <div className="relative from-teal-900 to-teal-700 p-6 flex justify-center">
        <img
          src={reserva.libroResumen.imgUrl}
          alt={reserva.libroResumen.titulo}
          className="h-56 object-contain drop-shadow-xl"
        />

        <span className={`absolute top-3 right-3 text-white text-xs font-semibold px-3 py-1 rounded-full ${getEstadoColor(reserva.estado)}`}>
          {reserva.estado}
        </span>
      </div>

      <div className="p-5">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="font-bold text-xl text-gray-900 leading-tight">
              {reserva.libroResumen.titulo}
            </h3>
            <p className="text-gray-500 text-sm">{reserva.libroResumen.autor}</p>
          </div>
          <div className="flex items-center gap-1 text-sm text-gray-700">
            ⭐
            <span>{reserva.libroResumen.puntuacion}</span>
          </div>
        </div>

        <div className="border-t my-4"></div>

        <div className="space-y-2 text-sm text-gray-600">
          <div className="flex items-center gap-2">
            <span>👤</span>
            <span>
              <strong>Prestado por:</strong> {reserva.prestadoPor}
            </span>
          </div>

          <div className="flex items-center gap-2">
            <span>📅</span>
            <span>
              <strong>Rango:</strong> {reserva.fechaDesde} - {reserva.fechaHasta}
            </span>
          </div>
        </div>

        <div className="mt-4 text-xs mb-5 text-gray-400 font-semibold">
          BIBLOKARMA
          <div className="text-gray-600 text-sm">+{reserva.bibliokarma}</div>
        </div>

        {reserva.puedeCalificar && tab === TAB_PRESTADOS_A_MI && 
         ( <button className="w-full flex items-center justify-center gap-2 bg-gray-200 hover:bg-gray-300
              text-blue-600 font-semibold px-4 py-3 mt-3 rounded-xl transition-colors duration-200" onClick={() => setOpenPopup(true)}>
            <span className="text-lg">☆</span>
            <span>Calificar</span>
          </button>)
        }

        {tab === TAB_PRESTADOS_A_MI && reserva.reseniaPuntuacion != null && (
          <div className="flex justify-center gap-2 mb-4">
            {[1,2,3,4,5].map((star) => (
              <span
                key={star}
                className={`text-3xl transition ${
                  star <= reserva.reseniaPuntuacion!
                    ? "text-yellow-400"
                    : "text-gray-300"
                }`}
              >
                ★
              </span>
            ))}
          </div>
        )}
      </div>

      <CalificarPopup open={openPopup} onClose={(reload) => closePopup(reload)} reserva={reserva} />
    </div>
  )
}
