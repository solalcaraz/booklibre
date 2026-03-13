type TipoSelectProps = {
  tipoUsuario: string
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void
}

export const TipoUsuarioSelect = ({ tipoUsuario, onChange }: TipoSelectProps) => {
  return (
    <div>
      <label htmlFor="tipoUsuario" className="mb-2 block text-sm font-semibold text-slate-600">
        Tipo de Usuario
      </label>

      <select
        name="tipoUsuario"
        value={tipoUsuario}
        onChange={onChange}
        className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition focus:border-sky-300 focus:bg-white"
      >
        <option value="Lector">Lector</option>
        <option value="Publicador">Publicador</option>
        <option value="Lector / Publicador">Lector / Publicador</option>
      </select>
    </div>
  )
}
