import { ActividadFeedHeader } from '../components/Dashboard/ActividadFeedHeader'
import { AnalisisCalificaciones } from '../components/Dashboard/AnalisisCalificaciones'
import { KpiHeader } from '../components/Dashboard/KpiHeader'
import { LeaderboardUsuarios } from '../components/Dashboard/LeaderboardUsuarios'
import { CatalogoSaludKpi } from '../components/Dashboard/SaludKpiHeader'

export const Dashboard = () => {
    return (
        <div className="flex flex-col gap-6 p-6">
        <header>
            <h1 className="text-2xl font-bold text-slate-800">Tablero de Administración</h1>
            <p className="text-sm text-slate-500">Métricas y estado general del sistema</p>
        </header>

        <div className="flex flex-col gap-6">
            <KpiHeader />
            <LeaderboardUsuarios />
            <CatalogoSaludKpi />
            <AnalisisCalificaciones />
            <ActividadFeedHeader />
        </div>
        </div>
    )
}