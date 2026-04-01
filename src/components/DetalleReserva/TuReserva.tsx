import { useState, type Dispatch, type SetStateAction } from 'react'
import { Reserva } from '../../classes/Reserva'
import type { RangosFechasOcupada } from '../../types/DetalleReserva'
import { reservaService } from '../../services/ReservaService'
import { getMensajeError, type ErrorCustom } from '../../utils/errorHandler'
import { useNavigate, type ErrorResponse } from 'react-router-dom'
import { ErrorCard } from '../ErrorCard'

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
const fieldWrapper = 'flex flex-col gap-1 mb-3'
const fieldLabel = 'text-xs text-slate-500'
const input =
  'w-full text-sm border border-slate-200 rounded-lg px-3 py-2 text-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-200'
const detailRow = 'flex justify-between items-center text-sm mb-3'
const detailLabel = 'text-slate-500'
const detailDias = 'font-semibold text-sky-600'
const alertError = 'text-xs text-red-500 text-center leading-snug'
const alertOk = 'text-xs text-emerald-600 text-center leading-snug'
const ocupadasTitle = 'text-xs font-semibold text-slate-400 uppercase tracking-wide'
const ocupadasList = 'space-y-1 mt-1'
const ocupadasItem = 'text-xs text-slate-500 bg-slate-50 px-3 py-1.5 rounded-lg'
const btnBase =
  'w-full py-3 rounded-xl text-sm font-bold transition text-white hover:brightness-95 disabled:cursor-not-allowed'
const btnExito = 'bg-emerald-500'
const btnNormal = 'bg-[#f97c5a] disabled:bg-slate-200 disabled:text-slate-400 mb-3'

export const TuReserva = ({
  libroId,
  fechaDesde,
  fechaHasta,
  fechasOcupadas,
  setFechaDesde,
  setFechaHasta
}: Props) => {
  const [confirmando, setConfirmando] = useState<boolean>(false)
  const [error, setError] = useState<ErrorCustom | null>(null)
  const [exito, setExito] = useState<boolean>(false)
  const navigate = useNavigate()

  const nuevaReserva = new Reserva(libroId.toString(), fechaDesde, fechaHasta, fechasOcupadas)
  const btnClassName = `${btnBase} ${exito ? btnExito : btnNormal}`

  const handleSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault()
    if (!nuevaReserva.puedeConfirmar()) return
    setConfirmando(true)
    try {
      const reservaJSON = nuevaReserva.toJSON()
      await reservaService.crearReserva(reservaJSON)
      setExito(true)
      setTimeout(() => {
        navigate('/prestamos')
      }, 2000)
    } catch (error) {
      setError(getMensajeError(error as ErrorResponse))
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

  if (error) {
    return (
      <ErrorCard
        error={error}
        onRetry={() => window.location.reload()}
        onVolver={() => navigate(-1)}
      />
    )
  }
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
          disabled={!nuevaReserva.puedeConfirmar() || confirmando || exito}
          className={btnClassName}
        >
          {exito
            ? '¡Reserva Exitosa! Redirigiendo...'
            : confirmando
              ? 'Confirmando tu reserva...'
              : 'Confirmar Reserva'}
        </button>

        {mensaje && (
          <p role="alert" className={mensaje.tipo === 'error' ? alertError : alertOk}>
            {mensaje.texto}
          </p>
        )}
      </form>

      {fechasOcupadas.length > 0 && (
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
      )}
    </section>
  )
}
