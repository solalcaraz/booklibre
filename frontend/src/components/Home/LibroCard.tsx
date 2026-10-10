import { useNavigate } from "react-router"
import type { ReservaLibroHomeJSON } from "../../types/ReservaLibroHome"

interface LibroCardProps {
  libro: ReservaLibroHomeJSON
}

export const LibroCard = ( {libro}: LibroCardProps) => {
  const navigate = useNavigate()

  return (
    <article onClick={() => navigate(`/libros/detalle/${libro.id}`)} className="bg-white rounded-2xl shadow-[0_10px_30px_rgba(15,23,42,0.08)] overflow-hidden transition hover:shadow-[0_14px_40px_rgba(15,23,42,0.12)] cursor-pointer"
    data-testid={`libro-card-${libro.id}`}>

      {/* Imagen */}
      <div className="bg-slate-50 p-3 flex justify-center">
        <img
          src={libro.portada}
          alt="Libro"
          className="h-56 object-cover"
        />
      </div>

      {/* Contenido */}
      <div className="p-5 space-y-4">

        {/* género + rating */}
        <div className="flex justify-between items-center text-xs">
          <span className={`${generoEstilos[libro.genero]} px-2 py-1 rounded-full font-medium`}>
            {libro.genero}
          </span>

          <span className="text-amber-500 font-medium" data-testid="libro-calificacion">
            ⭐ {libro.calificacionPromedio ?? "-"}
          </span>
        </div>

        <h3 className="font-semibold text-slate-800 leading-snug" data-testid="libro-titulo">
          {libro.titulo}
        </h3>

        <p className="ttext-sm text-slate-500">
          {libro.autor}
        </p>

        <hr className="border-slate-100" />

        {/* info */}
        <div className="text-xs grid grid-cols-2 gap-y-3 text-slate-600">

          <div>
            <p className="text-slate-400">ISBN</p>
            <p className="truncate">{libro.isbn13}</p>
          </div>

          <div>
            <p className="text-slate-400">Idioma</p>
            <p className="truncate">{libro.idioma}</p>
          </div>

          <div>
            <p className="text-slate-400">Tipo</p>
            <p className="truncate">{libro.tipo}</p>
          </div>

          <div>
            <p className="text-slate-400">Bibliokarma</p>
            <p>+{libro.biblioKarmasTotal}</p>
          </div>

        </div>

        {/* estado */}
        <div className="text-xs">
          <p className="text-slate-400 mb-1">Estado</p>
          <span className={`px-2 py-1 rounded-full text-xs ${estadoEstilos[libro.estado]}`}>
            {libro.estado}
          </span>
        </div>

        <hr className="border-slate-100" />

        {/* usuario */}
        <div className="flex items-center gap-2 text-xs text-slate-500">
          <div className="w-6 h-6 bg-slate-300 rounded-full"></div>
          <span>@{libro.dueno}</span>
        </div>

      </div>
    </article>
  )
}

const generoEstilos: Record<string, string> = {
  "Ciencia Ficción": "bg-purple-100 text-violet-700",
  "Romance": "bg-rose-100 text-rose-900",
  "Drama": "bg-red-100 text-red-700",
  "Autoayuda": "bg-blue-100 text-blue-700",
  "Diseño": "bg-orange-100 text-orange-700",
  "Literatura Clásica": "bg-yellow-100 text-yellow-700"
}

const estadoEstilos: Record<string, string> = {
  "Excelente": "bg-green-100 text-green-700",
  "Muy bueno": "bg-lime-100 text-lime-700",
  "Bueno": "bg-blue-100 text-blue-700",
  "Regular": "bg-yellow-100 text-yellow-700",
  "Malo": "bg-red-100 text-red-700"
}