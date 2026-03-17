import type { RangosFechasOcupada } from '../types/DetalleReserva'

export class Reserva {
  public libroId: number
  public fechaDesde: Date
  public fechaHasta: Date
  public fechasOcupadas: RangoFechas[]

  constructor(
    libroId: string,
    fechaDesdeStr: string,
    fechaHastaStr: string,
    fechasOcupadasStr: RangosFechasOcupada[]
  ) {
    this.libroId = Number(libroId)
    this.fechaDesde = this.crearFechaNormalizada(fechaDesdeStr)
    this.fechaHasta = this.crearFechaNormalizada(fechaHastaStr)
    this.fechasOcupadas = fechasOcupadasStr.map((rango) => ({
      desde: this.crearFechaNormalizada(rango.fechaDesde),
      hasta: this.crearFechaNormalizada(rango.fechaHasta)
    }))
  }

  private crearFechaNormalizada(fechaString: string): Date {
    const fecha = new Date(fechaString.replace(/-/g, '/')) //Reemplaza los guiones por / para evitar problema con UTF
    fecha.setHours(0, 0, 0, 0)
    return fecha
  }

  fechasValidas(): boolean {
    const hoy = new Date()
    return this.fechaDesde >= hoy && this.fechaHasta >= this.fechaDesde
  }

  calcularDias(): number {
    const diffTime = Math.abs(this.fechaHasta.getTime() - this.fechaDesde.getTime())
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1
  }
  estaDisponible(): boolean {
    return !this.fechasOcupadas.some((fechaOcupada) => {
      const ocupadaDesde = new Date(fechaOcupada.desde)
      const ocupadaHasta = new Date(fechaOcupada.hasta)
      return this.fechaDesde <= ocupadaHasta && this.fechaHasta >= ocupadaDesde
    })
  }
  puedeConfirmar(): boolean {
    return this.fechasValidas() && this.estaDisponible()
  }
}

export interface RangoFechas {
  desde: Date
  hasta: Date
}

export interface CrearReservaJSON {
  libroId: string
  fechaDesde: string
  fechaHasta: string
}
