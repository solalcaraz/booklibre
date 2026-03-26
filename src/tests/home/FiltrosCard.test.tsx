import { render, screen, fireEvent } from '@testing-library/react'
import { describe, expect, test, vi } from 'vitest'
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

describe('PanelFiltros', () => {
  test('renderiza todos los checkboxes de genero', () => {
    render(
      <PanelFiltros
        filtros={filtrosVacios}
        errores={[]}
        onAplicar={vi.fn()}
        onLimpiar={vi.fn()}
        onActualizar={vi.fn()}
        onGenero={vi.fn()}
        hoy="2026-03-25"
      />
    )
    Object.values(Genero).forEach(genero => {
      expect(screen.getByTestId(`checkbox-genero-${genero}`)).toBeTruthy()
    })
  })

  test('llama a onGenero cuando se marca un checkbox', () => {
    const onGeneroMock = vi.fn()
    render(
      <PanelFiltros
        filtros={filtrosVacios}
        errores={[]}
        onAplicar={vi.fn()}
        onLimpiar={vi.fn()}
        onActualizar={vi.fn()}
        onGenero={onGeneroMock}
        hoy="2026-03-25"
      />
    )
    fireEvent.click(screen.getByTestId(`checkbox-genero-${Genero.ROMANCE}`))
    expect(onGeneroMock).toHaveBeenCalledWith(Genero.ROMANCE, true)
  })

  test('muestra los errores cuando los hay', () => {
    render(
      <PanelFiltros
        filtros={filtrosVacios}
        errores={['La fecha de inicio no puede ser anterior a hoy']}
        onAplicar={vi.fn()}
        onLimpiar={vi.fn()}
        onActualizar={vi.fn()}
        onGenero={vi.fn()}
        hoy="2026-03-25"
      />
    )
    expect(screen.getByText('⚠ La fecha de inicio no puede ser anterior a hoy')).toBeTruthy()
  })

  test('llama a onLimpiar cuando se hace click en limpiar filtros', () => {
    const onLimpiarMock = vi.fn()
    render(
      <PanelFiltros
        filtros={filtrosVacios}
        errores={[]}
        onAplicar={vi.fn()}
        onLimpiar={onLimpiarMock}
        onActualizar={vi.fn()}
        onGenero={vi.fn()}
        hoy="2026-03-25"
      />
    )
    fireEvent.click(screen.getByTestId('btn-limpiar'))
    expect(onLimpiarMock).toHaveBeenCalled()
  })
})