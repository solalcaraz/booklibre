import { render, screen } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { KpiHeader } from '../../components/Dashboard/KpiHeader'
import { dashboardService } from '../../services/DashboardService'

vi.mock('../../services/DashboardService', () => ({
  dashboardService: {
    getTasaConversionClicksReservas: vi.fn()
  }
}))

describe('KpiHeader', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('muestra loading al iniciar', () => {
    vi.mocked(dashboardService.getTasaConversionClicksReservas).mockReturnValue(new Promise(() => {}))

    render(<KpiHeader />)

    expect(screen.getByTestId('kpi-header-loading')).toBeInTheDocument()
  })

  it('muestra las metricas de conversion', async () => {
    vi.mocked(dashboardService.getTasaConversionClicksReservas).mockResolvedValue([
      {
        libroId: '1',
        titulo: 'Rayuela',
        autor: 'Julio Cortazar',
        clicks: 20,
        reservas: 5,
        tasaConversion: 0.25
      }
    ])

    render(<KpiHeader />)

    expect(await screen.findByTestId('kpi-header')).toBeInTheDocument()
    expect(screen.getByText('Rayuela')).toBeInTheDocument()
    expect(screen.getByText('20 clicks')).toBeInTheDocument()
    expect(screen.getByText('5 reservas')).toBeInTheDocument()
    expect(screen.getByTestId('kpi-tasa-1')).toHaveTextContent('25.0%')
  })

  it('muestra estado vacio cuando no hay datos', async () => {
    vi.mocked(dashboardService.getTasaConversionClicksReservas).mockResolvedValue([])

    render(<KpiHeader />)

    expect(await screen.findByTestId('kpi-header-empty')).toBeInTheDocument()
  })

  it('muestra error si falla el service', async () => {
    vi.mocked(dashboardService.getTasaConversionClicksReservas).mockRejectedValue(new Error('GraphQL error'))

    render(<KpiHeader />)

    expect(await screen.findByTestId('kpi-header-error')).toBeInTheDocument()
  })
})
