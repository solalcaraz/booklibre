import { useState } from 'react'
import { GearIcon, ChartLineIcon, MapPinIcon, CalendarDotsIcon } from '@phosphor-icons/react'
import type { UsuarioResponse, EditProfileData } from '../../services/UsuarioService'
import { EditProfileModal } from './PerfilEditModal'

type PerfilHeaderProps = {
  usuario: UsuarioResponse
  onUpdate: (data: EditProfileData) => Promise<void>
}

export const PerfilHeader = ({ usuario, onUpdate }: PerfilHeaderProps) => {
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)

  const handleCloseModal = () => {
    setIsEditModalOpen(false)
    document.body.style.overflow = 'auto'
  }

  return (
    <section className="flex flex-col justify-between gap-5 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm md:flex-row md:items-center">
      <div className="flex items-start gap-4">
        <div className="flex h-20 w-20 shrink-0 items-center justify-center rounded-full border-2 border-none bg-[#f7d8c900] text-3xl">
          <img src={usuario.imagen} alt="" className="rounded-full " />
        </div>

        <div>
          <h1 className="text-4xl font-extrabold tracking-tight text-slate-800">
            {usuario.nombreCompleto}
          </h1>

          <p className="mt-2 text-sm font-medium text-sky-500">{usuario.descripcion}</p>

          <div className="mt-3 flex flex-wrap gap-x-4 gap-y-2 text-sm text-slate-500">
            <span className="flex items-center gap-1">
              <CalendarDotsIcon size={18} /> Se unió en {usuario.anoUnido}
            </span>
            <span className="flex items-center gap-1">
              <MapPinIcon size={18} /> {usuario.ciudad}
            </span>
            <span className="flex items-center gap-1">
              <ChartLineIcon size={18} /> Bibliokarma {usuario.bibliokarmas}
            </span>
          </div>
        </div>
      </div>

      <button
        className="inline-flex items-center justify-center gap-2 self-start rounded-xl bg-slate-100 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-200 cursor-pointer"
        onClick={() => setIsEditModalOpen(true)}
      >
        <GearIcon size={20} /> Editar Perfil
      </button>

      <EditProfileModal
        isOpen={isEditModalOpen}
        initialData={usuario}
        onClose={handleCloseModal}
        onUpdate={onUpdate}
      />
    </section>
  )
}
