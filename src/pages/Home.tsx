import { act, useState } from "react"
import { LibroCard } from "../components/LibroCard"
import type { FiltrosLibro, Orden, ReservaLibroHomeJSON } from "../types/ReservaLibroHome"
import { reservaService } from "../services/ReservaService"
import { Genero } from "../models/LibroResponseModel"
import { formatEnum } from "../utils/formateEnumLibros"
import DataGrid from "../components/DataGrid"

const hoy = new Date().toISOString().split('T')[0] // para quedarme solo con la fecha sin el horario

const filtrosIniciales: FiltrosLibro = {
  generos: [],
  cantPaginasMin: 0,
  cantPaginasMax: 1000,
  fechaDesde: hoy,
  fechaHasta: hoy,
  isbn: '',
  usuarioPrestador: '',
  orden: 'POPULARIDAD',
  numeroPagina: 0,
  tamanoPaginado: 6
}

export const Home = () => {
  const [filtros, setFiltros] = useState<FiltrosLibro>(filtrosIniciales)
  const [filtrosAplicados, setFiltrosAplicados] = useState<FiltrosLibro>(filtrosIniciales)
  const [errores, setErrores] = useState<string[]>([])
  
  //TODO: cambiar por local storage
  const userId = 2 //+JSON.parse(localStorage.getItem('user')!).id

  // metodo para actualizar inputs dinamicamente
  const actualizar = (campo: keyof FiltrosLibro, valor: unknown) => {
    setFiltros(prev => ({ ...prev, [campo]: valor }))
  }

  // validaciones para los filtros
  const validar = (): boolean => {
    const nuevosErrores: string[] = []

    if  (filtros.fechaDesde && filtros.fechaDesde < hoy)
      nuevosErrores.push('La fecha de inicio no puede ser anterior a hoy')
    if (filtros.fechaDesde && filtros.fechaHasta && filtros.fechaHasta < filtros.fechaDesde)
      nuevosErrores.push('La fecha hasta no puede ser anterior a la fecha desde')
    if (filtros.cantPaginasMin !== undefined && filtros.cantPaginasMax !== undefined && filtros.cantPaginasMin > filtros.cantPaginasMax)
      nuevosErrores.push('El mínimo de páginas no puede ser mayor al máximo')

    setErrores(nuevosErrores)
    return nuevosErrores.length === 0
  }

  const handleGenero = (genero: Genero, checked: boolean) => {
    const nuevosGeneros = checked 
    ? [...(filtros.generos ?? []), genero] : (filtros.generos ?? []).filter(g => g !== genero)
    actualizar('generos', nuevosGeneros)
  }
  
  const handleAplicarFiltros = (e: React.FormEvent) => {
    e.preventDefault()
    if (validar()) setFiltrosAplicados({ ...filtros })
  }
  
  const handleBusqueda = (e: React.FormEvent) => {
    e.preventDefault()
    if (validar()) setFiltrosAplicados({ ...filtros })
  }

  const handleOrden = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const nuevosFiltros = { ...filtros, orden: e.target.value as Orden}
    setFiltros(nuevosFiltros)
    setFiltrosAplicados(nuevosFiltros)
  }

  return (
    <main className="min-h-screen px-12 flex gap-8">
      
      {/* Filtrado para busqueda */}
      <aside className="w-fit rounded-2xl bg-white p-6 shadow-[0_10px_30px_rgba(15,23,42,0.08)] h-fit">
        <form className="space-y-6" onSubmit={handleAplicarFiltros}>

          {/* Erorres */}
          {errores.length > 0 && (
            <div className="text-xs text-red-500 space-y-1">
              {errores.map(e => <p key={e}>{e}</p>)}
            </div>
          )}

          <fieldset>
            <legend className="text-sm font-semibold text-slate-700">GÉNERO</legend>
            <div className="mt-3 space-y-2 text-sm text-slate-600">
              {Object.values(Genero).map(genero => (
                <label key={genero} className="flex items-center gap-2">
                  <input 
                    type="checkbox" 
                    className="accent-sky-400" 
                    checked={filtros.generos?.includes(genero) ?? false}
                    onChange={e => handleGenero(genero, e.target.checked)}/>
                  {formatEnum(genero)}
                </label>
              ))}
            </div>
          </fieldset>

          <hr className="border-slate-200" />

          {/* <fieldset>
            <legend className="text-sm font-semibold text-slate-700">Rango de páginas</legend>

            <input type="range" className="w-full mt-3 accent-sky-500"/>

            <div className="flex justify-between text-xs text-slate-400 mt-1">
              <span>150</span>
              <span>450</span>
            </div>
          </fieldset> */}
          <fieldset>
            <legend className="text-sm font-semibold text-slate-700">Rango de páginas</legend>
            <div className="flex gap-2 mt-3">
              <div className="flex flex-col w-1/2">
                <label className="text-xs text-slate-400">Mín</label>
                <input
                  type="number" min={0} max={1000}
                  value={filtros.cantPaginasMin ?? 0}
                  onChange={e => actualizar('cantPaginasMin', +e.target.value) }
                  className="border border-slate-200 rounded-md px-2 py-1 text-sm"
                />
              </div>
              <div className="flex flex-col w-1/2">
                <label className="text-xs text-slate-400">Máx</label>
                <input
                  type="number" min={0} max={1000}
                  value={filtros.cantPaginasMax ?? 1000}
                  onChange={e => actualizar('cantPaginasMax', +e.target.value) }
                  className="border border-slate-200 rounded-md px-2 py-1 text-sm"
                />
              </div>
            </div>
          </fieldset>

          <hr className="border-slate-200"/>

          <fieldset>
            <legend className="text-sm font-semibold text-slate-700">Rango de fechas para reservar</legend>
            <div className="flex gap-3 mt-3">
              <div className="flex flex-col w-1/2">
                <label className="text-xs text-slate-400">Desde</label>
                <input
                  type="date" min={hoy} value={filtros.fechaDesde ?? hoy}
                  onChange={e => actualizar('fechaDesde', e.target.value) }
                  className="border border-slate-200 rounded-md px-2 py-1 text-sm"
                />
              </div>

              <div className="flex flex-col w-1/2">
                <label className="text-xs text-slate-400">Hasta</label>
                <input
                  type="date" min={filtros.fechaDesde ?? hoy}
                  value={filtros.fechaHasta ?? hoy}
                  onChange={e => actualizar('fechaHasta', e.target.value) }
                  className="border border-slate-200 rounded-md px-2 py-1 text-sm"
                />
              </div>
            </div>
          </fieldset>

          <hr className="border-slate-200"/>

          <fieldset>
            <legend className="text-sm font-semibold text-slate-700">Detalles</legend>
            <div className="flex flex-col gap-2 mt-3">
              <label className="text-xs text-slate-400">ISBN</label>
              <input
                type="text" placeholder="978-3-16..."
                value={filtros.isbn ?? ''}
                onChange={e => actualizar('isbn', e.target.value) }
                className="border border-slate-200 rounded-md px-3 py-2 text-sm"
              />
            </div>

            <div className="flex flex-col gap-2 mt-3">
              <label className="text-xs text-slate-400">Prestado por...</label>
              <input
                type="text" placeholder="Usuario..."
                value={filtros.usuarioPrestador ?? ''}
                onChange={e => actualizar('usuarioPrestador', e.target.value) }
                className="border border-slate-200 rounded-md px-3 py-2 text-sm"
              />
            </div>
          </fieldset>
          
          <button 
            type="submit"
            className="w-full rounded-lg bg-sky-400 px-4 py-2 text-sm font-semibold text-white hover:bg-sky-500 transition cursor-pointer"
          >Aplicar Filtros</button>
        </form>
      </aside>

      <div className="flex flex-col gap-6 w-full">
        {/* Barra de busqueda */}
        <section className="rounded-2xl bg-white p-8 shadow-[0_10px_30px_rgba(15,23,42,0.08)]">
          <header>
            <h1 className="text-3xl font-extrabold text-slate-800">Encuentra tu próxima lectura</h1>
            <p className="text-slate-500 mt-2">Explora miles de libros disponibles para préstamo en nuestra comunidad.</p>
          </header>

          <form className="mt-6" onSubmit={handleBusqueda}>
            <div className="relative">
              <input
                type="search"
                placeholder="Buscar por título..."
                value={filtros.busqueda ?? ''}
                onChange={e => actualizar('busqueda', e.target.value) }
                className="w-full border border-slate-200 rounded-md px-4 py-3 pr-28 focus:outline-none focus:ring-2 focus:ring-sky-300"
              />

              <button
                type="submit"
                className="absolute right-2 top-1/2 -translate-y-1/2 rounded-md bg-sky-500 px-4 py-2 text-sm font-semibold text-white hover:bg-sky-600 transition cursor-pointer"
              >Buscar</button>
            </div>
          </form>
        </section>

        {/* Header resultados */}
        <section className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-slate-800">Libros Populares</h2>

          <div className="flex items-center gap-2 text-sm text-slate-500">
            <span>Ordenar por:</span>
            <select value={filtros.orden} 
              onChange={handleOrden}
              className="border border-slate-200 rounded-md px-2 py-1 text-slate-700"
            >
              <option value="POPULARIDAD">Popularidad</option>
              <option value="TITULO_ASC">Título A→Z</option>
              <option value="TITULO_DESC">Título Z→A</option>
              <option value="AUTOR_ASC">Autor A→Z</option>
              <option value="DUENO_ASC">Dueño A→Z</option>
            </select>
          </div>
        </section>

        {/* Lista del resultado de libros */}
        <DataGrid<ReservaLibroHomeJSON, FiltrosLibro>
          fetchFunction={(req) => reservaService.getLibrosDisponiblesReserva(userId, req)}
          filter={filtrosAplicados}
          renderItem={(libro) => <LibroCard libro={libro} />}
        />
      </div>
    </main>
  )
}