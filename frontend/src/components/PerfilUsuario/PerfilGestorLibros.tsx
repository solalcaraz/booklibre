import { useEffect, type Dispatch, type SetStateAction } from 'react'
import { Link } from 'react-router-dom'
import {
  ArrowsDownUpIcon,
  PlusCircleIcon,
  CaretLeftIcon,
  CaretRightIcon
} from '@phosphor-icons/react'
import type { LibroUsuario, FiltroLibros } from '../../services/UsuarioService'
import type { PaginationState } from '../../models/PaginationModel'
import { PerfilLibro } from './PerfilLibro'
import { PerfilTab } from './PerfilTab'
import { authService } from '../../services/AuthService'

type GestorLibrosProps = {
  libros: LibroUsuario[]
  pagination: PaginationState
  setPagination: Dispatch<SetStateAction<PaginationState>>
  onDelete: (id: number) => Promise<void>
  filter: string
  handleFilter: (newFilter: FiltroLibros) => void
  handleSort: (field: string) => void
}

export const PerfilGestorLibros = ({
  libros,
  onDelete,
  setPagination,
  pagination,
  filter,
  handleFilter,
  handleSort
}: GestorLibrosProps) => {
  const start = pagination.totalItems === 0 ? 0 : pagination.page * pagination.size + 1
  const end = Math.min((pagination.page + 1) * pagination.size, pagination.totalItems)

  const handlePreviousPage = () => {
    setPagination((prev) => ({
      ...prev,
      page: Math.max(prev.page - 1, 0)
    }))
  }

  const handleNextPage = () => {
    setPagination((prev) => ({
      ...prev,
      page: Math.min(prev.page + 1, prev.totalPages - 1)
    }))
  }

  useEffect(() => {
    if (start > end) {
      handlePreviousPage()
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [end, start])

  return (
    <section className="space-y-4" data-testid="perfil-gestor-libros">
      <div className="flex flex-col justify-between md:flex-row md:items-center">
        <h2 className="mb-3 font-display text-2xl font-semibold text-stone-900">
          Mis libros
        </h2>
        {authService.puedePublicar() && (
          <Link
            className="inline-flex items-center justify-center gap-2 cursor-pointer rounded-xl bg-tinta-700 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-tinta-800"
            to="/libro/nuevo"
          >
            <span>
              <PlusCircleIcon size={20} />
            </span>
            Publicar un libro
          </Link>
        )}
      </div>

      <div className="rounded-2xl border border-stone-200 bg-white">
        <div className="flex flex-wrap gap-2 border-b border-stone-200 px-4 py-4">
          <PerfilTab
            label="Todos"
            active={filter === 'Todos'}
            onClick={() => handleFilter('Todos')}
          />
          <PerfilTab
            label="Disponibles"
            active={filter === 'Disponibles'}
            onClick={() => handleFilter('Disponibles')}
          />
          <PerfilTab
            label="Prestados"
            active={filter === 'Prestados'}
            onClick={() => handleFilter('Prestados')}
          />
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full text-left">
            <thead>
              <tr className="border-b border-stone-200 text-xs text-stone-500">
                <th className="px-5 py-4 font-semibold">
                  <button
                    className="flex items-center gap-1 cursor-pointer font-semibold transition hover:text-stone-800"
                    onClick={() => handleSort('titulo')}
                  >
                    <ArrowsDownUpIcon size={18} />
                    Título y autor
                  </button>
                </th>
                <th className="px-5 py-4 font-semibold">
                  <button
                    className="flex items-center gap-1 cursor-pointer font-semibold transition hover:text-stone-800"
                    onClick={() => handleSort('disponible')}
                  >
                    <ArrowsDownUpIcon size={18} />
                    Disponibilidad
                  </button>
                </th>
                <th className="px-5 py-4 font-semibold">
                  <button
                    className="flex items-center gap-1 cursor-pointer font-semibold transition hover:text-stone-800"
                    onClick={() => handleSort('agregado')}
                  >
                    <ArrowsDownUpIcon size={18} />
                    Publicado
                  </button>
                </th>
                <th className="px-5 py-4 font-semibold">Clicks</th>
                <th className="px-5 py-4 text-center font-semibold">Acciones</th>
              </tr>
            </thead>

            <tbody>
              {authService.puedePublicar() &&
                libros.map((libro) => (
                  <PerfilLibro key={libro.id} libro={libro} onDelete={onDelete} />
                ))}
            </tbody>
          </table>
          {!authService.puedePublicar() && (
            <div className="p-4">Para publicar libros, cambiá tu tipo de usuario a Publicador o Lector / Publicador desde Editar perfil.</div>
          )}
        </div>

        <div className="flex items-center justify-between border-t border-stone-200 px-5 py-4 text-sm text-stone-500">
          <p>
            Mostrando {start}-{end} de {pagination.totalItems} libros
          </p>

          <div className="flex items-center gap-3 text-lg">
            <button
              className="cursor-pointer text-stone-500 transition hover:text-stone-800"
              onClick={handlePreviousPage}
            >
              <CaretLeftIcon />
            </button>
            <button
              className="text-stone-400 transition hover:text-stone-700 cursor-pointer"
              onClick={handleNextPage}
            >
              <CaretRightIcon />
            </button>
          </div>
        </div>
      </div>
    </section>
  )
}
