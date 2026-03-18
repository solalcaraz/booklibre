import { useState } from 'react'
import type { ReseniaReq } from '../../models/ReseniaModel'
import type { ReservaResponseModel } from '../../models/ReservaModel'
import { reservaService } from '../../services/ReservaService'

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

  if (!open) return null

  const submit = () => {
    if (puntaje === 0 || comentario.length === 0){
      setMostrarError(true)
    } else {
      const payload: ReseniaReq = {
        reservaId: reserva.id,
        puntuacion: puntaje,
        comentario
      }
      reservaService.calificar(payload)
      onClose(true)
    }
  }

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl w-[420px] p-6">
        <h2 className="text-xl font-bold mb-4">Calificar {reserva.libroResumen.titulo}</h2>

        {mostrarError && (
          <div className='text-red-500'>
            Debe completar puntuacion y comentario
          </div> )
        }

        {/* ⭐ puntaje */}
        <div className="flex justify-center gap-2 mb-4">
          {[1,2,3,4,5].map((star) => (
            <span
              key={star}
              className={`text-3xl cursor-pointer transition ${
                star <= (hover || puntaje)
                  ? "text-yellow-400"
                  : "text-gray-300"
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
          placeholder="Escribí un comentario (opcional)"
          value={comentario}
          onChange={(e) => setComentario(e.target.value)}
          className="w-full border rounded-lg p-3 mb-4 resize-none focus:outline-none focus:ring-2 focus:ring-blue-400"
          rows={4}
        />

        <div className="flex justify-end gap-3">
          <button onClick={() => onClose(false)} className="px-4 py-2 rounded-lg bg-gray-200 hover:bg-gray-300" >
            Cancelar
          </button>

          <button onClick={submit} className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700" >
            Enviar
          </button>
        </div>
      </div>
    </div>
  )
}