import { useEffect, useState } from 'react'
import type { LibroUsuario } from '../components/PerfilUsuario/PerfilLibro'
import type { EditProfileData } from '../components/PerfilUsuario/PerfilEditModal'
import { usuarioService, type BookFilter, type UsuarioResponse } from '../services/UsuarioService'
import { PerfilHeader } from '../components/PerfilUsuario/PerfilHeader'
import { PerfilInfoCard } from '../components/PerfilUsuario/PerfilInfoCard'
import { PerfilStats } from '../components/PerfilUsuario/PerfilStats'
import { PerfilGestorLibros, type Pagination } from '../components/PerfilUsuario/PerfilGestorLibros'
import { Spinner } from '../components/Spinner'
import { ErrorCard } from '../components/ErrorCard'
import { getMensajeError, type ErrorCustom, type ErrorResponse } from '../utils/errorHandler'
import { useNavigate } from 'react-router'

export const PerfilUsuario = () => {
  const [usuario, setUsuario] = useState<UsuarioResponse>()
  const [libros, setLibros] = useState<LibroUsuario[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<ErrorCustom | null>(null)
  const [filter, setFilter] = useState<BookFilter>('Todos')
  const [pagination, setPagination] = useState<Pagination>({
    page: 0,
    totalPages: 0,
    size: 0,
    totalItems: 0
  })
  const navigate = useNavigate()

  const getLibros = async () => {
    try {
      const libros = await usuarioService.getLibros(pagination.page, filter)
      setLibros(libros.items)
      setPagination({
        page: libros.page,
        totalPages: libros.totalPages,
        size: libros.size,
        totalItems: libros.totalItems
      })
    } catch (error: unknown) {
      setError(getMensajeError(error as ErrorResponse))
      console.log(error)
    } finally {
      setLoading(false)
    }
  }

  const getUsuario = async () => {
    try {
      const usuario = await usuarioService.getUsuario()
      setUsuario(usuario)
    } catch (error: unknown) {
      setError(getMensajeError(error as ErrorResponse))
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
      setError(getMensajeError(error as ErrorResponse))
      console.log(error)
    }
  }

  const handleDeleteLibro = async (id: number) => {
    try {
      await usuarioService.deleteLibro(id)
      getLibros()
    } catch (error: unknown) {
      setError(getMensajeError(error as ErrorResponse))
      console.log(error)
    }
  }

  const handleFilterChange = (newFilter: BookFilter) => {
    /* cuando cambia el filtro, reinicia a página 0 */
    setFilter(newFilter)
    setPagination((prev) => ({
      ...prev,
      page: 0
    }))
  }

  useEffect(() => {
    getUsuario()
    getLibros()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagination.page, filter])

  if (loading) return <Spinner />

  if (error)
    return (
      <ErrorCard
        error={error}
        onRetry={() => window.location.reload()}
        onVolver={() => navigate(-1)}
      />
    )

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

          <PerfilGestorLibros
            libros={libros!}
            onDelete={handleDeleteLibro}
            pagination={pagination}
            setPagination={setPagination}
            handleFilter={handleFilterChange}
            filter={filter}
          />
        </section>
      </div>
    </main>
  )
}
