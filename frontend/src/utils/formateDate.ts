// El back manda fechas 'AAAA-MM-DD' sin hora. new Date() las toma como medianoche UTC y en
// Argentina (UTC-3) se mostraban como el día anterior, así que se arman en hora local
export function formatDate(dateString: string): string {
  const [anio, mes, dia] = dateString.split('-').map(Number)
  const date = new Date(anio, mes - 1, dia)

  return date.toLocaleDateString('es-AR', {
    day: '2-digit',
    month: 'short',
    year: 'numeric'
  })
}
