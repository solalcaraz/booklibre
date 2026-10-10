import { useState } from 'react'
import { ESTADO_RESERVA_A_VENCER, ESTADO_RESERVA_ACTIVO, ESTADO_RESERVA_DEVUELTO, TAB_PRESTADOS_A_MI } from '../../constants/ReservasConstants'
import type { ReservaResponseModel } from '../../models/ReservaModel'
import { formatDate } from '../../utils/formateDate'
import { CalificarPopup } from './CalificarPopup'

interface Props {
  reserva: ReservaResponseModel
  tab: string
  reload: () => void
}

// Devuelto no es un error: va en gris. Lacre solo para lo que pide atención (devolverlo pronto)
const estadoReserva: Record<string, { texto: string; clase: string }> = {
  [ESTADO_RESERVA_ACTIVO]: { texto: 'Activo', clase: 'bg-tinta-700 text-white' },
  [ESTADO_RESERVA_A_VENCER]: { texto: 'Próximo a vencer', clase: 'bg-lacre-600 text-white' },
  [ESTADO_RESERVA_DEVUELTO]: { texto: 'Devuelto', clase: 'bg-stone-200 text-stone-700' }
}

export const ReservasCard = ({ reserva, tab, reload }: Props) => {
  const [openPopup, setOpenPopup] = useState(false)
  const esPrestadoAMi = tab === TAB_PRESTADOS_A_MI
  const estado = estadoReserva[reserva.estado] ?? { texto: reserva.estado, clase: 'bg-stone-200 text-stone-700' }

  const closePopup = (resetear: boolean) => {
    setOpenPopup(false)
    if (resetear) {
      reload()
    }
  }

  return (
    <div className="flex h-full w-full flex-col overflow-hidden rounded-2xl border border-stone-200 bg-white">
      <div className="relative flex justify-center bg-stone-100 p-6">
        <img
          src={reserva.libroResumen.imgUrl}
          alt={`Portada de ${reserva.libroResumen.titulo}`}
          className="h-48 rounded-sm object-contain shadow-md shadow-stone-400/40"
        />
        <span className={`absolute right-3 top-3 rounded-full px-2.5 py-1 text-xs font-semibold ${estado.clase}`}>
          {estado.texto}
        </span>
      </div>

      <div className="flex flex-1 flex-col p-5">
        <h3 className="font-display text-lg font-semibold leading-snug text-stone-900">
          {reserva.libroResumen.titulo}
        </h3>
        <p className="text-sm text-stone-600">{reserva.libroResumen.autor}</p>

        <dl className="mt-4 space-y-2 border-t border-dashed border-stone-200 pt-3 text-sm">
          {esPrestadoAMi && (
            <div className="flex justify-between gap-2">
              <dt className="text-stone-500">Te lo presta</dt>
              <dd className="text-right font-medium text-stone-800">{reserva.prestadoPor}</dd>
            </div>
          )}
          <div className="flex justify-between gap-2">
            <dt className="text-stone-500">Del</dt>
            <dd className="cifra text-right text-stone-800">{formatDate(reserva.fechaDesde)}</dd>
          </div>
          <div className="flex justify-between gap-2">
            <dt className="text-stone-500">Al</dt>
            <dd className="cifra text-right text-stone-800">{formatDate(reserva.fechaHasta)}</dd>
          </div>
          {esPrestadoAMi && (
            <div className="flex justify-between gap-2">
              <dt className="text-stone-500">Bibliokarma ganado</dt>
              <dd className="cifra font-semibold text-lacre-700">+{reserva.bibliokarma}</dd>
            </div>
          )}
        </dl>

        <div className="mt-auto pt-5">
          {reserva.puedeCalificar && esPrestadoAMi && (
            <button
              className="flex w-full cursor-pointer items-center justify-center gap-2 rounded-xl border border-tinta-200 px-4 py-2.5 text-sm font-semibold text-tinta-700 transition hover:bg-tinta-50"
              onClick={() => setOpenPopup(true)}
            >
              <span aria-hidden>☆</span> Calificar
            </button>
          )}

          {esPrestadoAMi && reserva.reseniaPuntuacion != null && (
            <p className="text-center text-sm text-stone-600">
              Tu calificación{' '}
              <span className="tracking-wider text-amber-500" aria-label={`${reserva.reseniaPuntuacion} de 5`}>
                {'★'.repeat(reserva.reseniaPuntuacion)}
                <span className="text-stone-300">{'★'.repeat(5 - reserva.reseniaPuntuacion)}</span>
              </span>
            </p>
          )}
        </div>
      </div>

      <CalificarPopup open={openPopup} onClose={(reload) => closePopup(reload)} reserva={reserva} />
    </div>
  )
}
