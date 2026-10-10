import { AxiosError } from 'axios'

export const getMensajeError = (error: unknown): ErrorCustom => {
  let errorMessage = 'Algo falló. Probá de nuevo en un rato.'
  let status = 0

  if (error instanceof Error) {
    errorMessage = error.message
  }

  if (error instanceof AxiosError) {
    status = error.response?.status ?? error.status ?? 0
    // En los 4xx el back explica qué falló (validación, permisos, conflicto): se muestra tal cual
    errorMessage =
      status >= 400 && status < 500
        ? error.response?.data?.message || 'No se pudo completar la operación'
        : 'No pudimos conectarnos con el servidor. Probá de nuevo en un rato.'
  }

  return {
    status: status,
    message: errorMessage
  }
}

export type ErrorResponse = {
  response: {
    status?: number
    data: {
      message: string
    }
  }
}

export type ErrorCustom = {
  status?: number
  message: string
}
