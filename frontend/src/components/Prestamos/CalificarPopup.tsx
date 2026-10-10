import { useState } from 'react'
import type { ReseniaReq } from '../../models/ReseniaModel'
import type { ReservaResponseModel } from '../../models/ReservaModel'
import { reservaService } from '../../services/ReservaService'
import { getMensajeError, type ErrorResponse } from '../../utils/errorHandler'
import toast from 'react-hot-toast'

interface Props {
  open: boolean | null
  onClose: (reload: boolean) => void
  reserva: ReservaResponseModel
}

export function CalificarPopup({ open, onClose, reserva }: Props) {
  const [puntaje, setPuntaje] = useState(0)
  const [hover, setHover] = useState(0)
  const [comentario, setComentario] = useState('')
  const [mostrarError, setMostrarError] = useState(false)
  const [loading, setLoading] = useState(false)

  if (!open) return null

  const submit = async () => {
    if (puntaje === 0 || comentario.length === 0) {
      setMostrarError(true)
      return
    }

    setMostrarError(false)
    const payload: ReseniaReq = {
      reservaId: reserva.id,
      puntuacion: puntaje,
      comentario
    }

    setLoading(true)

    try {
      await reservaService.calificar(payload)
      toast.success('¡Gracias! Tu reseña ya se ve en el libro.')
      onClose(true)
    } catch (error: unknown) {
      const { message } = getMensajeError(error as ErrorResponse)
      toast.error(message)
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-stone-900/40">
      <div className="w-[420px] rounded-2xl bg-white p-6 shadow-xl">
        <p className="text-xs font-semibold uppercase tracking-wide text-stone-500">Calificar</p>
        <h2 className="mb-4 font-display text-xl font-semibold text-stone-900">{reserva.libroResumen.titulo}</h2>

        {mostrarError && (
          <p className="mb-3 text-sm text-red-700">Elegí una puntuación y escribí un comentario.</p>
        )}

        <div className="flex justify-center gap-2 mb-4">
          {[1,2,3,4,5].map((star) => (
            <span
              key={star}
              className={`text-3xl cursor-pointer transition ${
                star <= (hover || puntaje)
                  ? "text-amber-500"
                  : "text-stone-300"
              }`}
              onMouseEnter={() => setHover(star)}
              onMouseLeave={() => setHover(0)}
              onClick={() => setPuntaje(star)}
            >
              ★
            </span>
          ))}
        </div>

        <textarea
          placeholder="Contá qué te pareció. Lo van a leer quienes lo reserven después."
          value={comentario}
          onChange={(e) => setComentario(e.target.value)}
          className="mb-4 w-full resize-none rounded-lg border border-stone-200 bg-stone-50 p-3 text-sm focus:border-tinta-500 focus:bg-white focus:outline-none focus:ring-2 focus:ring-tinta-100"
          rows={4}
        />

        <div className="flex justify-end gap-3">
          <button onClick={() => onClose(false)} className="cursor-pointer rounded-lg px-4 py-2 text-sm font-medium text-stone-600 hover:bg-stone-100">
            Cancelar
          </button>

          <button
            onClick={submit}
            disabled={loading}
            className={`rounded-lg px-4 py-2 text-sm font-semibold text-white ${loading ? 'cursor-not-allowed bg-stone-400' : 'cursor-pointer bg-tinta-700 hover:bg-tinta-800'}`}
          >
            {loading ? 'Enviando…' : 'Guardar calificación'}
          </button>
        </div>
      </div>
    </div>
  )
}