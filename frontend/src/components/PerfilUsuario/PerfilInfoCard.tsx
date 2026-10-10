import { UserIcon } from "@phosphor-icons/react"

type InfoItemProps = {
  label: string
  value: string
}

const InfoItem = ({ label, value }: InfoItemProps) => {
  return (
    <div className="border-b border-stone-200 pb-4 last:border-b-0 last:pb-0">
      <p className="mb-1 text-xs text-stone-500">{label}</p>
      <p className="text-sm font-medium text-stone-800">{value}</p>
    </div>
  )
}

type InfoCardProps = {
  email: string
  telefono: string
  tipoPerfil: string
}

export const PerfilInfoCard = ({ email, telefono, tipoPerfil }: InfoCardProps) => {
  return (
    <section
      className="rounded-2xl border border-stone-200 bg-white p-5"
      data-testid="perfil-info"
    >
      <div className="mb-5 flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-tinta-50 text-lg text-tinta-700">
          <UserIcon size={22} />
        </div>
        <h2 className="font-display text-xl font-semibold text-stone-900">Mis datos</h2>
      </div>

      <div className="space-y-5">
        <InfoItem label="Email" value={email} />
        <InfoItem label="Teléfono" value={telefono} />
        <InfoItem label="Tipo de usuario" value={tipoPerfil} />
      </div>
    </section>
  )
}
