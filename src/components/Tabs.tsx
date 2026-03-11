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
    <div className="flex mt-6 border-b border-gray-200">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          onClick={() => onChange(tab.id)}
          className={`pb-2 mr-20 text-center font-medium transition-colors cursor-pointer ${
            active === tab.id
              ? 'text-blue-600 border-b-2 border-blue-600'
              : 'text-gray-400 hover:text-gray-700'
          }`}
        >
          {tab.label}
        </button>
      ))}
    </div>
  )
}
