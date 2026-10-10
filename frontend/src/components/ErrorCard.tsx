import type { ErrorCustom } from '../utils/errorHandler'
import { ErrorIcon } from './ErrorIcon'

type Props = {
  error: ErrorCustom
  onRetry?: () => void
  onVolver?: () => void
}

export const ErrorCard = ({ error, onRetry, onVolver }: Props) => {
  return (
    <div className="flex items-center justify-center w-full h-[70vh] px-4" data-testid="error-card">
      <div className="p-8 max-w-md text-center flex flex-col items-center justify-center">
        <ErrorIcon />
        <h2 className="mb-5 font-display text-xl font-semibold text-stone-900">
          {error.message} {error.status == 0 ? '' : `(${error.status})`}
        </h2>
        <div className="flex justify-center gap-2">
          <button
            onClick={onVolver}
            className="mt-2 cursor-pointer rounded-md border border-stone-300 px-5 py-2 font-medium text-stone-700 transition hover:bg-stone-100"
            data-testid="volver-button"
          >
            Volver
          </button>
          <button
            onClick={onRetry}
            className="mt-2 cursor-pointer rounded-md bg-tinta-700 px-5 py-2 font-semibold text-white transition hover:bg-tinta-800"
            data-testid="reintentar-button"
          >
            Reintentar
          </button>
        </div>
      </div>
    </div>
  )
}
