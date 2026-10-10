import { useState, type Dispatch, type SetStateAction } from 'react'
import { Reserva } from '../../classes/Reserva'
import type { RangosFechasOcupada } from '../../types/DetalleReserva'
import { reservaService } from '../../services/ReservaService'
import { getMensajeError, type ErrorCustom } from '../../utils/errorHandler'
import { useNavigate, type ErrorResponse } from 'react-router-dom'
import { ErrorCard } from '../ErrorCard'
import { usuarioService } from '../../services/UsuarioService'
import { useUser } from '../../contexts/UserContext'

interface Props {
  libroId: number
  fechaDesde: string
  fechaHasta: string
  fechasOcupadas: RangosFechasOcupada[]
  setFechaDesde: Dispatch<SetStateAction<string>>
  setFechaHasta: Dispatch<SetStateAction<string>>
}

const section = 'sticky top-6 space-y-4 rounded-2xl border border-stone-200 bg-white p-5'
const title = 'font-display text-lg font-semibold text-stone-900'
const fieldWrapper = 'mb-3 flex flex-col gap-1'
const fieldLabel = 'text-xs font-medium text-stone-600'
const input =
  'w-full rounded-lg border border-stone-200 bg-stone-50 px-3 py-2 text-sm text-stone-800 focus:border-tinta-500 focus:bg-white focus:outline-none focus:ring-2 focus:ring-tinta-100'
const detailRow = 'mb-3 flex items-center justify-between border-t border-dashed border-stone-200 pt-3 text-sm'
const detailLabel = 'text-stone-600'
const detailDias = 'cifra font-semibold text-stone-900'
const alertError = 'text-center text-xs leading-snug text-red-700'
const alertOk = 'text-center text-xs leading-snug text-emerald-700'
const ocupadasTitle = 'text-xs font-semibold uppercase tracking-wide text-stone-500'
const ocupadasList = 'mt-1 space-y-1'
const ocupadasItem = 'cifra rounded-lg bg-stone-50 px-3 py-1.5 text-xs text-stone-700'
const btnBase =
  'w-full cursor-pointer rounded-xl py-3 text-sm font-semibold text-white transition disabled:cursor-not-allowed'
const btnExito = 'bg-emerald-700'
const btnNormal = 'mb-3 bg-lacre-600 hover:bg-lacre-700 disabled:bg-stone-200 disabled:text-stone-500'

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
  const { updateUser } = useUser()

  const nuevaReserva = new Reserva(libroId.toString(), fechaDesde, fechaHasta, fechasOcupadas)
  const btnClassName = `${btnBase} ${exito ? btnExito : btnNormal}`

  const handleSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault()
    if (!nuevaReserva.puedeConfirmar()) return
    setConfirmando(true)
    try {
      await reservaService.crearReserva(nuevaReserva.toJSON())
      const bibliokarmas = await usuarioService.getBibliokarmas()
      updateUser({ bibliokarmas })

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
          'La fecha de recogida debe ser hoy o posterior, y la devolución, posterior a la recogida.',
        tipo: 'error' as const
      }
    }
    if (!nuevaReserva.estaDisponible()) {
      return {
        texto: 'El libro no está disponible en el rango seleccionado.',
        tipo: 'error' as const
      }
    }
    return {
      texto: 'Disponible en esas fechas.',
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
      <h2 className={title}>Tu reserva</h2>

      <form
        onSubmit={(e) => {
          handleSubmit(e)
        }}
      >
        <div className={fieldWrapper}>
          <label className={fieldLabel} htmlFor="desde">
            Lo retirás el
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
            Lo devolvés el
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
          <span className={detailDias}>
            {nuevaReserva.calcularDias()} {nuevaReserva.calcularDias() === 1 ? 'día' : 'días'}
          </span>
        </div>

        <button
          type="submit"
          disabled={!nuevaReserva.puedeConfirmar() || confirmando || exito}
          className={btnClassName}
        >
          {exito
            ? 'Reserva confirmada. Te llevamos a tus préstamos…'
            : confirmando
              ? 'Confirmando…'
              : 'Confirmar reserva'}
        </button>

        {mensaje && (
          <p role="alert" className={mensaje.tipo === 'error' ? alertError : alertOk}>
            {mensaje.texto}
          </p>
        )}
      </form>

      {fechasOcupadas.length > 0 && (
        <section>
          <h3 className={ocupadasTitle}>Ya reservado</h3>
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
