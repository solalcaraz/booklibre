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
    <tr className="border-b border-stone-200 last:border-b-0">
      <td className="px-5 py-4">
        <div className="flex items-center gap-4">
          <div
            className={`flex h-16 w-12 shrink-0 items-center justify-center rounded-md text-white ${!libro.portada && 'shadow-sm bg-stone-300'}`}
          >
            {libro.portada ? (
              <img src={libro.portada} alt="" className="h-16 w-12 rounded-sm object-cover shadow-sm" />
            ) : (
              <BookIcon size={26} />
            )}
          </div>

          <div>
            <p className="font-display font-semibold text-stone-900">{libro.titulo}</p>
            <p className="text-sm text-stone-600">{libro.autor}</p>
            <p className="text-xs text-stone-500">{libro.genero}</p>
          </div>
        </div>
      </td>

      <td className="px-5 py-4">
        <span
          className={`inline-flex rounded-full px-3 py-1 text-xs font-bold ${
            isAvailable ? 'bg-emerald-50 text-emerald-800' : 'bg-lacre-50 text-lacre-700'
          }`}
        >
          {isAvailable ? 'Disponible' : 'Prestado'}
        </span>
      </td>

      <td className="cifra px-5 py-4 text-sm text-stone-600">
        {formatDate(libro.agregadoEl)}
      </td>

      <td className="cifra px-5 py-4 text-sm font-semibold text-stone-800">{libro.clicks}</td>

      <td className="px-5 py-4">
        <div className="flex items-center justify-center gap-3 text-lg text-stone-500">
          <Link
            className="transition hover:text-stone-800 cursor-pointer"
            title="Editar"
            to={`/libros/editar/${libro.id}`}
          >
            <PencilSimpleIcon size={22} />
          </Link>
          <button
            className="cursor-pointer transition hover:text-red-700"
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
