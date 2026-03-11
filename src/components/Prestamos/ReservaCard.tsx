import type { ReservaResponseModel } from '../../models/ReservaModel'

interface Props {
  reserva: ReservaResponseModel
}

export const ReservasCard = ({ reserva }: Props) => {
  
  return (
    <div className="bg-white rounded-2xl shadow-sm overflow-hidden w-full max-w-xs">

      <div className="relative bg-gradient-to-br from-teal-900 to-teal-700 p-6 flex justify-center">
        <img
          src={reserva.libroResumen.imgUrl}
          alt={reserva.libroResumen.titulo}
          className="h-56 object-contain drop-shadow-xl"
        />

        <span className="absolute top-3 right-3 bg-blue-500 text-white text-xs font-semibold px-3 py-1 rounded-full">
          {'COMPLETAR'}
        </span>
      </div>

      <div className="p-5">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="font-bold text-xl text-gray-900 leading-tight">
              {reserva.libroResumen.titulo}
            </h3>
            <p className="text-gray-500 text-sm">{reserva.libroResumen.autor}</p>
          </div>
          <div className="flex items-center gap-1 text-sm text-gray-700">
            ⭐
            <span>{reserva.libroResumen.puntuacion}</span>
          </div>
        </div>

        <div className="border-t my-4"></div>

        <div className="space-y-2 text-sm text-gray-600">
          <div className="flex items-center gap-2">
            <span>👤</span>
            <span>
              <strong>Prestado por:</strong> {reserva.prestadoPor}
            </span>
          </div>

          <div className="flex items-center gap-2">
            <span>📅</span>
            <span>
              <strong>Rango:</strong> {reserva.fechaDesde} - {reserva.fechaHasta}
            </span>
          </div>
        </div>

        <div className="mt-4 text-xs text-gray-400 font-semibold">
          BIBLOKARMA
          <div className="text-gray-600 text-sm">+{reserva.bibliokarma}</div>
        </div>
      </div>
    </div>
  )
}
