import { useEffect, useState } from 'react'
import { dashboardService } from '../../services/DashboardService'
import type { PromedioCalificacionPorTipo } from '../../types/Dashboard'
import { TIPO_OPTIONS } from '../../utils/formateEnumLibros'

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
            borde: 'border-tinta-500',
            fondo: 'bg-tinta-50',
            texto: 'text-tinta-800'
        },
        DEDICATORIA: {
            borde: 'border-tinta-500',
            fondo: 'bg-tinta-50',
            texto: 'text-tinta-800'
        },
        COLECCIONABLE: {
            borde: 'border-tinta-500',
            fondo: 'bg-tinta-50',
            texto: 'text-tinta-800'
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

    const formatearTipo = (tipo: PromedioCalificacionPorTipo['tipo']) => TIPO_OPTIONS[tipo] ?? tipo

    const formatearPuntaje = (puntaje: number) => puntaje.toFixed(2)

    const totalSlots = 3
    const contenedorBase = "w-full rounded-2xl border border-stone-200 bg-white p-5"

    if (cargando) {
        return (
            <div className={`${contenedorBase} flex items-center justify-center h-[105px]`}>
                <p className="text-sm text-stone-500">Cargando calificaciones…</p>
            </div>
        )
    }

    if (error) {
        return (
            <div className={`${contenedorBase} flex items-center justify-center h-[105px]`}>
                <p className="text-sm text-red-700">No se pudieron cargar las calificaciones.</p>
            </div>
        )
    }

    return (
        <div className={contenedorBase}>

            <div className="flex items-center justify-between mb-3">
                <div>
                    <p className="font-display text-lg font-semibold text-stone-900">Calificación por tipo de libro</p>
                    <p className="text-xs text-stone-500">Promedio de los libros con al menos una reseña.</p>
                </div>
                <span className="text-xs text-stone-500 bg-stone-100 rounded-full px-2 py-0.5">
                    Total: {calificaciones.length}
                </span>
            </div>

            <div className="grid grid-cols-3 gap-10">
                {[...Array(totalSlots)].map((_, index) => {
                const calificacion = calificaciones[index]

                if (!calificacion) {
                    return (
                    <div key={index}
                        className="rounded-md border border-dashed border-stone-200 p-3
                                    flex items-center justify-center min-h-[72px]">
                        <p className="text-xs text-stone-500">Sin datos</p>
                    </div>
                    )
                }

                const estilos = estilosPorTipo[calificacion.tipo]

                return (
                    <article key={index}
                            className={`rounded-md p-3 flex flex-col gap-1 min-h-[72px]
                                        border-l-4 ${estilos.borde} ${estilos.fondo}`}>

                    <p className="text-[10px] font-medium uppercase tracking-wide text-stone-500">
                        Tipo
                    </p>

                    <p className={`text-xs font-semibold line-clamp-2 flex-1 ${estilos.texto}`}>
                        {formatearTipo(calificacion.tipo)}
                    </p>

                    <div className="flex items-center justify-between mt-auto gap-1">
                        <span className="text-[10px] text-stone-500 truncate">
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
