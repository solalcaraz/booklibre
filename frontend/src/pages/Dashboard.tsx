import { ActividadFeedHeader } from '../components/Dashboard/ActividadFeedHeader'
import { AnalisisCalificaciones } from '../components/Dashboard/AnalisisCalificaciones'
import { KpiHeader } from '../components/Dashboard/KpiHeader'
import { LeaderboardUsuarios } from '../components/Dashboard/LeaderboardUsuarios'
import { CatalogoSaludKpi } from '../components/Dashboard/SaludKpiHeader'

export const Dashboard = () => {
    return (
        <div className="mx-auto flex max-w-7xl flex-col gap-6 px-8 pb-10">
        <header>
            <h1 className="font-display text-3xl font-semibold text-tinta-900">Tablero</h1>
            <p className="mt-1 text-stone-600">Qué busca la comunidad, qué termina reservando y cómo está el catálogo hoy.</p>
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