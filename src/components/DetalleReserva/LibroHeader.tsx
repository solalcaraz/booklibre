import type { LibroDetalleDTO } from '../../types/DetalleReserva'

interface Props {
  libro: LibroDetalleDTO
  calificacion: number
}

const section = 'flex gap-6 bg-white rounded-2xl p-6 shadow-sm border border-slate-100'
const figure = 'shrink-0'
const img = 'w-40 h-56 object-cover rounded-xl shadow-md'
const content = 'flex flex-col gap-3 min-w-0'
const metaRow = 'flex items-center gap-3'
const genreBadge =
  'bg-sky-100 text-sky-700 text-[11px] font-semibold px-3 py-1 rounded-full uppercase tracking-wide'
const rating = 'text-amber-400 text-sm font-semibold'
const title = 'text-3xl font-bold text-slate-800 leading-tight'
const authorLabel = 'text-[10px] text-slate-400 uppercase tracking-widest'
const authorName = 'text-sm font-semibold text-slate-700'
const synopsisTitle = 'text-sm font-semibold text-slate-700'
const synopsisText = 'text-sm text-slate-500 leading-relaxed'

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
          <span className={rating}>
            {'★'.repeat(Math.round(calificacion))}
            {'☆'.repeat(5 - Math.round(calificacion))} ({calificacion})
          </span>
        </div>
        <div>
          <h2 className={title}> {libro.titulo}</h2>
        </div>
        <div>
          <p className={authorLabel}>Autor</p>
          <p className={authorName}>{libro.autor}</p>
        </div>
        <div>
          <h4 className={synopsisTitle}>Sinopsis</h4>
          <p className={synopsisText}>{libro.descripcion}</p>
        </div>
      </div>
    </section>
  )
}
