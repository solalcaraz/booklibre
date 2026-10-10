export type Tab = {
  id: string
  label: string
}

type TabsProps = {
  tabs: Tab[]
  onChange: (id: string) => void
  active: string
}

export const Tabs = ({ tabs, onChange, active }: TabsProps) => {
  return (
    <div className="mt-6 flex gap-10 border-b border-stone-200">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          onClick={() => onChange(tab.id)}
          className={`-mb-px cursor-pointer border-b-2 pb-2 text-center text-sm transition-colors ${
            active === tab.id
              ? 'border-tinta-700 font-semibold text-tinta-800'
              : 'border-transparent text-stone-600 hover:text-stone-900'
          }`}
        >
          {tab.label}
        </button>
      ))}
    </div>
  )
}
