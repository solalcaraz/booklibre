import { useEffect, useState } from 'react'
import { dashboardService } from '../../services/DashboardService'
import type { EstadoSaludCatalogo } from '../../types/Dashboard'

export const CatalogoSaludKpi = () => {
  const [salud, setSalud] = useState<EstadoSaludCatalogo | null>(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    let cancelado = false

    dashboardService.getEstadoSaludCatalogo()
      .then((data) => {
        if (!cancelado) setSalud(data)
      })
      .catch(() => {
        if (!cancelado) setError(true)
      })
      .finally(() => {
        if (!cancelado) setCargando(false)
      })

    return () => {
      cancelado = true
    }
  }, [])

  if (cargando) {
    return (
      <section className="bg-white border border-slate-100 rounded-lg px-5 py-4 shadow-sm" data-testid="salud-header-loading">
        <h2 className="text-sm font-semibold text-slate-700">Salud del Catálogo</h2>
        <p className="text-sm text-slate-400 mt-1">Cargando métricas de disponibilidad...</p>
      </section>
    )
  }

  if (error) {
    return (
      <section className="bg-white border border-slate-100 rounded-lg px-5 py-4 shadow-sm" data-testid="salud-header-error">
        <h2 className="text-sm font-semibold text-slate-700">Salud del Catálogo</h2>
        <p className="text-sm text-red-500 mt-1">No se pudieron cargar las métricas de estado.</p>
      </section>
    )
  }

  if (!salud) return null

  return (
    <section className="bg-white border border-slate-100 rounded-lg px-5 py-4 shadow-sm" data-testid="salud-header">
      <div className="flex items-center justify-between gap-4 mb-4">
        <div>
          <h2 className="text-sm font-semibold text-slate-700">Salud del Catálogo</h2>
          <p className="text-xs text-slate-400">Distribución de libros</p>
        </div>
        <span className="text-xs font-bold text-indigo-600 bg-indigo-50 border border-indigo-100 px-3 py-1 rounded-full">
          Total: {salud.total} libros
        </span>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
        <article className="border border-rose-100 rounded-md p-3 bg-rose-50/50">
          <p className="text-xs font-medium text-rose-500 truncate" title="Prestados (Hoy)">
            Prestados (Hoy)
          </p>
          <p className="text-xl font-bold text-rose-700 mt-1">{salud.prestados}</p>
        </article>

        <article className="border border-emerald-100 rounded-md p-3 bg-emerald-50/50">
          <p className="text-xs font-medium text-emerald-500 truncate" title="Nunca Reservados">
            Nunca Reservados
          </p>
          <p className="text-xl font-bold text-emerald-700 mt-1">{salud.disponiblesNuncaReservados}</p>
        </article>

        <article className="border border-amber-100 rounded-md p-3 bg-amber-50/50">
          <p className="text-xs font-medium text-amber-500 truncate" title="Reservados a Futuro">
            Reservados a Futuro (Disponibles)
          </p>
          <p className="text-xl font-bold text-amber-700 mt-1">{salud.disponiblesReservadosAFuturo}</p>
        </article>

        <article className="border border-sky-100 rounded-md p-3 bg-sky-50/50">
          <p className="text-xs font-medium text-sky-500 truncate" title="Devueltos (Disponibles)">
            Devueltos (Disponibles)
          </p>
          <p className="text-xl font-bold text-sky-700 mt-1">{salud.disponiblesDevueltos}</p>
        </article>
      </div>
    </section>
  )
}