import type { LibroHomeJSON } from "../classes/Libro"
import { formatEnum } from "../utils/formateEnumLibros"

interface LibroCardProps {
  libro: LibroHomeJSON
}

export const LibroCard = ( {libro}: LibroCardProps) => {
  return (
    <article className="bg-white rounded-2xl shadow-[0_10px_30px_rgba(15,23,42,0.08)] overflow-hidden transition hover:shadow-[0_14px_40px_rgba(15,23,42,0.12)]">

      {/* Imagen */}
      <div className="bg-slate-50 p-6 flex justify-center">
        <img
          src={libro.portada}
          alt="Libro"
          className="h-56 object-contain"
        />
      </div>

      {/* Contenido */}
      <div className="p-5 space-y-4">

        {/* género + rating */}
        <div className="flex justify-between items-center text-xs">
          <span className={`${generoEstilos[libro.genero]} text-sky-600 px-2 py-1 rounded-full font-medium`}>
            {formatEnum(libro.genero)}
          </span>

          <span className="text-amber-500 font-medium">
            ⭐ {libro.calificacionPromedio ?? "-"}
          </span>
        </div>

        {/* titulo */}
        <h3 className="font-semibold text-slate-800 leading-snug">
          {libro.titulo}
        </h3>

        {/* autor */}
        <p className="ttext-sm text-slate-500">
          {libro.autor}
        </p>

        <hr className="border-slate-100" />

        {/* info */}
        <div className="text-xs grid grid-cols-2 gap-y-3 text-slate-600">

          <div>
            <p className="text-slate-400">ISBN</p>
            <p>{libro.isbn13}</p>
          </div>

          <div>
            <p className="text-slate-400">Idioma</p>
            <p>{libro.idioma}</p>
          </div>

          <div>
            <p className="text-slate-400">Tipo</p>
            <p>{libro.tipo}</p>
          </div>

          <div>
            <p className="text-slate-400">Bibliokarma</p>
            <p>+340</p>
          </div>

        </div>

        {/* estado */}
        <div className="text-xs">
          <p className="text-slate-400 mb-1">Estado</p>
          <span className={`px-2 py-1 rounded-full text-xs ${estadoEstilos[libro.estado]}`}>
            {formatEnum(libro.estado)}
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
  CIENCIA_FICCION: "bg-purple-100 text-violet-700",
  ROMANCE: "bg-rose-100 text-stone-500",
  DRAMA: "bg-red-100 text-red-700",
  AUTOAYUDA: "bg-blue-100 text-blue-700",
  DISENO: "bg-orange-100 text-orange-700",
  LITERATURA_CLASICA: "bg-yellow-100 text-yellow-700"
}

const estadoEstilos: Record<string, string> = {
  EXCELENTE: "bg-green-100 text-green-700",
  MUY_BUENO: "bg-emerald-100 text-emerald-700",
  BUENO: "bg-blue-100 text-blue-700",
  REGULAR: "bg-yellow-100 text-yellow-700",
  MALO: "bg-red-100 text-red-700"
}