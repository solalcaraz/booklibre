import { useNavigate, useParams, useSearchParams, type ErrorResponse } from 'react-router'
import { useOnInit } from '../utils/hooks'
import { useState } from 'react'
import { Spinner } from '../components/Spinner'
import { reservaService } from '../services/ReservaService'
import type { DetalleReserva } from '../types/DetalleReserva'
import { LibroReserva } from '../components/DetalleReserva/LibroReserva'
import { getMensajeError, type ErrorCustom } from '../utils/errorHandler'
import { ErrorCard } from '../components/ErrorCard'

export const DetalleReservaPage = () => {
  // Las fechas llegan desde el home en la URL para calcular los bibliokarmas de ese rango
  const { id } = useParams<{ id: string }>()
  const [searchParams] = useSearchParams()
  const fechaDesdeURL = searchParams.get('fechaDesde') || ''
  const fechaHastaURL = searchParams.get('fechaHasta') || ''
  const [fechaDesde, setFechaDesde] = useState(fechaDesdeURL)
  const [fechaHasta, setFechaHasta] = useState(fechaHastaURL)
  const navigate = useNavigate()

  const [detalleReserva, setDetalleReserva] = useState<DetalleReserva | null>(null)
  const [error, setError] = useState<ErrorCustom | null>(null)
  const [loading, setLoading] = useState<boolean>(true)

  const getDetalleReserva = async () => {
    try {
      if (!id) {
        setError({ message: 'No se encuentra ID' })
        return
      }
      const reserva = await reservaService.getDetalleLibroByID(id, fechaDesde, fechaHasta)
      setDetalleReserva(reserva)
    } catch (error) {
      setError(getMensajeError(error as ErrorResponse))
    } finally {
      setLoading(false)
    }
  }

  useOnInit(getDetalleReserva)

  if (loading) return <Spinner />

  if (error || !detalleReserva) {
    return (
      <ErrorCard
        error={error!}
        onRetry={() => window.location.reload()}
        onVolver={() => navigate(-1)}
      />
    )
  }

  const libro = detalleReserva.libroDetalleDTO

  return (
    <>
      <LibroReserva
        libro={libro}
        calificacion={detalleReserva.calificacionPromedio}
        bibliokarmas={detalleReserva.bibliokarmasTotales}
        ultimasResenias={detalleReserva.ultimasResenias}
        fechasOcupadas={detalleReserva.rangosFechasOcupadas}
        fechaDesde={fechaDesde}
        fechaHasta={fechaHasta}
        setFechaDesde={setFechaDesde}
        setFechaHasta={setFechaHasta}
      />
    </>
  )
}
