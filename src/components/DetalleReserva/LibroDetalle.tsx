import type { LibroDetalleDTO } from '../../types/DetalleReserva'

interface Props {
  libro: LibroDetalleDTO
  bibliokarmas: number
}

export const LibroDetalle = ({ libro, bibliokarmas }: Props) => {
  return (
    <>
      <section className="bg-amber-400">
        <h3>Detalles del libro</h3>
        <p>Tipo <span>{libro.tipo}</span></p>
        <p>genero <span>{libro.genero}</span></p>
        <p>isbn13 <span>{libro.isbn13}</span></p>
        <p>paginas <span>{libro.paginas}</span></p>
        <p>publicado <span>{libro.fechaPublicacion}</span></p>
        <p>idioma <span>{libro.idioma}</span></p>
        <p>estado <span>{libro.estado}</span></p>
        <p>editorial <span>{libro.editorial}</span></p>
        <p>Bibliokarmas <span>+{bibliokarmas}</span></p>
      </section>
    </>
  )
}
