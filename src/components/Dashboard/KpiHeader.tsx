import { useEffect, useState } from 'react'
import { dashboardService } from '../../services/DashboardService'
import type { TasaConversionLibro } from '../../types/Dashboard'

const porcentaje = (valor: number) => `${(valor * 100).toFixed(1)}%`

export const KpiHeader = () => {
  const [kpis, setKpis] = useState<TasaConversionLibro[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    let cancelado = false

    dashboardService.getTasaConversionClicksReservas()
      .then((data) => {
        if (!cancelado) setKpis(data)
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
      <section className="bg-white border border-slate-100 rounded-lg px-5 py-4 shadow-sm" data-testid="kpi-header-loading">
        <h2 className="text-sm font-semibold text-slate-700">KPIs del tablero</h2>
        <p className="text-sm text-slate-400 mt-1">Cargando metricas...</p>
      </section>
    )
  }

  if (error) {
    return (
      <section className="bg-white border border-slate-100 rounded-lg px-5 py-4 shadow-sm" data-testid="kpi-header-error">
        <h2 className="text-sm font-semibold text-slate-700">KPIs del tablero</h2>
        <p className="text-sm text-red-500 mt-1">No se pudieron cargar las metricas.</p>
      </section>
    )
  }

  if (kpis.length === 0) {
    return (
      <section className="bg-white border border-slate-100 rounded-lg px-5 py-4 shadow-sm" data-testid="kpi-header-empty">
        <h2 className="text-sm font-semibold text-slate-700">KPIs del tablero</h2>
        <p className="text-sm text-slate-400 mt-1">Todavia no hay datos de conversion.</p>
      </section>
    )
  }

  return (
    <section className="bg-white border border-slate-100 rounded-lg px-5 py-4 shadow-sm" data-testid="kpi-header">
      <div className="flex items-center justify-between gap-4 mb-4">
        <div>
          <h2 className="text-sm font-semibold text-slate-700">KPIs del tablero</h2>
          <p className="text-xs text-slate-400">Top 5 conversion clicks vs reservas</p>
        </div>
        <span className="text-xs font-medium text-slate-500">{kpis.length} libros</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-5 gap-3">
        {kpis.map((kpi) => (
          <article
            key={kpi.libroId}
            className="border border-slate-100 rounded-md p-3 bg-slate-50"
            data-testid={`kpi-card-${kpi.libroId}`}
          >
            <p className="text-sm font-semibold text-slate-800 truncate" title={kpi.titulo}>
              {kpi.titulo}
            </p>
            <p className="text-xs text-slate-400 truncate" title={kpi.autor}>
              {kpi.autor}
            </p>

            <div className="mt-3 flex items-end justify-between gap-2">
              <div className="text-xs text-slate-500">
                <p>{kpi.clicks} clicks</p>
                <p>{kpi.reservas} reservas</p>
              </div>
              <p className="text-lg font-bold text-blue-600" data-testid={`kpi-tasa-${kpi.libroId}`}>
                {porcentaje(kpi.tasaConversion)}
              </p>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
