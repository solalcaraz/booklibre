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
    <>
      <LibroHeader libro={libro} calificacion={calificacion} />
      <LibroDetalle libro={libro} bibliokarmas={bibliokarmas}></LibroDetalle>
      <TuReserva
        libroId={libro.id}
        fechaDesde={fechaDesde}
        fechaHasta={fechaHasta}
        fechasOcupadas={fechasOcupadas}
        setFechaDesde={setFechaDesde}
        setFechaHasta={setFechaHasta}
      />
      <LibroResenias ultimasResenias={ultimasResenias} />
    </>
  )
}
