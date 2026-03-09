import { useParams } from 'react-router'
import { useOnInit } from '../utils/hooks'
import type { DetalleLibro } from '../types/DetalleLibro'
import { useState } from 'react'
import { libroService } from '../services/LibroService'

//TODO: Terminar de de pulir el backend y definir el dominio para traer lo que falta
//TODO: Pulir el los estilos. Terminar de entender bien taildwind. 
//TODO: Manejo de error. Hablar con el team. 

export const DetalleLibroPage = () => {
  const { id } = useParams<{ id: string }>()

  const [libro, setLibro] = useState<DetalleLibro | null>(null)
  const [error, setError] = useState<boolean>(false)

  useOnInit(() => {
    if (!id) return
    const fetchDetalleLibro = async () => {
      try {
        const data = await libroService.getDetalleLibroByID(id)
        setLibro(data)
      } catch (err) {
        console.log('error', err)
        setError(true)
      }
    }
    fetchDetalleLibro()
  })

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
            <span className="text-white font-bold">Portada de {libro.urlImagen}</span>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h3 className="font-bold text-lg mb-4">📅 Tu Reserva</h3>
            <p className="text-sm text-gray-500">Formulario en construcción...</p>
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
            <p>
              Publicado: {libro.fechaPublicacion.getDay()} {libro.fechaPublicacion.getMonth()}{' '}
              {libro.fechaPublicacion.getFullYear()}
            </p>
            <p>editorial: {libro.editorial}</p>
            <p>idioma: {libro.idioma}</p>
            <p>estado: {libro.estado}</p>
            <p>Ranking: {libro.calificacionPromedio} FALTA TRAER DEL BACKEND</p>
            <p>editorial: {libro.editorial}</p>
            <p>Bibliokarma: FALTA TRAER DEL BACKEND</p>
          </section>
        </main>
      </div>
    </div>
  )
}
