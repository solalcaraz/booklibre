import { render, screen, fireEvent } from '@testing-library/react'
import { describe, expect, test, vi } from 'vitest'
import { BarraBusqueda } from '../../components/Home/BarraBusqueda'


describe('BarraBusqueda', () => {
  test('renderiza el input de busqueda', () => {
    render(
      <BarraBusqueda
        busqueda=""
        onChangeBusqueda={vi.fn()}
        onBuscar={vi.fn()}
        onLimpiar={vi.fn()}
      />
    )
    expect(screen.getByTestId('input-busqueda')).toBeTruthy()
  })

  test('llama a onChangeBusqueda cuando el usuario escribe', async () => {
    const onChangeMock = vi.fn()
    render(
      <BarraBusqueda
        busqueda=""
        onChangeBusqueda={onChangeMock}
        onBuscar={vi.fn()}
        onLimpiar={vi.fn()}
      />
    )
    fireEvent.change(screen.getByTestId('input-busqueda'), { target: { value: 'Rayuela' } })
    expect(onChangeMock).toHaveBeenCalledWith('Rayuela')
  })

  test('llama a onBuscar cuando se envia el formulario', () => {
    const onBuscarMock = vi.fn()
    render(
      <BarraBusqueda
        busqueda="Rayuela"
        onChangeBusqueda={vi.fn()}
        onBuscar={onBuscarMock}
        onLimpiar={vi.fn()}
      />
    )
    fireEvent.click(screen.getByTestId('btn-buscar'))
    expect(onBuscarMock).toHaveBeenCalled()
  })

  test('llama a onLimpiar cuando el input se vacia', () => {
    const onLimpiarMock = vi.fn()
    render(
      <BarraBusqueda
        busqueda="algo"
        onChangeBusqueda={vi.fn()}
        onBuscar={vi.fn()}
        onLimpiar={onLimpiarMock}
      />
    )
    fireEvent.input(screen.getByTestId('input-busqueda'), { target: { value: '' } })
    expect(onLimpiarMock).toHaveBeenCalled()
  })
})