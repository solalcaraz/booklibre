import { useParams, useSearchParams } from 'react-router'
import { useOnInit } from '../utils/hooks'
import { useState } from 'react'
import { Spinner } from '../components/Spinner'
import { reservaService } from '../services/ReservaService'
import type { DetalleReserva } from '../types/DetalleReserva'
import { LibroReserva } from '../components/DetalleReserva/LibroReserva'

export const DetalleReservaPage = () => {
  //Capturo los datos pasados de la vista de sol.
  const { id } = useParams<{ id: string }>()
  const [searchParams] = useSearchParams()
  const fechaDesdeURL = searchParams.get('fechaDesde') || ''
  const fechaHastaURL = searchParams.get('fechaHasta') || ''
  const [fechaDesde, setFechaDesde] = useState(fechaDesdeURL)
  const [fechaHasta, setFechaHasta] = useState(fechaHastaURL)

  //Estado de la pagina.
  const [detalleReserva, setDetalleReserva] = useState<DetalleReserva | null>(null)
  const [error, setError] = useState<boolean>(false)
  const [loading, setLoading] = useState<boolean>(true)

  const getDetalleReserva = async () => {
    try {
      if (!id) {
        setError(true)
        return
      }
      const reserva = await reservaService.getDetalleLibroByID(id, fechaDesde, fechaHasta)
      setDetalleReserva(reserva)
    } catch (error) {
      setError(true)
      console.log(error)
    } finally {
      setLoading(false)
    }
  }

  useOnInit(getDetalleReserva)

  if (loading) return <Spinner />

  if (error || !detalleReserva) {
    return <div>Hubo un error al cargar la reserva del libro con id {id}</div>
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
