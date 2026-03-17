import type { Dispatch, SetStateAction } from 'react'
import { Reserva } from '../../classes/Reserva'
import type { RangosFechasOcupada } from '../../types/DetalleReserva'
import { reservaService } from '../../services/ReservaService'

interface Props {
  libroId: number
  fechaDesde: string
  fechaHasta: string
  fechasOcupadas: RangosFechasOcupada[]
  setFechaDesde: Dispatch<SetStateAction<string>>
  setFechaHasta: Dispatch<SetStateAction<string>>
}
export const TuReserva = ({
  libroId,
  fechaDesde,
  fechaHasta,
  fechasOcupadas,
  setFechaDesde,
  setFechaHasta
}: Props) => {

  const nuevaReserva = new Reserva(libroId.toString(), fechaDesde, fechaHasta, fechasOcupadas)

  const onConfirmarReserva = async () => {
    if (!nuevaReserva.puedeConfirmar()) return
    const reservaJSON = nuevaReserva.toJSON()
    console.log(reservaJSON)
    await reservaService.crearReserva(reservaJSON)
  }

  return (
    <div className="border bg-blue-100">
      <h3>Tu reserva</h3>
      <p>fechas seleccionadas</p>

      <div>
        <label htmlFor="desde">Recogida: </label>
        <input
          id="desde"
          type="date"
          value={fechaDesde}
          onChange={(e) => setFechaDesde(e.target.value)}
        />
      </div>

      <div>
        <label htmlFor="hasta">Devolución: </label>
        <input
          id="hasta"
          type="date"
          value={fechaHasta}
          onChange={(e) => setFechaHasta(e.target.value)}
        />
      </div>
      <div>
        <p>
          Duracion: <span>{nuevaReserva.calcularDias()}</span>
        </p>
      </div>
      <button onClick={() => onConfirmarReserva()} className="bg-sky-500 hover:bg-sky-700">
        Confirmar Reserva
      </button>

      <h4>fechas ocupadas</h4>
      {fechasOcupadas.map((fecha, i) => (
        <li key={i}>
          {fecha.fechaDesde} - {fecha.fechaHasta}{' '}
        </li>
      ))}
    </div>
  )
}
