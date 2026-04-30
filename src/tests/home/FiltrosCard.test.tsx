import { render, screen, fireEvent } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { PanelFiltros } from '../../components/Home/FiltrosCard'
import { Genero } from '../../models/LibroResponseModel'

const filtrosVacios = {
  generos: [],
  cantPaginasMin: 0,
  cantPaginasMax: 1000,
  fechaDesde: '2026-03-25',
  fechaHasta: '2026-03-25',
  isbn: '',
  usuarioPrestador: '',
}

const renderPanel = ({
  onAplicar = vi.fn(),
  onLimpiar = vi.fn(),
  onActualizar = vi.fn(),
  onGenero = vi.fn(),
  errores = []
} = {}) => {
  render(
    <PanelFiltros
      filtros={filtrosVacios}
      errores={errores}
      onAplicar={onAplicar}
      onLimpiar={onLimpiar}
      onActualizar={onActualizar}
      onGenero={onGenero}
      hoy="2026-03-25"
    />
  )
}

describe('PanelFiltros', () => {
  it('renderiza todos los checkboxes de genero', () => {
    renderPanel()

    Object.values(Genero).forEach(genero => {
      expect(screen.getByTestId(`checkbox-genero-${genero}`)).toBeInTheDocument()
    })
  })

  it('llama a onGenero cuando se marca un checkbox', () => {
    const onGeneroMock = vi.fn()
    renderPanel({ onGenero: onGeneroMock })

    fireEvent.click(screen.getByTestId(`checkbox-genero-${Genero.ROMANCE}`))

    expect(onGeneroMock).toHaveBeenCalledWith(Genero.ROMANCE, true)
  })

  it('llama a onAplicar cuando se envia el formulario', () => {
    const onAplicarMock = vi.fn((e) => e.preventDefault())
    renderPanel({ onAplicar: onAplicarMock })

    fireEvent.click(screen.getByTestId('btn-aplicar'))

    expect(onAplicarMock).toHaveBeenCalled()
  })

  it('llama a onActualizar cuando se escriben detalles', () => {
    const onActualizarMock = vi.fn()
    renderPanel({ onActualizar: onActualizarMock })

    fireEvent.change(screen.getByTestId('input-isbn'), { target: { value: '978' } })
    fireEvent.change(screen.getByTestId('input-prestador'), { target: { value: 'usuario' } })

    expect(onActualizarMock).toHaveBeenCalledWith('isbn', '978')
    expect(onActualizarMock).toHaveBeenCalledWith('usuarioPrestador', 'usuario')
  })

  it('muestra los errores cuando los hay', () => {
    renderPanel({ errores: ['La fecha de inicio no puede ser anterior a hoy'] })

    expect(screen.getByText('⚠ La fecha de inicio no puede ser anterior a hoy')).toBeInTheDocument()
  })

  it('llama a onLimpiar cuando se hace click en limpiar filtros', () => {
    const onLimpiarMock = vi.fn()
    renderPanel({ onLimpiar: onLimpiarMock })

    fireEvent.click(screen.getByTestId('btn-limpiar'))

    expect(onLimpiarMock).toHaveBeenCalled()
  })
})
