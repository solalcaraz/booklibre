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
      <section className="rounded-2xl border border-stone-200 bg-white px-5 py-4" data-testid="salud-header-loading">
        <h2 className="font-display text-lg font-semibold text-stone-900">Estado del catálogo</h2>
        <p className="text-sm text-stone-500 mt-1">Cargando métricas de disponibilidad…</p>
      </section>
    )
  }

  if (error) {
    return (
      <section className="rounded-2xl border border-stone-200 bg-white px-5 py-4" data-testid="salud-header-error">
        <h2 className="font-display text-lg font-semibold text-stone-900">Estado del catálogo</h2>
        <p className="text-sm text-red-700 mt-1">No se pudieron cargar las métricas de estado.</p>
      </section>
    )
  }

  if (!salud) return null

  return (
    <section className="rounded-2xl border border-stone-200 bg-white px-5 py-4" data-testid="salud-header">
      <div className="flex items-center justify-between gap-4 mb-4">
        <div>
          <h2 className="font-display text-lg font-semibold text-stone-900">Estado del catálogo</h2>
          <p className="text-xs text-stone-500">Cada libro cae en un solo grupo, así que los cuatro suman el total</p>
        </div>
        <span className="cifra rounded-full bg-tinta-50 px-3 py-1 text-xs font-semibold text-tinta-800">
          Total: {salud.total} libros
        </span>
      </div>

      <div className="grid grid-cols-2 gap-3 md:grid-cols-4">
        {[
          { titulo: 'Prestados hoy', valor: salud.prestados, punto: 'bg-lacre-600' },
          { titulo: 'Nunca reservados', valor: salud.disponiblesNuncaReservados, punto: 'bg-stone-400' },
          { titulo: 'Con reservas futuras', valor: salud.disponiblesReservadosAFuturo, punto: 'bg-tinta-500' },
          { titulo: 'Ya devueltos', valor: salud.disponiblesDevueltos, punto: 'bg-emerald-600' }
        ].map(({ titulo, valor, punto }) => (
          <article key={titulo} className="rounded-xl border border-stone-200 bg-papel p-3">
            <p className="flex items-center gap-1.5 text-xs font-medium text-stone-600">
              <span className={`h-2 w-2 rounded-full ${punto}`} />
              {titulo}
            </p>
            <p className="cifra mt-1 font-display text-2xl font-semibold text-stone-900">{valor}</p>
          </article>
        ))}
      </div>
    </section>
  )
}