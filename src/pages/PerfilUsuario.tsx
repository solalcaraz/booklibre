import type { LibroUsuario } from '../components/PerfilUsuario/PerfilLibro'
import { usuarioService, type UsuarioResponse } from '../services/UsuarioService'
import { PerfilHeader } from '../components/PerfilUsuario/PerfilHeader'
import { PerfilInfoCard } from '../components/PerfilUsuario/PerfilInfoCard'
import { PerfilStats } from '../components/PerfilUsuario/PerfilStats'
import { PerfilGestorLibros } from '../components/PerfilUsuario/PerfilGestorLibros'
import { Spinner } from '../components/Spinner'
import { useOnInit } from '../utils/hooks'
import { useState } from 'react'

export const PerfilUsuario = () => {
  const [usuario, setUsuario] = useState<UsuarioResponse>()
  const [libros, setLibros] = useState<LibroUsuario[]>()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  const getUsuario = async () => {
    try {
      const usuario = await usuarioService.getUsuario()
      const libros = await usuarioService.getLibros()
      setUsuario(usuario)
      setLibros(libros)
    } catch (error: unknown) {
      setError(true)
      console.log(error)
    } finally {
      setLoading(false)
    }
  }

  useOnInit(getUsuario)

  if (loading) return <Spinner />

  if (error) return <div>exploto todo llamen a los bomberos</div>

  return (
    <main className="min-h-screen bg-[#f8f7f4] px-4 py-8 md:px-8">
      <div className="mx-auto flex max-w-6xl flex-col gap-6">
        <PerfilHeader
          nombre={usuario!.nombreCompleto}
          descripcion={usuario!.descripcion}
          unidoEn={`Se unió en ${usuario!.anoUnido}`}
          ciudad={usuario!.ciudad}
          bibliokarmas={`Bibliokarma ${usuario!.bibliokarmas}`}
          imagen={usuario!.imagen}
        />

        <section className="grid grid-cols-1 gap-6 lg:grid-cols-[280px_minmax(0,1fr)]">
          <aside className="space-y-6">
            <PerfilInfoCard
              email={usuario!.email}
              telefono={usuario!.telefono}
              tipoPerfil={usuario!.tipoUsuario}
            />
            <PerfilStats
              librosPrestados={usuario!.librosPrestados}
              librosLeidos={usuario!.librosLeidos}
            />
          </aside>

          <PerfilGestorLibros libros={libros!} />
        </section>
      </div>
    </main>
  )
}
