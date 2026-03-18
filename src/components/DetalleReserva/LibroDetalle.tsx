import type { LibroDetalleDTO } from '../../types/DetalleReserva'

interface Props {
  libro: LibroDetalleDTO
  bibliokarmas: number
}

const section = 'bg-white rounded-2xl p-6 shadow-sm border border-slate-100'
const title = 'text-base font-semibold text-slate-700 mb-5'
const grid = 'grid grid-cols-2 sm:grid-cols-4 gap-x-4 gap-y-5'
const item = 'flex flex-col gap-1'
const dt = 'text-[10px] uppercase tracking-widest text-slate-400 font-medium'
const dd = 'text-sm font-semibold text-slate-700'
const Bibliokarmas =
  'inline-flex w-fit bg-emerald-100 text-emerald-700 text-sm font-semibold px-2 py-0.5 rounded-md'

export const LibroDetalle = ({ libro, bibliokarmas }: Props) => {
  return (
    <section className={section}>
      <h3 className={title}>Detalles del libro</h3>
      <dl className={grid}>
        <div className={item}>
          <dt className={dt}>Tipo</dt> <dd className={dd}>{libro.tipo}</dd>
        </div>
        <div className={item}>
          <dt className={dt}>Género</dt> <dd className={dd}>{libro.genero}</dd>
        </div>
        <div className={item}>
          <dt className={dt}>ISBN-13</dt> <dd className={dd}>{libro.isbn13}</dd>
        </div>
        <div className={item}>
          <dt className={dt}>Páginas</dt> <dd className={dd}>{libro.paginas}</dd>
        </div>
        <div className={item}>
          <dt className={dt}>Publicado</dt> <dd className={dd}>{libro.fechaPublicacion}</dd>
        </div>
        <div className={item}>
          <dt className={dt}>Idioma</dt> <dd className={dd}>{libro.idioma}</dd>
        </div>
        <div className={item}>
          <dt className={dt}>Estado</dt> <dd className={dd}>{libro.estado}</dd>
        </div>
        <div className={item}>
          <dt className={dt}>Editorial</dt> <dd className={dd}>{libro.editorial}</dd>
        </div>
        <div className={item}>
          <dt className={dt}>Bibliokarmas</dt>
          <dd className={Bibliokarmas}>+{bibliokarmas}</dd>
        </div>
      </dl>
    </section>
  )
}
