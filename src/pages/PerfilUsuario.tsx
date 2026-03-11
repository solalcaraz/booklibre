import type { LibroUsuario } from '../components/PerfilUsuario/PerfilLibro'
import { usuarioService, type UsuarioResponse } from '../services/UsuarioService'
import { PerfilHeader } from '../components/PerfilUsuario/PerfilHeader'
import { PerfilInfoCard } from '../components/PerfilUsuario/PerfilInfoCard'
import { PerfilStats } from '../components/PerfilUsuario/PerfilStats'
import { PerfilGestorLibros } from '../components/PerfilUsuario/PerfilGestorLibros'

import { Spinner } from '../components/Spinner'
import { useOnInit } from '../utils/hooks'
import { useState } from 'react'
import type { EditProfileData } from '../components/PerfilUsuario/PerfilEditModal'

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

  const handleActualizarUsuario = async (userPatch: EditProfileData) => {
    try {
      await usuarioService.patchUsuario(userPatch)
      getUsuario()
    } catch (error: unknown) {
      setError(true)
      console.log(error)
    }
  }

  const handleDeleteLibro = async (id: number) => {
    try {
      await usuarioService.deleteLibro(id)
      setLibros((prev) => prev!.filter((book) => book.id !== id))
    } catch (error: unknown) {
      setError(true)
      console.log(error)
    }
  }

  useOnInit(getUsuario)

  if (loading) return <Spinner />

  if (error) return <div>exploto todo llamen a los bomberos</div>

  return (
    <main className="min-h-screen bg-[#f8f7f4] px-4 pb-8 md:px-8">
      <div className="mx-auto flex max-w-6xl flex-col gap-6">
        <PerfilHeader usuario={usuario!} onUpdate={handleActualizarUsuario} />

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

          <PerfilGestorLibros libros={libros!} onDelete={handleDeleteLibro} />
        </section>
      </div>
    </main>
  )
}
