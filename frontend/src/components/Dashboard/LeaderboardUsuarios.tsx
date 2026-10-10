import { useEffect, useState } from "react"
import { dashboardService, type UsuarioLeaderboard } from "../../services/DashboardService"

export const LeaderboardUsuarios = () => {
  const [data, setData] = useState<UsuarioLeaderboard[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    dashboardService.getLeaderboardUsuarios()
      .then(setData)
      .catch(() => setError("No se pudo cargar el ranking"))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="rounded-2xl border border-stone-200 bg-white p-5">
        <p className="text-stone-500">Cargando ranking…</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-stone-200 bg-white p-5">
        <p className="text-red-700">{error}</p>
      </div>
    )
  }

  return (
    <div className="rounded-2xl border border-stone-200 bg-white p-5">
      <div className="flex justify-between items-center mb-4">
        <h2 className="font-display text-lg font-semibold text-stone-900">
          Lectores con más bibliokarma
        </h2>
        <span className="text-xs bg-stone-100 text-stone-600 px-2 py-1 rounded">
          Top 5
        </span>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-2">
        {data.map((user, index) => (
            <div
                key={user.id}
                className="flex items-center justify-between p-2 rounded-lg bg-stone-50 hover:bg-stone-100 transition shadow-sm"
            >
                <div className="flex items-center gap-3">
                    <div className="w-6 h-6 flex items-center justify-center rounded-full bg-tinta-50 text-tinta-700 font-bold text-xs">
                        {index + 1}
                    </div>

                    <div>
                        <p className="font-medium text-stone-800 text-sm">
                        {user.nombreCompleto}
                        </p>
                    </div>
                </div>

                <div className="text-right">
                    <p className="font-semibold text-tinta-700 text-sm">
                        {user.bibliokarmas}
                    </p>
                    <p className="text-xs text-stone-500">
                        bibliokarmas
                    </p>
                </div>
            </div>
        ))}
      </div>
    </div>
  )
}