import type { ChangeEvent } from 'react'

type Option = {
    value: string;
    label: string;
}

type BaseInputProps = {
    label: string
    type: 'text' | 'password' | 'email' | 'number' | 'date' | 'select' | 'textarea'
    name: string
    placeholder?: string
    value: string | number
    onChange: (event: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => void
    error?: string
    options?: string[] | Option[]
    disabled?: boolean
}

export const BaseInput = ({
    label, type, name, placeholder, value, onChange, error, options, disabled
}: BaseInputProps) => {
    const baseClass = `w-full rounded-lg border bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition focus:bg-white disabled:opacity-70 disabled:cursor-not-allowed ${
        error ? 'border-red-400 focus:border-red-500' : 'border-slate-200 focus:border-sky-300'
    }`

    return (
        <div>
            <label htmlFor={name} className="mb-2 block text-sm font-semibold text-slate-600">{label}</label>

            {type === 'select' ? (
                <select id={name} name={name} value={value} onChange={onChange} className={baseClass} disabled={disabled}>
                    <option value="">Seleccionar...</option>
                    {/* {options?.map(opt => <option key={opt} value={opt}>{opt}</option>)} */}
                    {options?.map(opt => {
                        // Si es un objeto usamos sus propiedades (type Option), si es string lo usamos para ambos
                        const val = typeof opt === 'string' ? opt : opt.value;
                        const lab = typeof opt === 'string' ? opt : opt.label;
                        return <option key={val} value={val}>{lab}</option>;
                    })}
                </select>
            ) : type === 'textarea' ? (
                <textarea id={name} name={name} value={value} onChange={onChange} placeholder={placeholder} rows={3} className={baseClass} disabled={disabled} />
            ) : (
                <input id={name} type={type} name={name} value={value} onChange={onChange} placeholder={placeholder} className={baseClass} disabled={disabled} />
            )}

            {error && <span className="mt-1 text-xs text-red-500">{error}</span>}
        </div>
    )
}