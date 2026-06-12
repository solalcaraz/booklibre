import { useEffect, useState } from 'react'
import { dashboardService } from '../../services/DashboardService'
import type { PromedioCalificacionPorTipo } from '../../types/Dashboard'

export const AnalisisCalificaciones = () => {
    const [calificaciones, setCalificaciones] = useState<PromedioCalificacionPorTipo[]>([])
    const [cargando, setCargando] = useState(true)
    const [error, setError] = useState(false)

    const estilosPorTipo: Record<PromedioCalificacionPorTipo['tipo'], {
        borde: string
        fondo: string
        texto: string
    }> = {
        COMUN: {
            borde: 'border-amber-400',
            fondo: 'bg-amber-50',
            texto: 'text-amber-700'
        },
        DEDICATORIA: {
            borde: 'border-purple-400',
            fondo: 'bg-purple-50',
            texto: 'text-purple-700'
        },
        COLECCIONABLE: {
            borde: 'border-rose-400',
            fondo: 'bg-rose-50',
            texto: 'text-rose-700'
        }
    }

    useEffect(() => {
        let cancelado = false

        dashboardService.getAnalisisCalificaciones()
            .then((data) => {
                if (!cancelado) setCalificaciones(data)
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

    const formatearTipo = (tipo: PromedioCalificacionPorTipo['tipo']) =>
        tipo.charAt(0) + tipo.slice(1).toLowerCase()

    const formatearPuntaje = (puntaje: number) => puntaje.toFixed(2)

    const totalSlots = 3
    const contenedorBase = "bg-white border border-slate-100 rounded-lg shadow-sm p-4 w-full"

    if (cargando) {
        return (
            <div className={`${contenedorBase} flex items-center justify-center h-[105px]`}>
                <p className="text-sm text-slate-400">Cargando análisis de calificaciones...</p>
            </div>
        )
    }

    if (error) {
        return (
            <div className={`${contenedorBase} flex items-center justify-center h-[105px]`}>
                <p className="text-sm text-rose-500">Ocurrió un error al cargar el análisis de calificaciones.</p>
            </div>
        )
    }

    return (
        <div className={contenedorBase}>

            {/* ── Encabezado ── */}
            <div className="flex items-center justify-between mb-3">
                <div>
                    <p className="text-sm font-semibold text-slate-700">Análisis de Calificaciones</p>
                    <p className="text-xs text-slate-400">Promedio de calificación agrupado por tipo de libro.</p>
                </div>
                <span className="text-xs text-slate-400 bg-slate-100 rounded-full px-2 py-0.5">
                    Total: {calificaciones.length}
                </span>
            </div>

            {/* ── Contenido ── */}
            <div className="grid grid-cols-3 gap-10">
                {[...Array(totalSlots)].map((_, index) => {
                const calificacion = calificaciones[index]

                if (!calificacion) {
                    return (
                    <div key={index}
                        className="rounded-md border border-dashed border-slate-200 p-3
                                    flex items-center justify-center min-h-[72px]">
                        <p className="text-xs text-slate-300">Sin datos</p>
                    </div>
                    )
                }

                const estilos = estilosPorTipo[calificacion.tipo]

                return (
                    <article key={index}
                            className={`rounded-md p-3 flex flex-col gap-1 min-h-[72px]
                                        border-l-4 ${estilos.borde} ${estilos.fondo}`}>

                    <p className="text-[10px] font-medium uppercase tracking-wide text-slate-500">
                        Tipo
                    </p>

                    <p className={`text-xs font-semibold line-clamp-2 flex-1 ${estilos.texto}`}>
                        {formatearTipo(calificacion.tipo)}
                    </p>

                    <div className="flex items-center justify-between mt-auto gap-1">
                        <span className="text-[10px] text-slate-500 truncate">
                            Puntaje promedio
                        </span>
                        <span className={`text-lg font-bold whitespace-nowrap ${estilos.texto}`}>
                            {formatearPuntaje(calificacion.puntajePromedio)}
                        </span>
                    </div>

                    </article>
                )
                })}
            </div>
        </div>
    )
}
