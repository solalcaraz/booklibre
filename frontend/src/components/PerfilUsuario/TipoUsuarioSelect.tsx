type TipoSelectProps = {
  tipoUsuario: string
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void
}

export const TipoUsuarioSelect = ({ tipoUsuario, onChange }: TipoSelectProps) => {
  return (
    <div>
      <label htmlFor="tipoUsuario" className="mb-2 block text-sm font-medium text-stone-700">
        Tipo de Usuario
      </label>

      <select
        name="tipoUsuario"
        value={tipoUsuario}
        onChange={onChange}
        className="w-full rounded-xl border border-stone-200 bg-stone-50 px-4 py-3 text-sm text-stone-700 outline-none transition focus:border-tinta-500 focus:bg-white focus:ring-2 focus:ring-tinta-100"
      >
        <option value="Lector">Lector</option>
        <option value="Publicador">Publicador</option>
        <option value="Lector / Publicador">Lector / Publicador</option>
      </select>
    </div>
  )
}
