import type { LibroDetalleDTO } from '../../types/DetalleReserva'

interface Props {
  libro: LibroDetalleDTO
  calificacion: number
}

export const LibroHeader = ({ libro, calificacion }: Props) => {
  return (
    <section className="border bg-amber-600">
      <img src={libro.portada} alt={`Imagen de portada del libro ${libro.titulo}`} />
      <div>
        <p>
          {libro.genero} {calificacion}
        </p>
      </div>
      <div>
        <h2>{libro.titulo}</h2>
      </div>
      <div>
        <p>
          Autor: <span>{libro.autor}</span>
        </p>
      </div>
      <div className="border">
        <h4>Sinopsis</h4>
        <p>{libro.descripcion}</p>
      </div>
    </section>
  )
}
