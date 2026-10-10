import type { LibroDetalleDTO } from '../../types/DetalleReserva'
import { Calificacion } from '../Calificacion'

interface Props {
  libro: LibroDetalleDTO
  calificacion: number
}

const section = 'flex gap-8 rounded-2xl border border-stone-200 bg-white p-6'
const figure = 'shrink-0'
const img = 'h-60 w-40 rounded-sm object-cover shadow-lg shadow-stone-400/40'
const content = 'flex min-w-0 flex-col gap-3'
const metaRow = 'flex items-center gap-3'
const genreBadge = 'rounded-full bg-tinta-50 px-2.5 py-1 text-xs font-medium text-tinta-800'
const title = 'font-display text-4xl font-semibold leading-tight text-stone-900'
const authorName = 'text-base text-stone-600'
const synopsisTitle = 'mt-2 text-xs font-semibold uppercase tracking-wide text-stone-500'
const synopsisText = 'max-w-2xl text-sm leading-relaxed text-stone-700'

export const LibroHeader = ({ libro, calificacion }: Props) => {
  return (
    <section className={section}>
      <figure className={figure}>
        <img
          className={img}
          src={libro.portada}
          alt={`Imagen de portada del libro ${libro.titulo}`}
        />
      </figure>
      <div className={content}>
        <div className={metaRow}>
          <span className={genreBadge}>{libro.genero}</span>
          <Calificacion valor={calificacion} />
        </div>
        <div>
          <h1 className={title}>{libro.titulo}</h1>
        </div>
        <div>
          <p className={authorName}>de {libro.autor}</p>
        </div>
        <div>
          <h4 className={synopsisTitle}>Sinopsis</h4>
          <p className={synopsisText}>{libro.descripcion}</p>
        </div>
      </div>
    </section>
  )
}
