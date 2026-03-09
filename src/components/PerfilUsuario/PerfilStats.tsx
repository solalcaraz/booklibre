type StatBoxProps = {
  value: string
  label: string
}

const StatBox = ({ value, label }: StatBoxProps) => {
  return (
    <div className="rounded-2xl bg-white px-4 py-6 text-center shadow-sm">
      <p className="text-4xl font-extrabold text-slate-800">{value}</p>
      <p className="mt-2 text-sm font-medium text-slate-500">{label}</p>
    </div>
  )
}

type StatsProps = {
  librosPrestados: string
  librosLeidos: string
}

export const PerfilStats = ({ librosPrestados, librosLeidos }: StatsProps) => {
  return (
    <section className="rounded-2xl border border-slate-200 bg-[#faf4fb] p-5 shadow-sm">
      <div className="grid grid-cols-2 gap-4">
        <StatBox value={librosPrestados} label="Libros Prestados" />
        <StatBox value={librosLeidos} label="Libros Leídos" />
      </div>
    </section>
  )
}
