import type { UltimasResenia } from '../../types/DetalleReserva'

interface Props {
  ultimasResenias: UltimasResenia[]
}
const wrapper = 'rounded-2xl border border-stone-200 bg-white p-6'
const title = 'mb-4 font-display text-lg font-semibold text-stone-900'
const list = 'grid grid-cols-2 gap-4'
const card = 'space-y-2 rounded-xl border border-stone-100 bg-papel p-4'
const header = 'flex items-center justify-between'
const name = 'text-sm font-semibold text-stone-800'
const rating = 'text-xs tracking-wider text-amber-500'
const comment = 'text-sm leading-relaxed text-stone-700'
const vacio = 'text-sm text-stone-500'

export const LibroResenias = ({ ultimasResenias }: Props) => {
  return (
    <section className={wrapper}>
      <h2 className={title}>Últimas reseñas</h2>
      {ultimasResenias.length === 0 && (
        <p className={vacio}>Todavía nadie lo reseñó. Las reseñas aparecen cuando quien lo reservó lo devuelve y lo califica.</p>
      )}
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
