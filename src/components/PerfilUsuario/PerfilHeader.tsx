type PerfilHeaderProps = {
  nombre: string
  descripcion: string
  unidoEn: string
  ciudad: string
  bibliokarmas: string
}

export const PerfilHeader = ({
  nombre,
  descripcion,
  unidoEn,
  ciudad,
  bibliokarmas
}: PerfilHeaderProps) => {
  return (
    <section className="flex flex-col justify-between gap-5 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm md:flex-row md:items-center">
      <div className="flex items-start gap-4">
        <div className="flex h-20 w-20 shrink-0 items-center justify-center rounded-full border-2 border-sky-100 bg-[#f7d8c9] text-3xl">
          📚
        </div>

        <div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-800">{nombre}</h1>

          <p className="mt-1 text-sm font-medium text-sky-500">{descripcion}</p>

          <div className="mt-3 flex flex-wrap gap-x-4 gap-y-2 text-sm text-slate-500">
            <span>🗓️ {unidoEn}</span>
            <span>📍 {ciudad}</span>
            <span>📈 {bibliokarmas}</span>
          </div>
        </div>
      </div>

      <button className="inline-flex items-center justify-center gap-2 self-start rounded-xl bg-slate-100 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-200 cursor-pointer">
        <span>⚙️</span>
        Editar Perfil
      </button>
    </section>
  )
}
