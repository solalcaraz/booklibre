type FilterChipProps = {
  label: string
  active: boolean
  onClick: () => void
}

export const PerfilTab = ({ label, active = false, onClick }: FilterChipProps) => {
  return (
    <button
      onClick={onClick}
      className={`rounded-full px-4 py-2 text-sm font-semibold transition ${
        active
          ? 'bg-tinta-700 text-white'
          : 'bg-stone-100 text-stone-500 hover:bg-stone-200 cursor-pointer'
      }`}
    >
      {label}
    </button>
  )
}
