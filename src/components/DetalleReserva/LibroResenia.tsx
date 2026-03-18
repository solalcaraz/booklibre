import type { UltimasResenia } from '../../types/DetalleReserva'

interface Props {
  ultimasResenias: UltimasResenia[]
}
const wrapper = 'bg-white rounded-2xl p-6 shadow-sm border border-slate-100'
const title = 'text-base font-semibold text-slate-700 mb-4'
const list = 'grid grid-cols-2 gap-4'
const card = 'bg-slate-50 rounded-xl p-4 space-y-2'
const header = 'flex items-center justify-between'
const name = 'text-sm font-semibold text-slate-700'
const rating = 'text-amber-400 text-xs'
const comment = 'text-sm text-slate-500 leading-relaxed'

export const LibroResenias = ({ ultimasResenias }: Props) => {
  return (
    <section className={wrapper}>
      <p className={title}>Reseñas de la Comunidad</p>
      <ul className={list}>
        {ultimasResenias.map((resenia) => (
          <li key={resenia.id} className={card}>
            <header className={header}>
              <p className={name}>{resenia.nombreUsuario}</p>
              <p className={rating}>
                {'★'.repeat(resenia.puntuacion)}
                {'☆'.repeat(5 - resenia.puntuacion)}
              </p>
            </header>
            <p className={comment}>{resenia.comentario}</p>
          </li>
        ))}
      </ul>
    </section>
  )
}
