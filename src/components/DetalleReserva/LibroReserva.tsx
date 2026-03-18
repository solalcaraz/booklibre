import type { Dispatch, SetStateAction } from 'react'
import type {
  LibroDetalleDTO,
  RangosFechasOcupada,
  UltimasResenia
} from '../../types/DetalleReserva'
import { LibroDetalle } from './LibroDetalle'
import { LibroHeader } from './LibroHeader'
import { LibroResenias } from './LibroResenia'
import { TuReserva } from './TuReserva'

interface Props {
  libro: LibroDetalleDTO
  calificacion: number
  bibliokarmas: number
  ultimasResenias: UltimasResenia[]
  fechasOcupadas: RangosFechasOcupada[]
  fechaDesde: string
  fechaHasta: string
  setFechaDesde: Dispatch<SetStateAction<string>>
  setFechaHasta: Dispatch<SetStateAction<string>>
}

const main = 'max-w-5xl mx-auto px-6 py-8'
const header = 'mb-6'
const body = 'grid grid-cols-[300px_1fr] gap-6 items-start'
const right = 'space-y-6'

export const LibroReserva = ({
  libro,
  calificacion,
  bibliokarmas,
  ultimasResenias,
  fechasOcupadas,
  fechaDesde,
  fechaHasta,
  setFechaDesde,
  setFechaHasta
}: Props) => {
  return (
    <main className={main}>
      <div className={header}>
        <LibroHeader libro={libro} calificacion={calificacion} />
      </div>
      <div className={body}>
        <TuReserva
          libroId={libro.id}
          fechaDesde={fechaDesde}
          fechaHasta={fechaHasta}
          fechasOcupadas={fechasOcupadas}
          setFechaDesde={setFechaDesde}
          setFechaHasta={setFechaHasta}
        />
        <div className={right}>
          <LibroDetalle libro={libro} bibliokarmas={bibliokarmas}></LibroDetalle>
          <LibroResenias ultimasResenias={ultimasResenias} />
        </div>
      </div>
    </main>
  )
}
