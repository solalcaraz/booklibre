import { Genero } from "../../models/LibroResponseModel"
import type { FiltrosLibro } from "../../types/ReservaLibroHome"
import { GENERO_OPTIONS } from "../../utils/formateEnumLibros"
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
    <aside className="h-fit w-full max-w-[280px] rounded-2xl border border-stone-200 bg-white p-4 sm:p-5">
      <form className="space-y-6" onSubmit={onAplicar}>

        {errores.length > 0 && (
          <div className="space-y-1 text-xs text-red-700">
            {errores.map(e => <p key={e}>⚠ {e}</p>)}
          </div>
        )}

        <fieldset>
          <legend className="text-xs font-semibold uppercase tracking-wide text-stone-500">Género</legend>
          <div className="mt-3 grid grid-cols-1 gap-2 text-sm text-stone-700">
            {Object.values(Genero).map(genero => (
              <label key={genero} className="flex items-center gap-2">
                <input
                  type="checkbox" className="accent-tinta-700"
                  checked={filtros.generos?.includes(genero) ?? false}
                  onChange={e => onGenero(genero, e.target.checked)}
                  data-testid={`checkbox-genero-${genero}`}
                />
                {GENERO_OPTIONS[genero]}
              </label>
            ))}
          </div>
        </fieldset>

        <hr className="border-stone-200" />

        <fieldset>
          <legend className="text-xs font-semibold uppercase tracking-wide text-stone-500">Páginas</legend>
          <RangeDoble
            min={0} max={2000}
            valorMin={filtros.cantPaginasMin ?? 0}
            valorMax={filtros.cantPaginasMax ?? 2000}
            onChangeMin={v => onActualizar('cantPaginasMin', v)}
            onChangeMax={v => onActualizar('cantPaginasMax', v)}
          />
        </fieldset>

        <hr className="border-stone-200" />

        <fieldset>
          <legend className="text-xs font-semibold uppercase tracking-wide text-stone-500">Fechas del préstamo</legend>

          <div className="flex flex-col gap-3 mt-3">
            
            <div className="flex flex-col w-full flex-1">
              <label className="text-xs text-stone-600">Desde</label>
              <input
                type="date"
                min={hoy}
                value={filtros.fechaDesde ?? hoy}
                onChange={e => onActualizar('fechaDesde', e.target.value)}
                className="w-full border border-stone-200 rounded-md px-2 py-1.5 text-sm focus:border-tinta-500 focus:outline-none"
              />
            </div>

            <div className="flex flex-col w-full flex-1">
              <label className="text-xs text-stone-600">Hasta</label>
              <input
                type="date"
                min={filtros.fechaDesde ?? hoy}
                value={filtros.fechaHasta ?? hoy}
                onChange={e => onActualizar('fechaHasta', e.target.value)}
                className="w-full border border-stone-200 rounded-md px-2 py-1.5 text-sm focus:border-tinta-500 focus:outline-none"
              />
            </div>

          </div>
        </fieldset>

        <hr className="border-stone-200" />

        <fieldset>
          <legend className="text-xs font-semibold uppercase tracking-wide text-stone-500">Más filtros</legend>
          <div className="flex flex-col gap-2 mt-3">
            <label className="text-xs text-stone-600">ISBN</label>
            <input
              type="text" placeholder="978-3-16..."
              value={filtros.isbn ?? ''}
              onChange={e => onActualizar('isbn', e.target.value)}
              className="w-full border border-stone-200 rounded-md px-3 py-2 text-sm focus:border-tinta-500 focus:outline-none"
              data-testid="input-isbn"
            />
          </div>
          <div className="flex flex-col gap-2 mt-3">
            <label className="text-xs text-stone-600">Dueño del libro</label>
            <input
              type="text" placeholder="Nombre"
              value={filtros.usuarioPrestador ?? ''}
              onChange={e => onActualizar('usuarioPrestador', e.target.value)}
              className="w-full border border-stone-200 rounded-md px-3 py-2 text-sm focus:border-tinta-500 focus:outline-none"
              data-testid="input-prestador"
            />
          </div>
        </fieldset>

        <button type="submit" className="w-full rounded-lg bg-tinta-700 px-4 py-2 text-sm font-semibold text-white hover:bg-tinta-800 transition cursor-pointer"
        data-testid="btn-aplicar">
          Aplicar filtros
        </button>
        <button type="button" onClick={onLimpiar} className="w-full rounded-lg px-4 py-2 text-sm font-medium text-stone-600 transition hover:bg-stone-100 hover:text-stone-900 cursor-pointer"
        data-testid="btn-limpiar">
          Limpiar filtros
        </button>
      </form>
    </aside>
  )
}