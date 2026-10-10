import { useEffect, useState } from 'react'
import { dashboardService } from '../../services/DashboardService'
import type { ActividadReciente } from '../../types/Dashboard'

export const ActividadFeedHeader = () => {
  const [actividades, setActividades] = useState<ActividadReciente[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    let cancelado = false

    dashboardService.getFeedActividadReciente()
      .then((data) => {
        if (!cancelado) setActividades(data)
      })
      .catch(() => {
        if (!cancelado) setError(true)
      })
      .finally(() => {
        if (!cancelado) setCargando(false)
      })

    return () => { cancelado = true }
  }, [])

  const formatearFecha = (timestampStr: string) => {
    const fecha = new Date(parseInt(timestampStr))
    return fecha.toLocaleDateString('es-AR', {
      day: '2-digit', month: 'short', year: 'numeric'
    })
  }

  const tipoEvento = (actividad: ActividadReciente) =>
    actividad.tipoEvento === 'ALTA_LIBRO' ? 'Nuevo Libro' : 'Nueva Reserva'

  const iconoEvento = (actividad: ActividadReciente) =>
    actividad.tipoEvento === 'ALTA_LIBRO' ? '📚' : '✅'

  const descripcion = (actividad: ActividadReciente) =>
    actividad.tipoEvento === 'ALTA_LIBRO'
      ? actividad.titulo ?? '—'
      : `Reserva #${actividad.reservaId}`

  const colorBorde: Record<string, string> = {
    ALTA_LIBRO:    'border-l-4 border-tinta-700',
    NUEVA_RESERVA: 'border-l-4 border-green-400',
  }

  const totalSlots = 5

  const contenedorBase =
    "w-full rounded-2xl border border-stone-200 bg-white p-5"

  if (cargando) {
    return (
      <div className={`${contenedorBase} flex items-center justify-center h-[105px]`}
           data-testid="feed-loading">
        <p className="text-sm text-stone-500">Cargando actividad…</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className={`${contenedorBase} flex items-center justify-center h-[105px]`}>
        <p className="text-sm text-red-700">No se pudo cargar la actividad.</p>
      </div>
    )
  }

  return (
    <div className={contenedorBase} data-testid="feed-container">

      <div className="flex items-center justify-between mb-3">
        <div>
          <p className="font-display text-lg font-semibold text-stone-900">Actividad reciente</p>
          <p className="text-xs text-stone-500">Los últimos libros publicados y reservas confirmadas</p>
        </div>
        <span className="text-xs text-stone-500 bg-stone-100 rounded-full px-2 py-0.5">
          Top {totalSlots}
        </span>
      </div>

      <div className="grid grid-cols-5 gap-2">
        {[...Array(totalSlots)].map((_, index) => {
          const actividad = actividades[index]

          if (!actividad) {
            return (
              <div key={index}
                   className="rounded-md border border-dashed border-stone-200 p-3
                              flex items-center justify-center min-h-[72px]">
                <p className="text-xs text-stone-500">Sin datos</p>
              </div>
            )
          }

          const borde = colorBorde[actividad.tipoEvento] ?? 'border-l-4 border-stone-300'

          return (
            <article key={index}
                     className={`rounded-md bg-stone-50 p-3 flex flex-col gap-1
                                 min-h-[72px] ${borde}`}>

              <p className="text-[10px] font-medium text-stone-400 uppercase tracking-wide">
                {iconoEvento(actividad)} {tipoEvento(actividad)}
              </p>

              <p className="text-xs font-semibold text-stone-700 line-clamp-2 flex-1">
                {descripcion(actividad)}
              </p>

              <div className="flex items-center justify-between mt-auto gap-1">
                <span className="text-[10px] text-stone-500 truncate">
                  {actividad.usuario}
                </span>
                <span className="text-[10px] text-stone-400 whitespace-nowrap">
                  {formatearFecha(actividad.fecha)}
                </span>
              </div>

            </article>
          )
        })}
      </div>
    </div>
  )
}