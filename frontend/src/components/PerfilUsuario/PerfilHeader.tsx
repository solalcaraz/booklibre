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
    <section
      className="flex flex-col justify-between gap-5 rounded-2xl border border-stone-200 bg-white p-6 md:flex-row md:items-center"
      data-testid="perfil-header"
    >
      <div className="flex items-start gap-4">
        <div className="flex h-20 w-20 shrink-0 items-center justify-center rounded-full">
          <img src={usuario.imagen} alt="" className="h-20 w-20 rounded-full object-cover ring-4 ring-tinta-50" />
        </div>

        <div>
          <h1 className="font-display text-4xl font-semibold text-stone-900">
            {usuario.nombreCompleto}
          </h1>

          <p className="mt-1 text-sm italic text-stone-600">{usuario.descripcion}</p>

          <div className="mt-3 flex flex-wrap gap-x-5 gap-y-2 text-sm text-stone-600">
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
        className="inline-flex items-center justify-center gap-2 self-start cursor-pointer rounded-xl border border-stone-200 px-4 py-2.5 text-sm font-semibold text-stone-700 transition hover:bg-stone-50"
        onClick={() => setIsEditModalOpen(true)}
        data-testid="editar-perfil"
      >
        <GearIcon size={20} /> Editar perfil
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
