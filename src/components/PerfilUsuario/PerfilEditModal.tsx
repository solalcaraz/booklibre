import { useEffect, useMemo, useState } from 'react'
import { XIcon } from '@phosphor-icons/react'
import type { UsuarioResponse, EditProfileData } from '../../services/UsuarioService'
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
  /* uso el hook useMemo para evitar re-render infinito */
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

  /* Mapeo los datos iniciales del usuario en un estado reactivo */
  const [editFormData, setEditFormData] = useState<EditProfileData>(initialEditFormData)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (isOpen) {
      setEditFormData(initialEditFormData)
      document.body.style.overflow = 'hidden'
    }
  }, [isOpen, initialEditFormData])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
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
    } catch (err) {
      console.log(err)
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black/30 px-4 py-6">
      <div className="flex min-h-full items-center justify-center">
        <div className="max-h-[90vh] w-full max-w-xl overflow-hidden rounded-2xl bg-white shadow-[0_20px_60px_rgba(15,23,42,0.18)]">
          {loading ? (
            <Spinner />
          ) : (
            <div className="flex max-h-[90vh] flex-col">
              <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4 shrink-0">
                <div>
                  <h2 className="text-xl font-extrabold text-slate-800">Editar perfil</h2>
                  <p className="mt-1 text-sm text-slate-500">Modificá tus datos personales.</p>
                </div>

                <button
                  type="button"
                  onClick={onClose}
                  className="cursor-pointer rounded-full px-2 py-2 text-black transition hover:bg-slate-100"
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

                {/* Esto va a ser un select entiendo */}
                <FormInput
                  label="Tipo de Usuario"
                  value={editFormData.tipoUsuario}
                  type="text"
                  name="tipoUsuario"
                  onChange={handleChange}
                  placeholder="Lector / Publicador"
                />

                <div className="flex justify-end gap-3 pt-2">
                  <button
                    type="button"
                    onClick={onClose}
                    className="cursor-pointer rounded-xl border border-slate-200 px-4 py-2.5 text-sm font-semibold text-slate-600 transition hover:bg-slate-50"
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    disabled={loading}
                    className="cursor-pointer rounded-xl bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-70"
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
