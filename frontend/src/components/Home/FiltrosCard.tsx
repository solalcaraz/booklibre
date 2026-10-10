import { Genero } from "../../models/LibroResponseModel"
import type { FiltrosLibro } from "../../types/ReservaLibroHome"
import { formatEnum } from "../../utils/formateEnumLibros"
import { RangeDoble } from "./RangeDoble"

interface PanelFiltrosProps {
  filtros: FiltrosLibro
  errores: string[]
  onAplicar: (e: React.FormEvent) => void
  onLimpiar: () => void
  onActualizar: (campo: keyof FiltrosLibro, valor: unknown) => void
  onGenero: (genero: Genero, checked: boolean) => void
  hoy: string
}

export const PanelFiltros = ({ filtros, errores, onAplicar, onLimpiar, onActualizar, onGenero, hoy }: PanelFiltrosProps) => {
  return (
    // <aside className="w-fit rounded-2xl bg-white p-6 shadow-[0_10px_30px_rgba(15,23,42,0.08)] h-fit">
    <aside className="
  w-full
  max-w-[280px]
  rounded-2xl
  bg-white
  p-4 sm:p-5
  shadow-[0_10px_30px_rgba(15,23,42,0.08)]
  h-fit
">
      <form className="space-y-6" onSubmit={onAplicar}>

        {errores.length > 0 && (
          <div className="text-xs text-red-500 space-y-1">
            {errores.map(e => <p key={e}>⚠ {e}</p>)}
          </div>
        )}

        <fieldset>
          <legend className="text-sm font-semibold text-slate-700">GÉNERO</legend>
          <div className="mt-3 grid grid-cols-1 sm:grid-cols-2 gap-2 text-sm text-slate-600">
            {/* desarmo el enum genero para mostrar en los checkbox cada uno */}
            {Object.values(Genero).map(genero => (
              <label key={genero} className="flex items-center gap-2">
                <input
                  type="checkbox" className="accent-sky-400"
                  checked={filtros.generos?.includes(genero) ?? false}
                  onChange={e => onGenero(genero, e.target.checked)}
                  data-testid={`checkbox-genero-${genero}`}
                />
                {formatEnum(genero)}
              </label>
            ))}
          </div>
        </fieldset>

        <hr className="border-slate-200" />

        <fieldset>
          <legend className="text-sm font-semibold text-slate-700">Rango de páginas</legend>
          <RangeDoble
            min={0} max={2000}
            valorMin={filtros.cantPaginasMin ?? 0}
            valorMax={filtros.cantPaginasMax ?? 2000}
            onChangeMin={v => onActualizar('cantPaginasMin', v)}
            onChangeMax={v => onActualizar('cantPaginasMax', v)}
          />
        </fieldset>

        <hr className="border-slate-200" />

        <fieldset>
          <legend className="text-sm font-semibold text-slate-700">
            Rango de fechas
          </legend>

          <div className="flex flex-col gap-3 mt-3">
            
            <div className="flex flex-col w-full flex-1">
              <label className="text-xs text-slate-400">Desde</label>
              <input
                type="date"
                min={hoy}
                value={filtros.fechaDesde ?? hoy}
                onChange={e => onActualizar('fechaDesde', e.target.value)}
                className="w-full border border-slate-200 rounded-md px-2 py-1 text-sm"
              />
            </div>

            <div className="flex flex-col w-full flex-1">
              <label className="text-xs text-slate-400">Hasta</label>
              <input
                type="date"
                min={filtros.fechaDesde ?? hoy}
                value={filtros.fechaHasta ?? hoy}
                onChange={e => onActualizar('fechaHasta', e.target.value)}
                className="w-full border border-slate-200 rounded-md px-2 py-1 text-sm"
              />
            </div>

          </div>
        </fieldset>

        <hr className="border-slate-200" />

        <fieldset>
          <legend className="text-sm font-semibold text-slate-700">Detalles</legend>
          <div className="flex flex-col gap-2 mt-3">
            <label className="text-xs text-slate-400">ISBN</label>
            <input
              type="text" placeholder="978-3-16..."
              value={filtros.isbn ?? ''}
              onChange={e => onActualizar('isbn', e.target.value)}
              // className="border border-slate-200 rounded-md px-3 py-2 text-sm"
              className="w-full border border-slate-200 rounded-md px-3 py-2 text-sm"
              data-testid="input-isbn"
            />
          </div>
          <div className="flex flex-col gap-2 mt-3">
            <label className="text-xs text-slate-400">Prestado por...</label>
            <input
              type="text" placeholder="Usuario..."
              value={filtros.usuarioPrestador ?? ''}
              onChange={e => onActualizar('usuarioPrestador', e.target.value)}
              // className="border border-slate-200 rounded-md px-3 py-2 text-sm"
              className="w-full border border-slate-200 rounded-md px-3 py-2 text-sm"
              data-testid="input-prestador"
            />
          </div>
        </fieldset>

        <button type="submit" className="w-full rounded-lg bg-sky-400 px-4 py-2 text-sm font-semibold text-white hover:bg-sky-500 transition cursor-pointer"
        data-testid="btn-aplicar">
          Aplicar Filtros
        </button>
        <button type="button" onClick={onLimpiar} className="w-full rounded-lg border bg-sky-100 border-slate-300 px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-50 transition cursor-pointer"
        data-testid="btn-limpiar">
          Limpiar Filtros
        </button>
      </form>
    </aside>
  )
}