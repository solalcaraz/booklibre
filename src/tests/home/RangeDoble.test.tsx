// RangeDoble.test.tsx
import { render, screen, fireEvent } from '@testing-library/react'
import { describe, expect, test, vi } from 'vitest'
import { RangeDoble } from '../../components/Home/RangeDoble'

describe('RangeDoble', () => {
  test('muestra los valores iniciales correctamente', () => {
    render(
      <RangeDoble
        min={0} max={1000}
        valorMin={100} valorMax={800}
        onChangeMin={vi.fn()} onChangeMax={vi.fn()}
      />
    )
    expect(screen.getByTestId('valor-min').textContent).toBe('100')
    expect(screen.getByTestId('valor-max').textContent).toBe('800')
  })

  test('llama a onChangeMin cuando se mueve el slider minimo', () => {
    const onChangeMinMock = vi.fn()
    render(
      <RangeDoble
        min={0} max={1000}
        valorMin={100} valorMax={800}
        onChangeMin={onChangeMinMock} onChangeMax={vi.fn()}
      />
    )
    fireEvent.change(screen.getByTestId('range-min'), { target: { value: '200' } })
    expect(onChangeMinMock).toHaveBeenCalledWith(200)
  })

  test('llama a onChangeMax cuando se mueve el slider maximo', () => {
    const onChangeMaxMock = vi.fn()
    render(
      <RangeDoble
        min={0} max={1000}
        valorMin={100} valorMax={800}
        onChangeMin={vi.fn()} onChangeMax={onChangeMaxMock}
      />
    )
    fireEvent.change(screen.getByTestId('range-max'), { target: { value: '900' } })
    expect(onChangeMaxMock).toHaveBeenCalledWith(900)
  })

  test('no llama a onChangeMin si el valor supera el maximo', () => {
    const onChangeMinMock = vi.fn()
    render(
      <RangeDoble
        min={0} max={1000}
        valorMin={100} valorMax={500}
        onChangeMin={onChangeMinMock} onChangeMax={vi.fn()}
      />
    )
    fireEvent.change(screen.getByTestId('range-min'), { target: { value: '600' } })
    expect(onChangeMinMock).not.toHaveBeenCalled()
  })

  test('no llama a onChangeMax si el valor es menor al minimo', () => {
    const onChangeMaxMock = vi.fn()
    render(
      <RangeDoble
        min={0} max={1000}
        valorMin={300} valorMax={800}
        onChangeMin={vi.fn()} onChangeMax={onChangeMaxMock}
      />
    )
    fireEvent.change(screen.getByTestId('range-max'), { target: { value: '200' } })
    expect(onChangeMaxMock).not.toHaveBeenCalled()
  })
})