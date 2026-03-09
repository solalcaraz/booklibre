export const LibroCard = () => {
  return (
    <article className="bg-white rounded-xl shadow-md overflow-hidden w-72">

      {/* Imagen */}
      <div className="bg-gray-100 p-6 flex justify-center">
        <img
          src="/book.jpg"
          alt="Libro"
          className="h-56 object-contain"
        />
      </div>

      {/* Contenido */}
      <div className="p-4 space-y-3">

        {/* género + rating */}
        <div className="flex justify-between items-center text-xs">
          <span className="bg-blue-100 text-blue-600 px-2 py-1 rounded-full">
            Ciencia ficción
          </span>

          <span className="text-yellow-500">
            ⭐ 4.8
          </span>
        </div>

        {/* titulo */}
        <h3 className="font-semibold text-gray-800">
          La Biblioteca de la Medianoche
        </h3>

        {/* autor */}
        <p className="text-sm text-gray-500">
          Matt Haig
        </p>

        <hr />

        {/* info */}
        <div className="text-xs grid grid-cols-2 gap-y-2">

          <div>
            <p className="text-gray-400">ISBN</p>
            <p>978-0-525-55947-4</p>
          </div>

          <div>
            <p className="text-gray-400">Idioma</p>
            <p>Español</p>
          </div>

          <div>
            <p className="text-gray-400">Tipo</p>
            <p>Libro común</p>
          </div>

          <div>
            <p className="text-gray-400">Bibliokarma</p>
            <p>+340</p>
          </div>

        </div>

        {/* estado */}
        <div className="text-xs">
          <p className="text-gray-400">Estado</p>
          <span className="bg-green-100 text-green-700 px-2 py-1 rounded-full">
            Bueno
          </span>
        </div>

        <hr />

        {/* usuario */}
        <div className="flex items-center gap-2 text-xs text-gray-500">
          <div className="w-6 h-6 bg-gray-300 rounded-full"></div>
          <span>@sarahreads</span>
        </div>

      </div>
    </article>
  )
}