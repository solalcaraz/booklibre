import {
  ArrowsDownUpIcon,
  PlusCircleIcon,
  CaretLeftIcon,
  CaretRightIcon
} from '@phosphor-icons/react'
import type { LibroUsuario } from './PerfilLibro'
import { PerfilLibro } from './PerfilLibro'
import { PerfilTab } from './PerfilTab'

type GestorLibrosProps = {
  libros: LibroUsuario[]
}

export const PerfilGestorLibros = ({ libros }: GestorLibrosProps) => {
  return (
    <section className="space-y-4">
      <div className="flex flex-col justify-between md:flex-row md:items-center">
        <h2 className="text-3xl font-extrabold tracking-tight text-slate-800 mb-3">
          Gestión de Mis Libros
        </h2>

        <button className="inline-flex items-center justify-center gap-2 rounded-xl bg-pink-100 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-pink-200 cursor-pointer">
          <span>
            <PlusCircleIcon size={20} />
          </span>
          Agregar Nuevo Libro
        </button>
      </div>

      <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="flex flex-wrap gap-2 border-b border-slate-200 px-4 py-4">
          <PerfilTab label="Todos" active />
          <PerfilTab label="Disponibles" />
          <PerfilTab label="Prestados" />
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
                <PerfilLibro key={libro.id} libro={libro} />
              ))}
            </tbody>
          </table>
        </div>

        <div className="flex items-center justify-between border-t border-slate-200 px-5 py-4 text-sm text-slate-500">
          <p>Mostrando 1-3 de 12 libros</p>

          <div className="flex items-center gap-3 text-lg">
            <button className="text-slate-400 transition hover:text-slate-700 cursor-pointer">
              <CaretLeftIcon />
            </button>
            <button className="text-slate-400 transition hover:text-slate-700 cursor-pointer">
              <CaretRightIcon />
            </button>
          </div>
        </div>
      </div>
    </section>
  )
}
