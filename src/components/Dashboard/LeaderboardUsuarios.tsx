import { useEffect, useState } from "react"
import { dashboardService, type UsuarioLeaderboard } from "../../services/DashboardService"

export const LeaderboardUsuarios = () => {
  const [data, setData] = useState<UsuarioLeaderboard[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    dashboardService.getLeaderboardUsuarios()
      .then(setData)
      .catch(() => setError("No se pudo cargar el leaderboard"))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="bg-white rounded-xl shadow p-4">
        <p className="text-slate-500">Cargando leaderboard...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-white rounded-xl shadow p-4">
        <p className="text-red-500">{error}</p>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-xl shadow p-4">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold text-slate-800">
          Top Usuarios Bibliokarma
        </h2>
        <span className="text-xs bg-slate-100 text-slate-600 px-2 py-1 rounded">
          Top 5
        </span>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-2">
        {data.map((user, index) => (
            <div
                key={user.id}
                className="flex items-center justify-between p-2 rounded-lg bg-slate-50 hover:bg-slate-100 transition shadow-sm"
            >
                <div className="flex items-center gap-3">
                    <div className="w-6 h-6 flex items-center justify-center rounded-full bg-indigo-100 text-indigo-700 font-bold text-xs">
                        {index + 1}
                    </div>

                    <div>
                        <p className="font-medium text-slate-800 text-sm">
                        {user.nombreCompleto}
                        </p>
                        <p className="text-xs text-slate-500">
                        ID: {user.id}
                        </p>
                    </div>
                </div>

                <div className="text-right">
                    <p className="font-semibold text-indigo-600 text-sm">
                        {user.bibliokarmas}
                    </p>
                    <p className="text-xs text-slate-500">
                        bibliokarmas
                    </p>
                </div>
            </div>
        ))}
      </div>
    </div>
  )
}