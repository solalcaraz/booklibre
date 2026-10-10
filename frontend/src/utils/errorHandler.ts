import { AxiosError } from 'axios'

export const getMensajeError = (error: unknown): ErrorCustom => {
  let errorMessage = 'Ocurrió un error. Consulte al administrador del sistema'
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
        : 'Ocurrió un error al conectarse al backend. Intente nuevamente más tarde'
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
