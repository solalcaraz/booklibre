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
      <label htmlFor={name} className="mb-2 block text-sm font-medium text-stone-700">
        {label}
      </label>

      <input
        id={name}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className={`w-full rounded-lg border bg-stone-50 px-4 py-3 text-sm text-stone-700 outline-none transition placeholder:text-stone-500 focus:bg-white ${error ? 'border-red-400 focus:border-red-600 focus:ring-2 focus:ring-red-100' : 'border-stone-200 focus:border-tinta-500 focus:ring-2 focus:ring-tinta-100'}`}
      />
      {error && (
        <span className="mt-1 text-xs text-red-700" data-testid={`${name}Error`}>
          {error}
        </span>
      )}
    </div>
  )
}
