import { UserIcon } from "@phosphor-icons/react"

type InfoItemProps = {
  label: string
  value: string
}

const InfoItem = ({ label, value }: InfoItemProps) => {
  return (
    <div className="border-b border-slate-200 pb-4 last:border-b-0 last:pb-0">
      <p className="mb-2 text-xs font-bold uppercase tracking-wider text-slate-500">{label}</p>
      <p className="text-sm font-medium text-slate-700">{value}</p>
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
      className="rounded-2xl border border-slate-200 bg-[#eef5fb] p-5 shadow-sm"
      data-testid="perfil-info"
    >
      <div className="mb-5 flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-[#d1eaff] text-lg shadow-sm">
          <UserIcon size={22} />
        </div>
        <h2 className="text-2xl font-bold text-slate-800">Mis Datos</h2>
      </div>

      <div className="space-y-5">
        <InfoItem label="Email" value={email} />
        <InfoItem label="Teléfono" value={telefono} />
        <InfoItem label="Tipo de Perfil" value={tipoPerfil} />
      </div>
    </section>
  )
}
