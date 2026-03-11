import { useParams } from 'react-router'
import { useOnInit } from '../utils/hooks'
import type { DetalleLibro } from '../types/DetalleLibro'
import { useState } from 'react'
import { libroService } from '../services/LibroService'
import { Spinner } from '../components/Spinner'

//TODO: Componentizar. 

export const DetalleLibroPage = () => {
  const { id } = useParams<{ id: string }>()

  const [libro, setLibro] = useState<DetalleLibro | null>(null)
  const [error, setError] = useState<boolean>(false)
  const [loading, setLoading] = useState<boolean>(true)

  const getDetalleLibro = async () => {
    try {
      if (!id) {
        setError(true)
        return
      }
      const libro = await libroService.getDetalleLibroByID(id)
      setLibro(libro)
    } catch (error) {
      setError(true)
      console.log(error)
    } finally {
      setLoading(false)
    }
  }

  useOnInit(getDetalleLibro)

  if (loading) return <Spinner />

  if (error) {
    return <div>Hubo un error al cargar el libro con id {id}</div>
  }

  if (!libro) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <p className="text-xl font-bold text-gray-500">Cargando libro...</p>
      </div>
    )
  }
  return (
    <div className="max-w-7xl mx-auto px-4 py-8 bg-gray-50 min-h-screen font-sans">
      {/* Breadcrumb temporal */}
      <nav className="text-sm text-gray-500 mb-6">
        Explorar &gt; <span className="font-bold text-gray-900">{libro.titulo}</span>
      </nav>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* COLUMNA IZQUIERDA */}
        <aside className="lg:col-span-4 flex flex-col gap-6">
          <div className="bg-black rounded-xl h-96 shadow-md border-8 border-black flex items-center justify-center">
            {/* Placeholder temporal hasta que mapeemos la imagen de tu JSON */}
            <img className="text-white font-bold" src={libro.urlImagen} alt="" />
          </div>

          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h3 className="font-bold text-lg mb-4">📅 Tu Reserva</h3>
            <fieldset className="mb-6">
              <legend className="text-sm font-medium text-gray-600">
                RANGO DE FECHAS PARA RESERVAR
              </legend>

              <div className="flex gap-3 mt-2">
                <div className="flex flex-col w-1/2">
                  <label className="text-xs text-gray-500">Desde</label>
                  <input
                    type="date"
                    placeholder="mm/dd/yy"
                    className="border rounded-md px-2 py-1 text-sm"
                  />
                </div>

                <div className="flex flex-col w-1/2">
                  <label className="text-xs text-gray-500">Hasta</label>
                  <input
                    type="date"
                    placeholder="mm/dd/yy"
                    className="border rounded-md px-2 py-1 text-sm"
                  />
                </div>
              </div>
            </fieldset>
            <button className="btn">Confirmar</button>
          </div>
        </aside>

        {/* COLUMNA DERECHA */}
        <main className="lg:col-span-8 flex flex-col gap-8">
          <section>
            {/* INYECTAMOS LOS DATOS DEL ESTADO */}
            <h1 className="text-4xl font-extrabold text-gray-900 mb-2">{libro.titulo}</h1>

            <p className="text-gray-500 font-medium mb-4">Foto autor: FALTA TRAER DEL BACKEND</p>
            <p className="text-gray-500 font-medium mb-4">Por: {libro.autor}</p>
            <p className="text-gray-700 leading-relaxed">{libro.descripcion}</p>
          </section>

          <section className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h3 className="font-bold text-lg mb-4 border-b pb-2">Detalles del Libro</h3>
            <p>Tipo: {libro.tipo}</p>
            <p>ISBN-13: {libro.isbn13}</p>
            <p>Páginas: {libro.cantPaginas}</p>
            <p>Editorial: {libro.editorial}</p>
            <p>Publicado: {libro.fechaPublicacion}</p>
            <p>idioma: {libro.idioma}</p>
            <p>estado: {libro.estado}</p>
            <p>editorial: {libro.editorial}</p>
          </section>

          <section className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h3 className="font-bold text-lg mb-4 border-b pb-2">Reseñas</h3>
            {/* mmm algo nada mal...*/}
            {libro.resenias.map((resenia) => (
              <article key={resenia.comentario}>
                <p>{resenia.nombreUsuario}</p>
                <p>{resenia.comentario}</p>
                <p>{resenia.puntuacion}</p>
              </article>
            ))}
          </section>
        </main>
      </div>
    </div>
  )
}
