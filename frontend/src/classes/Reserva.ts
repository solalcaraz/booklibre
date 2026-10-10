import type { RangosFechasOcupada } from '../types/DetalleReserva'

export class Reserva {
  public libroId: string
  public fechaDesde: Date
  public fechaHasta: Date
  public fechasOcupadas: RangoFechas[]

  constructor(
    libroId: string,
    fechaDesdeStr: string,
    fechaHastaStr: string,
    fechasOcupadasStr: RangosFechasOcupada[]
  ) {
    this.libroId = libroId
    this.fechaDesde = this.crearFechaNormalizada(fechaDesdeStr)
    this.fechaHasta = this.crearFechaNormalizada(fechaHastaStr)
    this.fechasOcupadas = fechasOcupadasStr.map((rango) => ({
      desde: this.crearFechaNormalizada(rango.fechaDesde),
      hasta: this.crearFechaNormalizada(rango.fechaHasta)
    }))
  }

  //Manejo de fechas
  private crearFechaNormalizada(fechaString: string): Date {
    const [anio, mes, dia] = fechaString.split('-').map(Number)
    return new Date(anio, mes - 1, dia)
  }

  private crearFechaBackend(fecha: Date): string {
    const y = fecha.getFullYear()
    const m = String(fecha.getMonth() + 1).padStart(2, '0')
    const d = String(fecha.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  }

  //Validaciones
  fechasValidas(): boolean {
    const hoy = new Date()
    hoy.setHours(0, 0, 0, 0)
    return this.fechaDesde >= hoy && this.fechaHasta >= this.fechaDesde
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

  calcularDias(): number {
    const diffTime = Math.abs(this.fechaHasta.getTime() - this.fechaDesde.getTime())
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1
  }

  setDesde(fechaDesde: string): void {
    this.fechaDesde = this.crearFechaNormalizada(fechaDesde)
  }
  setHasta(fechaHasta: string): void {
    this.fechaHasta = this.crearFechaNormalizada(fechaHasta)
  }
  //Parseo a JSON
  toJSON() {
    return {
      libroMongoID: this.libroId,
      fechaDesde: this.crearFechaBackend(this.fechaDesde),
      fechaHasta: this.crearFechaBackend(this.fechaHasta)
    }
  }
}

export interface RangoFechas {
  desde: Date
  hasta: Date
}

export interface CrearReservaJSON {
  libroMongoID: string
  fechaDesde: string
  fechaHasta: string
}
