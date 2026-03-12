import type { ErrorCustom } from '../utils/errorHandler'
import { ErrorIcon } from './ErrorIcon'

type Props = {
  error: ErrorCustom
  onRetry?: () => void
  onVolver?: () => void
}

export const ErrorCard = ({ error, onRetry, onVolver }: Props) => {
  return (
    <div className="flex items-center justify-center w-full h-[70vh] px-4">
      <div className="p-8 max-w-md text-center flex flex-col items-center justify-center">
        <ErrorIcon />
        <h2 className="text-sky-500 text-xl font-bold mb-5">
          {error.message} {error.status == 0 ? '' : `(${error.status})`}
        </h2>
        <div className="flex justify-center gap-2">
          <button
            onClick={onVolver}
            className="bg-gray-500 text-white font-semibold px-5 py-2 rounded-md hover:bg-gray-600 transition cursor-pointer mt-2"
          >
            Volver
          </button>
          <button
            onClick={onRetry}
            className="bg-sky-500 text-white font-semibold px-5 py-2 rounded-md hover:bg-sky-600 transition cursor-pointer mt-2"
          >
            Reintentar
          </button>
        </div>
      </div>
    </div>
  )
}
