import type { ChangeEvent } from 'react'

type FormFieldProps = {
  label: string
  type: string
  name: string
  placeholder: string
  value: string
  onChange: (event: ChangeEvent<HTMLInputElement>) => void
  error?: string
}

export const FormInput = ({
  label,
  type,
  name,
  placeholder,
  value,
  onChange,
  error
}: FormFieldProps) => {
  return (
    <div>
      <label htmlFor={name} className="mb-2 block text-sm font-semibold text-slate-600">
        {label}
      </label>

      <input
        id={name}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className={`w-full rounded-lg border bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition placeholder:text-slate-400 focus:bg-white ${error ? 'border-red-400 focus:border-red-500' : 'border-slate-200 focus:border-sky-300'}`}
      />
      {error && <span className="mt-1 text-xs text-red-500">{error}</span>}
    </div>
  )
}
