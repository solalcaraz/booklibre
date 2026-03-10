type FilterChipProps = {
  label: string
  active?: boolean
}

export const PerfilTab = ({ label, active = false }: FilterChipProps) => {
  return (
    <button
      className={`rounded-full px-4 py-2 text-sm font-semibold transition ${
        active
          ? 'bg-slate-900 text-white'
          : 'bg-slate-100 text-slate-500 hover:bg-slate-200 cursor-pointer'
      }`}
    >
      {label}
    </button>
  )
}
