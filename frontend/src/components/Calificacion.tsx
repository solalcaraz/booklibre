type Props = {
  valor: number | null | undefined
  testId?: string
}

// El back devuelve 0 cuando un libro no tiene reseñas: mostrar "★ 0" parecería una mala nota
export const Calificacion = ({ valor, testId }: Props) => {
  if (!valor) {
    return (
      <span className="text-xs text-stone-500" data-testid={testId}>
        Sin reseñas
      </span>
    )
  }

  return (
    <span className="cifra text-xs font-medium text-stone-700" data-testid={testId}>
      <span className="text-amber-500">★</span> {valor.toFixed(1)}
    </span>
  )
}
