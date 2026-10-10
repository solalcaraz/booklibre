type StatBoxProps = {
  value: string
  label: string
}

const StatBox = ({ value, label }: StatBoxProps) => {
  return (
    <div className="rounded-xl bg-papel px-4 py-5 text-center">
      <p className="cifra font-display text-4xl font-semibold text-tinta-900">{value}</p>
      <p className="mt-1 text-sm text-stone-600">{label}</p>
    </div>
  )
}

type StatsProps = {
  librosPrestados: number
  librosLeidos: number
}

export const PerfilStats = ({ librosPrestados, librosLeidos }: StatsProps) => {
  return (
    <section
      className="rounded-2xl border border-stone-200 bg-white p-5"
      data-testid="perfil-stats"
    >
      <div className="grid grid-cols-2 gap-4">
        <StatBox value={librosPrestados.toString()} label="Préstamos de mis libros" />
        <StatBox value={librosLeidos.toString()} label="Libros leídos" />
      </div>
    </section>
  )
}
