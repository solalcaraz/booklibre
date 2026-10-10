import { Link } from 'react-router-dom'
import { PencilSimpleIcon, TrashIcon, BookIcon } from '@phosphor-icons/react'
import type { LibroUsuario } from '../../services/UsuarioService'
import { formatDate } from '../../utils/formateDate'

type LibroProps = {
  libro: LibroUsuario
  onDelete: (id: number) => Promise<void>
}

export const PerfilLibro = ({ libro, onDelete }: LibroProps) => {
  const isAvailable = libro.estadoReserva == 'DISPONIBLE'

  return (
    <tr className="border-b border-slate-200 last:border-b-0">
      <td className="px-5 py-4">
        <div className="flex items-center gap-4">
          <div
            className={`flex h-16 w-12 shrink-0 items-center justify-center rounded-md text-white ${!libro.portada && 'shadow-sm bg-gray-300'}`}
          >
            {libro.portada ? (
              <img src={libro.portada} alt="" className="h-16 w-12 rounded-md" />
            ) : (
              <BookIcon size={26} />
            )}
          </div>

          <div>
            <p className="font-bold text-slate-800">{libro.titulo}</p>
            <p className="text-sm text-slate-500">{libro.autor}</p>
            <p className="text-sm text-slate-400">{libro.genero}</p>
          </div>
        </div>
      </td>

      <td className="px-5 py-4">
        <span
          className={`inline-flex rounded-full px-3 py-1 text-xs font-bold ${
            isAvailable ? 'bg-emerald-100 text-emerald-700' : 'bg-orange-100 text-orange-700'
          }`}
        >
          {isAvailable ? '● Disponible' : '● Prestado'}
        </span>
      </td>

      <td className="px-5 py-4 text-sm font-medium text-slate-500">
        {formatDate(libro.agregadoEl)}
      </td>

      <td className="px-5 py-4 text-sm font-semibold text-slate-600">{libro.clicks}</td>

      <td className="px-5 py-4">
        <div className="flex items-center justify-center gap-3 text-lg text-slate-500">
          <Link
            className="transition hover:text-slate-800 cursor-pointer"
            title="Editar"
            to={`/libros/editar/${libro.id}`}
          >
            <PencilSimpleIcon size={22} />
          </Link>
          <button
            className="transition hover:text-red-500 cursor-pointer"
            title="Eliminar"
            onClick={() => onDelete(libro.id)}
            data-testid="delete-libro"
          >
            <TrashIcon size={22} />
          </button>
        </div>
      </td>
    </tr>
  )
}
