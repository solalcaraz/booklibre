import { useEffect, useMemo, useState } from 'react'
import { XIcon } from '@phosphor-icons/react'
import type { UsuarioResponse, EditProfileData } from '../../services/UsuarioService'
import { TipoUsuarioSelect } from './TipoUsuarioSelect'
import { FormInput } from '../FormInput'
import { Spinner } from '../Spinner'

type EditProfileModalProps = {
  isOpen: boolean
  initialData: UsuarioResponse
  onClose: () => void
  onUpdate: (data: EditProfileData) => Promise<void>
}

export const EditProfileModal = ({
  isOpen,
  initialData,
  onClose,
  onUpdate
}: EditProfileModalProps) => {
  // Sin useMemo el objeto cambiaría en cada render y el useEffect de abajo se dispararía en loop
  const initialEditFormData = useMemo(() => {
    return {
      nombreCompleto: initialData.nombreCompleto,
      descripcion: initialData.descripcion,
      email: initialData.email,
      telefono: initialData.telefono,
      ciudad: initialData.ciudad,
      imagen: initialData.imagen,
      tipoUsuario: initialData.tipoUsuario
    }
  }, [initialData])

  const [editFormData, setEditFormData] = useState<EditProfileData>(initialEditFormData)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (isOpen) {
      setEditFormData(initialEditFormData)
      document.body.style.overflow = 'hidden'
    }
  }, [isOpen, initialEditFormData])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target

    setEditFormData((prev) => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault()

    try {
      setLoading(true)
      await onUpdate(editFormData)
      onClose()
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-stone-900/40 px-4 py-6">
      <div className="flex min-h-full items-center justify-center">
        <div className="max-h-[90vh] w-full max-w-xl overflow-hidden rounded-2xl bg-white shadow-xl">
          {loading ? (
            <Spinner />
          ) : (
            <div className="flex max-h-[90vh] flex-col">
              <div className="flex items-center justify-between border-b border-stone-200 px-6 py-4 shrink-0">
                <div>
                  <h2 className="font-display text-xl font-semibold text-stone-900">Editar perfil</h2>
                  <p className="mt-1 text-sm text-stone-600">Si cambiás el tipo de usuario, cambian las secciones que ves.</p>
                </div>

                <button
                  type="button"
                  onClick={onClose}
                  className="cursor-pointer rounded-full px-2 py-2 text-stone-600 transition hover:bg-stone-100"
                  aria-label="Cerrar modal"
                >
                  <XIcon size={22} />
                </button>
              </div>

              <form onSubmit={handleSubmit} className="space-y-5 overflow-y-auto px-6 py-6">
                <FormInput
                  label="Nombre completo"
                  value={editFormData.nombreCompleto}
                  type="text"
                  name="nombreCompleto"
                  onChange={handleChange}
                  placeholder="Ej. Diego Maradona"
                />

                <FormInput
                  label="Descripción"
                  value={editFormData.descripcion}
                  type="text"
                  name="descripcion"
                  onChange={handleChange}
                  placeholder="Ej. Lector Ávido & Coleccionista"
                />

                <FormInput
                  label="Imagen"
                  value={editFormData.imagen}
                  type="text"
                  name="imagen"
                  onChange={handleChange}
                  placeholder="https://imagen.com/imagen.jpg"
                />

                <FormInput
                  label="Email"
                  type="email"
                  name="email"
                  value={editFormData.email}
                  onChange={handleChange}
                  placeholder="tu@email.com"
                />

                <FormInput
                  label="Teléfono"
                  value={editFormData.telefono}
                  name="telefono"
                  type="text"
                  onChange={handleChange}
                  placeholder="+54 (11) 123-4567"
                />

                <FormInput
                  label="Ciudad"
                  value={editFormData.ciudad}
                  onChange={handleChange}
                  name="ciudad"
                  type="text"
                  placeholder="Ej. González Catán, BA"
                />

                <TipoUsuarioSelect tipoUsuario={editFormData.tipoUsuario} onChange={handleChange} />

                <div className="flex justify-end gap-3 pt-2">
                  <button
                    type="button"
                    onClick={onClose}
                    className="cursor-pointer rounded-xl border border-stone-200 px-4 py-2.5 text-sm font-semibold text-stone-600 transition hover:bg-stone-50"
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    disabled={loading}
                    className="cursor-pointer rounded-xl bg-tinta-700 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-tinta-800 disabled:cursor-not-allowed disabled:opacity-70"
                    data-testid="patch-usuario"
                  >
                    {loading ? 'Guardando...' : 'Guardar cambios'}
                  </button>
                </div>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
