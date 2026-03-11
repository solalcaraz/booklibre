import { useState } from "react"
import { LibroCard } from "../components/LibroCard"
import { type LibroHomeJSON } from "../classes/Libro"
import { libroService } from "../services/LibroService"
import { useOnInit } from "../utils/hooks"
import { Spinner } from "../components/Spinner"

export const Home = () => {
  const [libros, setLibros] = useState<LibroHomeJSON[]>([])
  const [loading, setLoading] = useState(true)

  const traerLibros = async () => {
    try {
      const libros = await libroService.getAll()
      setLibros(libros)
    } catch (error: unknown) {
      console.log(error)
      setLibros([])
    } finally {
      setLoading(false)
    }
  }

  useOnInit(traerLibros)

  if (loading) return <Spinner /> 

  return (
    <main className="min-h-screen px-12 flex gap-8">
      
      {/* Filtrado para busqueda */}
      <aside className="w-fit rounded-2xl bg-white p-6 shadow-[0_10px_30px_rgba(15,23,42,0.08)] h-fit">
        <form className="space-y-6">
          <fieldset>
            <legend className="text-sm font-semibold text-slate-700">
              GÉNERO
            </legend>

            <div className="mt-3 space-y-2 text-sm text-slate-600">
              <label className="flex items-center gap-2">
                <input type="checkbox" className="accent-emerald-400"/>
                Ciencia ficción
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" className="accent-emerald-400"/>
                Drama
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" className="accent-emerald-400"/>
                Autoayuda
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" className="accent-emerald-400"/>
                Romance
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" className="accent-emerald-400"/>
                Diseño
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" className="accent-emerald-400"/>
                Literatura clásica
              </label>
            </div>
          </fieldset>

          <hr className="border-slate-200" />

          <fieldset>
            <legend className="text-sm font-semibold text-slate-700">
              Rango de páginas
            </legend>

            <input type="range" className="w-full mt-3 accent-sky-500"/>

            <div className="flex justify-between text-xs text-slate-400 mt-1">
              <span>150</span>
              <span>450</span>
            </div>
          </fieldset>

          <hr className="border-slate-200"/>

          <fieldset>
            <legend className="text-sm font-semibold text-slate-700">
              Rango de fechas para reservar
            </legend>

            <div className="flex gap-3 mt-3">
              <div className="flex flex-col w-1/2">
                <label className="text-xs text-slate-400">Desde</label>
                <input
                  type="date"
                  className="border border-slate-200 rounded-md px-2 py-1 text-sm"
                />
              </div>

              <div className="flex flex-col w-1/2">
                <label className="text-xs text-slate-400">Hasta</label>
                <input
                  type="date"
                  className="border border-slate-200 rounded-md px-2 py-1 text-sm"
                />
              </div>
            </div>
          </fieldset>

          <hr className="border-slate-200"/>

          <fieldset>
            <legend className="text-sm font-semibold text-slate-700">
              Detalles
            </legend>

            <div className="flex flex-col gap-2 mt-3">
              <label className="text-xs text-slate-400">ISBN</label>
              <input
                type="text"
                placeholder="978-3-16..."
                className="border border-slate-200 rounded-md px-3 py-2 text-sm"
              />
            </div>

            <div className="flex flex-col gap-2 mt-3">
              <label className="text-xs text-slate-400">Prestado por...</label>
              <input
                type="text"
                placeholder="Usuario..."
                className="border border-slate-200 rounded-md px-3 py-2 text-sm"
              />
            </div>
          </fieldset>
          
          <button 
            type="submit"
            className="w-full rounded-lg bg-sky-400 px-4 py-2 text-sm font-semibold text-white hover:bg-sky-500 transition cursor-pointer"
          >
            Aplicar Filtros
          </button>
        </form>
      </aside>

      <div className="flex flex-col gap-6 w-full">
        {/* Barra de busqueda */}
        <section className="rounded-2xl bg-white p-8 shadow-[0_10px_30px_rgba(15,23,42,0.08)]">
          <header>
            <h1 className="text-3xl font-extrabold text-slate-800">
              Encuentra tu próxima lectura
            </h1>

            <p className="text-slate-500 mt-2">
              Explora miles de libros disponibles para préstamo en nuestra comunidad.
            </p>
          </header>

          <form className="mt-6">
            <div className="relative">
              <input
                type="search"
                placeholder="Buscar por título..."
                className="w-full border border-slate-200 rounded-md px-4 py-3 pr-28 focus:outline-none focus:ring-2 focus:ring-sky-300"
              />

              <button
                type="submit"
                className="absolute right-2 top-1/2 -translate-y-1/2 rounded-md bg-sky-500 px-4 py-2 text-sm font-semibold text-white hover:bg-sky-600 transition cursor-pointer"
              >
                Buscar
              </button>
            </div>
          </form>
        </section>

        {/* Header resultados */}
        <section className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-slate-800">
            Libros Populares
          </h2>

          <div className="flex items-center gap-2 text-sm text-slate-500">
            <span>Ordenar por:</span>

            <select className="border border-slate-200 rounded-md px-2 py-1 text-slate-700">
              <option>Relevancia</option>
              <option>Mejor valorados</option>
              <option>Más recientes</option>
            </select>
          </div>
        </section>

        {/* Lista del resultado de libros */}
        <section className="grid grid-cols-3 gap-6">
          {libros.map(libro => (
            <LibroCard key={libro.id} libro={libro} />
          ))}
        </section>
        {/* <DataGrid<LibroHomeJSON, LibroFilter>
          fetchFunction={libroService.getAll}
          renderItem={(libro) => (
            <LibroCard libro={libro} />
          )}
        /> */}
      </div>
    </main>
  )
}