type LibroEstado = 'Disponible' | 'Prestado'

export type LibroUsuario = {
  id: number
  titulo: string
  autor: string
  genero: string
  estado: LibroEstado
  agregadoEl: string
  portada: string
}

type LibroProps = {
  libro: LibroUsuario
}

export const PerfilLibro = ({ libro }: LibroProps) => {
  const isAvailable = libro.estado === 'Disponible'

  return (
    <tr className="border-b border-slate-200 last:border-b-0">
      <td className="px-5 py-4">
        <div className="flex items-center gap-4">
          <div
            className={`flex h-16 w-12 shrink-0 items-center justify-center rounded-md text-white shadow-sm ${libro.portada}`}
          >
            📘
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

      <td className="px-5 py-4 text-sm font-medium text-slate-500">{libro.agregadoEl}</td>

      <td className="px-5 py-4">
        <div className="flex items-center justify-center gap-3 text-lg text-slate-500">
          <button className="transition hover:text-slate-800 cursor-pointer" title="Editar">
            ✏️
          </button>
          <button className="transition hover:text-red-500 cursor-pointer" title="Eliminar">
            🗑️
          </button>
        </div>
      </td>
    </tr>
  )
}
