interface BarraBusquedaProps {
  busqueda: string
  onChangeBusqueda: (value: string) => void
  onBuscar: (e: React.FormEvent) => void
  onLimpiar: () => void
}

export const BarraBusqueda = ({ busqueda, onChangeBusqueda, onBuscar, onLimpiar }: BarraBusquedaProps) => {
  return (
    <section className="rounded-2xl bg-white p-8 shadow-[0_10px_30px_rgba(15,23,42,0.08)]">
      <header>
        <h1 className="text-3xl font-extrabold text-slate-800">Encuentra tu próxima lectura</h1>
        <p className="text-slate-500 mt-2">Explora miles de libros disponibles para préstamo en nuestra comunidad.</p>
      </header>
      <form className="mt-6" onSubmit={onBuscar}>
        <div className="relative">
          <input
            type="search"
            placeholder="Buscar por título o autor..."
            value={busqueda}
            onChange={e => onChangeBusqueda(e.target.value)}
            onInput={e => {
              if ((e.target as HTMLInputElement).value === '') onLimpiar()
            }}
            className="w-full border border-slate-200 rounded-md px-4 py-3 pr-28 focus:outline-none focus:ring-2 focus:ring-sky-300"
            data-testid="input-busqueda"
          />
          <button
            type="submit"
            className="absolute right-2 top-1/2 -translate-y-1/2 rounded-md bg-sky-500 px-4 py-2 text-sm font-semibold text-white hover:bg-sky-600 transition cursor-pointer"
            data-testid="btn-buscar"
          >Buscar</button>
        </div>
      </form>
    </section>
  )
}