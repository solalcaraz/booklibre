interface RangeDobleProps {
  min: number
  max: number
  valorMin: number
  valorMax: number
  onChangeMin: (value: number) => void
  onChangeMax: (value: number) => void
}

export const RangeDoble = ({ min, max, valorMin, valorMax, onChangeMin, onChangeMax }: RangeDobleProps) => {
  return (
    <div className="mt-4">
      {/* valores */}
      <div className="flex justify-between text-xs text-slate-400 mb-2">
        <span data-testid="valor-min">{valorMin}</span>
        <span data-testid="valor-max">{valorMax}</span>
      </div>
      <div className="relative w-full h-6 flex items-center">
        {/* barra base */}
        <div className="absolute w-full h-1 bg-slate-200 rounded" />
        {/* barra activa */}
        <div
          className="absolute h-1 bg-sky-400 rounded"
          style={{
            left: `${(valorMin / max) * 100}%`,
            right: `${100 - (valorMax / max) * 100}%`
          }}
        />
        {/* rango minimo */}
        <input
          type="range" min={min} max={max} value={valorMin}
          data-testid="range-min"
          onChange={e => {
            const v = +e.target.value
            if (v <= valorMax) onChangeMin(v)
          }}
          className="absolute w-full appearance-none bg-transparent pointer-events-none
                    [&::-webkit-slider-thumb]:pointer-events-auto
                    [&::-webkit-slider-thumb]:appearance-none
                    [&::-webkit-slider-thumb]:h-4
                    [&::-webkit-slider-thumb]:w-4
                    [&::-webkit-slider-thumb]:rounded-full
                    [&::-webkit-slider-thumb]:bg-sky-500"
        />
        {/* range maximo */}
        <input
          type="range" min={min} max={max} value={valorMax}
          data-testid="range-max"
          onChange={e => {
            const v = +e.target.value
            if (v >= valorMin) onChangeMax(v)
          }}
          className="absolute w-full appearance-none bg-transparent pointer-events-none
                    [&::-webkit-slider-thumb]:pointer-events-auto
                    [&::-webkit-slider-thumb]:appearance-none
                    [&::-webkit-slider-thumb]:h-4
                    [&::-webkit-slider-thumb]:w-4
                    [&::-webkit-slider-thumb]:rounded-full
                    [&::-webkit-slider-thumb]:bg-sky-500"
        />
      </div>
    </div>
  )
}