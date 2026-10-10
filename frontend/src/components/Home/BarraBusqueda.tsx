interface BarraBusquedaProps {
  busqueda: string
  onChangeBusqueda: (value: string) => void
  onBuscar: (e: React.FormEvent) => void
  onLimpiar: () => void
}

export const BarraBusqueda = ({ busqueda, onChangeBusqueda, onBuscar, onLimpiar }: BarraBusquedaProps) => {
  return (
    <section className="rounded-2xl border border-stone-200 bg-white p-8">
      <header>
        <h1 className="font-display text-3xl font-semibold text-tinta-900">Encontrá tu próxima lectura</h1>
        <p className="mt-2 max-w-2xl text-stone-600">
          Libros que prestan otros lectores, disponibles en las fechas que elijas. Cada reserva suma bibliokarma.
        </p>
      </header>
      <form className="mt-6" onSubmit={onBuscar}>
        <div className="relative">
          <input
            type="search"
            placeholder="Título o autor"
            value={busqueda}
            onChange={e => onChangeBusqueda(e.target.value)}
            onInput={e => {
              if ((e.target as HTMLInputElement).value === '') onLimpiar()
            }}
            className="w-full rounded-lg border border-stone-200 bg-stone-50 px-4 py-3 pr-28 focus:border-tinta-500 focus:bg-white focus:outline-none focus:ring-2 focus:ring-tinta-100"
            data-testid="input-busqueda"
          />
          <button
            type="submit"
            className="absolute right-2 top-1/2 -translate-y-1/2 rounded-md bg-tinta-700 px-4 py-2 text-sm font-semibold text-white hover:bg-tinta-800 transition cursor-pointer"
            data-testid="btn-buscar"
          >Buscar</button>
        </div>
      </form>
    </section>
  )
}