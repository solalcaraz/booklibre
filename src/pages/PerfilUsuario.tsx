import type { LibroUsuario } from '../components/PerfilUsuario/PerfilLibro'
import { PerfilHeader } from '../components/PerfilUsuario/PerfilHeader'
import { PerfilInfoCard } from '../components/PerfilUsuario/PerfilInfoCard'
import { PerfilStats } from '../components/PerfilUsuario/PerfilStats'
import { PerfilGestorLibros } from '../components/PerfilUsuario/PerfilGestorLibros'

const mockLibros: LibroUsuario[] = [
  {
    id: 1,
    titulo: 'El Gran Gatsby',
    autor: 'F. Scott Fitzgerald',
    genero: 'Literatura Clásica',
    estado: 'Disponible',
    agregadoEl: '24 Oct, 2023',
    portada: 'bg-yellow-700'
  },
  {
    id: 2,
    titulo: 'Sistemas de Diseño',
    autor: 'Alla Kholmatova',
    genero: 'Diseño',
    estado: 'Prestado',
    agregadoEl: '12 Sep, 2023',
    portada: 'bg-emerald-600'
  },
  {
    id: 3,
    titulo: 'La Biblioteca de la Medianoche',
    autor: 'Matt Haig',
    genero: 'Ciencia Ficción',
    estado: 'Disponible',
    agregadoEl: '05 Ago, 2023',
    portada: 'bg-cyan-950'
  }
]

export const PerfilUsuario = () => {
  return (
    <main className="min-h-screen bg-[#f8f7f4] px-4 py-8 md:px-8">
      <div className="mx-auto flex max-w-6xl flex-col gap-6">
        <PerfilHeader
          nombre="Emilia Romero"
          descripcion="Lectora Ávida & Coleccionista"
          unidoEn="Se unió en 2021"
          ciudad="Gonzalez Catán, BA"
          bibliokarmas="Bibliokarma 2345"
        />

        <section className="grid grid-cols-1 gap-6 lg:grid-cols-[280px_minmax(0,1fr)]">
          <aside className="space-y-6">
            <PerfilInfoCard
              email="emily@example.com"
              telefono="+54 (11) 123-4567"
              tipoPerfil="Lector / Publicador"
            />
            <PerfilStats librosPrestados="12" librosLeidos="45" />
          </aside>

          <PerfilGestorLibros libros={mockLibros} />
        </section>
      </div>
    </main>
  )
}
