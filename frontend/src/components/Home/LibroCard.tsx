import type { ReservaLibroHomeJSON } from "../../types/ReservaLibroHome"
import { Calificacion } from "../Calificacion"

interface LibroCardProps {
  libro: ReservaLibroHomeJSON
}

// La tarjeta se lee como la ficha de un libro de biblioteca: portada, datos del ejemplar
// y, abajo, quién lo presta y cuánto bibliokarma suma reservarlo en las fechas buscadas
export const LibroCard = ({ libro }: LibroCardProps) => {
  return (
    <article
      className="group flex h-full cursor-pointer flex-col overflow-hidden rounded-2xl border border-stone-200 bg-white transition hover:-translate-y-0.5 hover:border-tinta-200 hover:shadow-md"
      data-testid={`libro-card-${libro.id}`}
    >
      <div className="flex justify-center bg-stone-100 px-6 py-5">
        <img
          src={libro.portada}
          alt={`Portada de ${libro.titulo}`}
          className="h-52 rounded-sm object-cover shadow-md shadow-stone-400/40 transition group-hover:shadow-lg"
        />
      </div>

      <div className="flex flex-1 flex-col p-5">
        <div className="flex items-center justify-between text-xs">
          <span className="rounded-full bg-tinta-50 px-2.5 py-1 font-medium text-tinta-800">{libro.genero}</span>
          <Calificacion valor={libro.calificacionPromedio} testId="libro-calificacion" />
        </div>

        <h3 className="mt-3 font-display text-lg font-semibold leading-snug text-stone-900" data-testid="libro-titulo">
          {libro.titulo}
        </h3>
        <p className="text-sm text-stone-600">{libro.autor}</p>

        <dl className="mt-4 grid grid-cols-3 gap-2 border-t border-dashed border-stone-200 pt-3 text-xs">
          <div>
            <dt className="text-stone-500">Tipo</dt>
            <dd className="mt-0.5 font-medium text-stone-800">{libro.tipo}</dd>
          </div>
          <div>
            <dt className="text-stone-500">Idioma</dt>
            <dd className="mt-0.5 font-medium text-stone-800">{libro.idioma}</dd>
          </div>
          <div>
            <dt className="text-stone-500">Estado</dt>
            <dd className="mt-0.5 flex items-center gap-1.5 font-medium text-stone-800">
              <span className={`h-1.5 w-1.5 rounded-full ${puntoEstado[libro.estado] ?? 'bg-stone-400'}`} />
              {libro.estado}
            </dd>
          </div>
        </dl>
        <p className="cifra mt-2 text-[11px] text-stone-500">ISBN {libro.isbn13}</p>

        <div className="mt-auto flex items-center justify-between gap-2 border-t border-stone-100 pt-4 text-xs">
          <span className="truncate text-stone-600">
            Lo presta <span className="font-medium text-stone-800">{libro.dueno}</span>
          </span>
          <span className="cifra shrink-0 rounded-full bg-lacre-50 px-2.5 py-1 font-semibold text-lacre-700">
            +{libro.biblioKarmasTotal}
          </span>
        </div>
      </div>
    </article>
  )
}

// Solo dos señales: verde si el ejemplar está muy bien conservado, ámbar si está gastado
const puntoEstado: Record<string, string> = {
  "Excelente": "bg-emerald-600",
  "Muy bueno": "bg-emerald-600",
  "Bueno": "bg-stone-400",
  "Regular": "bg-amber-500",
  "Malo": "bg-amber-500"
}
