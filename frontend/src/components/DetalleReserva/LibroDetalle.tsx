import type { LibroDetalleDTO } from '../../types/DetalleReserva'
import { formatDate } from '../../utils/formateDate'
import { TIPO_OPTIONS } from '../../utils/formateEnumLibros'

interface Props {
  libro: LibroDetalleDTO
  bibliokarmas: number
}

const section = 'rounded-2xl border border-stone-200 bg-white p-6'
const title = 'mb-5 font-display text-lg font-semibold text-stone-900'
const grid = 'grid grid-cols-2 gap-x-4 gap-y-5 sm:grid-cols-4'
const item = 'flex flex-col gap-1'
const dt = 'text-xs text-stone-500'
const dd = 'text-sm font-medium text-stone-800'
const Bibliokarmas = 'cifra inline-flex w-fit rounded-full bg-lacre-50 px-2.5 py-0.5 text-sm font-semibold text-lacre-700'

export const LibroDetalle = ({ libro, bibliokarmas }: Props) => {
  return (
    <section className={section}>
      <h2 className={title}>Ficha del ejemplar</h2>
      <dl className={grid}>
        <div className={item}>
          <dt className={dt}>Tipo</dt> <dd className={dd}>{TIPO_OPTIONS[libro.tipo] ?? libro.tipo}</dd>
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
          <dt className={dt}>Publicado</dt>{' '}
          <dd className={dd}>{formatDate(libro.fechaPublicacion)}</dd>
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
          <dt className={dt}>Bibliokarma por las fechas buscadas</dt>
          <dd className={Bibliokarmas}>+{bibliokarmas}</dd>
        </div>
      </dl>
    </section>
  )
}
