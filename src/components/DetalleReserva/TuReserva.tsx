import { useState, type Dispatch, type SetStateAction } from 'react'
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

const section = 'bg-white rounded-2xl p-5 shadow-sm border border-slate-100 space-y-4 sticky top-6'
const title = 'text-base font-semibold text-slate-700 flex items-center gap-2'
const fieldWrapper = 'flex flex-col gap-1'
const fieldLabel = 'text-xs text-slate-500'
const input =
  'w-full text-sm border border-slate-200 rounded-lg px-3 py-2 text-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-200'
const detailRow = 'flex justify-between items-center text-sm'
const detailLabel = 'text-slate-500'
const detailDias = 'font-semibold text-sky-600'
const btnConfirm =
  'w-full py-3 rounded-xl text-sm font-bold transition bg-[#f97c5a] text-white hover:brightness-95 disabled:bg-slate-200 disabled:text-slate-400 disabled:cursor-not-allowed'
const alertError = 'text-xs text-red-500 text-center leading-snug'
const alertOk = 'text-xs text-emerald-600 text-center leading-snug'
const ocupadasTitle = 'text-xs font-semibold text-slate-400 uppercase tracking-wide'
const ocupadasList = 'space-y-1 mt-1'
const ocupadasItem = 'text-xs text-slate-500 bg-slate-50 px-3 py-1.5 rounded-lg'

export const TuReserva = ({
  libroId,
  fechaDesde,
  fechaHasta,
  fechasOcupadas,
  setFechaDesde,
  setFechaHasta
}: Props) => {
  const [confirmando, setConfirmando] = useState<boolean>(false)

  const nuevaReserva = new Reserva(libroId.toString(), fechaDesde, fechaHasta, fechasOcupadas)

  const handleSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault()
    if (!nuevaReserva.puedeConfirmar()) return
    setConfirmando(true)
    try {
      const reservaJSON = nuevaReserva.toJSON()
      await reservaService.crearReserva(reservaJSON)
    } catch (e) {
      console.log(e)
    } finally {
      setConfirmando(false)
    }
  }

  const getMensajeValidacion = () => {
    if (!nuevaReserva.fechasValidas()) {
      return {
        texto:
          'La fecha de recogida debe ser hoy o posterior y la devolucion posterior a la recogida.',
        tipo: 'error' as const
      }
    }
    if (!nuevaReserva.estaDisponible()) {
      return {
        texto: 'El libro no está disponible en el rango seleccionado',
        tipo: 'error' as const
      }
    }
    return {
      texto: `Disponible — ${nuevaReserva.calcularDias()} días.`,
      tipo: 'ok' as const
    }
  }

  const mensaje = getMensajeValidacion()

  return (
    <section className={section}>
      <h3 className={title}>Tu reserva 📅</h3>

      <form
        onSubmit={(e) => {
          handleSubmit(e)
        }}
      >
        <div className={fieldWrapper}>
          <label className={fieldLabel} htmlFor="desde">
            Recogida:{' '}
          </label>
          <input
            className={input}
            id="desde"
            type="date"
            value={fechaDesde}
            onChange={(e) => setFechaDesde(e.target.value)}
          />
        </div>

        <div className={fieldWrapper}>
          <label className={fieldLabel} htmlFor="hasta">
            Devolución:{' '}
          </label>
          <input
            className={input}
            id="hasta"
            type="date"
            value={fechaHasta}
            onChange={(e) => setFechaHasta(e.target.value)}
          />
        </div>
        <div className={detailRow}>
          <span className={detailLabel}>Duración</span>
          <span className={detailDias}>{nuevaReserva.calcularDias()} días</span>
        </div>

        <button
          type="submit"
          disabled={!nuevaReserva.puedeConfirmar() || confirmando}
          className={btnConfirm}
        >
          {confirmando ? 'confirmando tu reserva...' : 'Confirmar Reserva'}
        </button>

        {mensaje && (
          <p role="alert" className={mensaje.tipo === 'error' ? alertError : alertOk}>
            {mensaje.texto}
          </p>
        )}
      </form>

      <section>
        <h4 className={ocupadasTitle}>Fechas ocupadas</h4>
        <ul className={ocupadasList}>
          {fechasOcupadas.map((fecha, i) => (
            <li key={i} className={ocupadasItem}>
              {fecha.fechaDesde} — {fecha.fechaHasta}
            </li>
          ))}
        </ul>
      </section>
    </section>
  )
}
