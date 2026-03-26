import { describe, expect, test } from 'vitest'
import { formatearFecha } from '../../utils/formatearFecha'

describe('formatearFecha', () => {
  test('formatea una fecha correctamente en formato YYYY-MM-DD', () => {
    const fecha = new Date(2026, 2, 25) // 25 de marzo de 2026
    expect(formatearFecha(fecha)).toBe('2026-03-25')
  })

  test('formatea null sin romper', () => {
    expect(formatearFecha(null)).toBeTruthy()
  })
})