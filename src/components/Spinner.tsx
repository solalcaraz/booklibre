export const Spinner = () => {
  return (
    <div className="flex items-center justify-center w-full h-[70vh]" data-testid="spinner">
      <div className="animate-spin rounded-full h-14 w-14 border-4 border-gray-300 border-t-red-600" />
    </div>
  )
}
