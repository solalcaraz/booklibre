import { LibroCard } from "../components/LibroCard"

export const Home = () => {
  return (
    <main className="min-h-screen flex gap-6 p-6">
      
      {/* Card para filtrado de busqueda */}
      <aside className="bg-white rounded-xl shadow-md p-4">
        <form>
          <fieldset>
            <legend className="text-sm font-medium text-gray-600">
              GÉNERO
            </legend>

            <div className="mt-2 space-y-2">
              <label className="flex items-center gap-2">
                <input type="checkbox" />
                Ciencia ficción
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" />
                Drama
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" />
                Autoayuda
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" />
                Romance
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" />
                Diseño
              </label>

              <label className="flex items-center gap-2">
                <input type="checkbox" />
                Literatura clásica
              </label>
            </div>
          </fieldset>

          <hr className="my-4 border-gray-200" />

          <fieldset className="mb-6">
            <legend className="text-sm font-medium text-gray-600">
              RANGO DE PÁGINAS
            </legend>

            <input type="range" className="w-full" />

            <div className="flex justify-between text-xs text-gray-500 mt-1">
              <span>150</span>
              <span>450</span>
            </div>
          </fieldset>

          <hr className="my-4 border-gray-200" />

          <fieldset className="mb-6">
            <legend className="text-sm font-medium text-gray-600">
              RANGO DE FECHAS PARA RESERVAR
            </legend>

            <div className="flex gap-3 mt-2">
              <div className="flex flex-col w-1/2">
                <label className="text-xs text-gray-500">Desde</label>
                <input
                  type="date"
                  placeholder="mm/dd/yy"
                  className="border rounded-md px-2 py-1 text-sm"
                />
              </div>

              <div className="flex flex-col w-1/2">
                <label className="text-xs text-gray-500">Hasta</label>
                <input
                  type="date"
                  placeholder="mm/dd/yy"
                  className="border rounded-md px-2 py-1 text-sm"
                />
              </div>
            </div>
              
          </fieldset>
          
          <hr className="my-4 border-gray-200" />

          <div className="mb-6">
            <legend className="text-sm font-medium text-gray-600">
              DETALLES
            </legend>

            <div className="flex flex-col gap-2 mt-2">
              <label className="text-xs text-gray-500">ISBN</label>
              <input
                type="text"
                placeholder="978-3-16..."
                className="w-full border rounded-md px-3 py-2 text-sm mb-2"
              />
            </div>

            <div className="flex flex-col gap-2 mt-2">
              <label className="text-xs text-gray-500">Prestado por...</label>
              <input
                type="text"
                placeholder="Usuario..."
                className="w-full border rounded-md px-3 py-2 text-sm"
              />
            </div>

          </div>
          
          <button 
            type="submit"
            className="w-full bg-blue-100 text-blue-600 font-medium py-2 rounded-lg hover:bg-blue-200 cursor-pointer"
          >
            Aplicar Filtros
          </button>
        </form>
      </aside>

      <div className="flex flex-col gap-6 w-full">
        {/* Barra de busqueda */}
        <section className="bg-white p-8 rounded-xl">
          <header>
            <h1 className="text-3xl font-bold text-gray-800">
              Encuentra tu próxima lectura
            </h1>

            <p className="text-gray-500 mt-2">
              Explora miles de libros disponibles para préstamo en nuestra comunidad.
            </p>
          </header>

          <form className="mt-6">
            <div className="relative">
              <input
                type="search"
                placeholder="Buscar por título..."
                className="w-full border border-gray-300 rounded-md px-4 py-4 pr-10 focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
              <button
                type="submit"
                className="absolute cursor-pointer px-4 py-2 right-2 top-1/2 -translate-y-1/2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
              >
                Buscar
              </button>
            </div>
          </form>
        </section>

        {/* Header resultados */}
        <section className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-800">
            Libros Populares
          </h2>

          <div className="flex items-center gap-2 text-sm text-gray-500">
            <span>Ordenar por:</span>

            <select className="border rounded-md px-2 py-1 text-gray-700">
              <option>Relevancia</option>
              <option>Mejor valorados</option>
              <option>Más recientes</option>
            </select>
          </div>
        </section>

        {/* Lista del resultado de libros */}
        <div className="grid grid-cols-3 gap-6">
          <LibroCard />
          <LibroCard />
          <LibroCard />
        </div>
      </div>
    </main>
  )
}