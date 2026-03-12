import {
  ArrowsDownUpIcon,
  PlusCircleIcon,
  CaretLeftIcon,
  CaretRightIcon
} from '@phosphor-icons/react'
import type { LibroUsuario } from './PerfilLibro'
import { PerfilLibro } from './PerfilLibro'
import { PerfilTab } from './PerfilTab'
import { Link } from 'react-router'
import { useEffect, type Dispatch, type SetStateAction } from 'react'
import type { BookFilter } from '../../services/UsuarioService'

export type Pagination = {
  page: number
  totalPages: number
  size: number
  totalItems: number
}

type GestorLibrosProps = {
  libros: LibroUsuario[]
  pagination: Pagination
  setPagination: Dispatch<SetStateAction<Pagination>>
  onDelete: (id: number) => Promise<void>
  filter: string
  handleFilter: (newFilter: BookFilter) => void
}

export const PerfilGestorLibros = ({
  libros,
  onDelete,
  setPagination,
  pagination,
  filter,
  handleFilter
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
    <section className="space-y-4">
      <div className="flex flex-col justify-between md:flex-row md:items-center">
        <h2 className="text-3xl font-extrabold tracking-tight text-slate-800 mb-3">
          Gestión de Mis Libros
        </h2>

        <Link
          className="inline-flex items-center justify-center gap-2 rounded-xl bg-pink-100 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-pink-200 cursor-pointer"
          to="/libro/nuevo"
        >
          <span>
            <PlusCircleIcon size={20} />
          </span>
          Agregar Nuevo Libro
        </Link>
      </div>

      <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="flex flex-wrap gap-2 border-b border-slate-200 px-4 py-4">
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
              <tr className="border-b border-slate-200 text-sm uppercase tracking-wider text-slate-500">
                <th className="px-5 py-4 font-bold">Título y Autor</th>
                <th className="px-5 py-4 font-bold ">
                  <button className="flex items-center gap-1 cursor-pointer font-bold transition hover:text-slate-800">
                    <ArrowsDownUpIcon size={18} />
                    DISPONIBLE
                  </button>
                </th>
                <th className="px-5 py-4 font-bold ">
                  <button className="flex items-center gap-1 cursor-pointer font-bold transition hover:text-slate-800">
                    <ArrowsDownUpIcon size={18} />
                    AGREGADO
                  </button>
                </th>
                <th className="px-5 py-4 font-bold text-center">Acciones</th>
              </tr>
            </thead>

            <tbody>
              {libros.map((libro) => (
                <PerfilLibro key={libro.id} libro={libro} onDelete={onDelete} />
              ))}
            </tbody>
          </table>
        </div>

        <div className="flex items-center justify-between border-t border-slate-200 px-5 py-4 text-sm text-slate-500">
          <p>
            Mostrando {start}-{end} de {pagination.totalItems} libros
          </p>

          <div className="flex items-center gap-3 text-lg">
            <button
              className="text-slate-400 transition hover:text-slate-700 cursor-pointer"
              onClick={handlePreviousPage}
            >
              <CaretLeftIcon />
            </button>
            <button
              className="text-slate-400 transition hover:text-slate-700 cursor-pointer"
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
