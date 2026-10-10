import { describe, it, expect } from 'vitest'
import { Reserva } from '../../classes/Reserva'

describe('Clase Reserva (Dominio)', () => {
  const fechasOcupadasMock = [
    { fechaDesde: '2099-05-10', fechaHasta: '2099-05-15' }
  ]

  it('fechasValidas da false si la fechaHasta es anterior a fechaDesde', () => {
    const reserva = new Reserva('1', '2099-12-20', '2099-12-10', [])
    expect(reserva.fechasValidas()).toBe(false)
  })

  it('estaDisponible da false si las fechas se superponen con una ocupada', () => {
    // Intenta reservar del 12 al 18 de mayo (se pisa con la del 10 al 15)
    const reserva = new Reserva('1', '2099-05-12', '2099-05-18', fechasOcupadasMock)
    expect(reserva.estaDisponible()).toBe(false)
    expect(reserva.puedeConfirmar()).toBe(false)
  })

  it('fechas validas y disponibles permiten confirmar y calculan bien los dias', () => {
    const reserva = new Reserva('1', '2099-06-01', '2099-06-05', fechasOcupadasMock)
    expect(reserva.fechasValidas()).toBe(true)
    expect(reserva.estaDisponible()).toBe(true)
    expect(reserva.puedeConfirmar()).toBe(true)
    expect(reserva.calcularDias()).toBe(5) 
  })
})