import { useEffect, useRef, useState } from 'react';
import type { PagReqModel, PagResModel } from '../models/PaginationModel';
import { Spinner } from './Spinner';
import { type ErrorCustom, type ErrorResponse, getMensajeError } from '../utils/errorHandler'
import { ErrorCard } from './ErrorCard';
import { useNavigate } from 'react-router-dom';

export type FetchFunction<TData, TFilter> = (
  request: PagReqModel<TFilter>
) => Promise<PagResModel<TData>>

interface DataGridProps<TData, TFilter> {
  fetchFunction: FetchFunction<TData, TFilter>
  filter?: TFilter
  renderItem: (item: TData) => React.ReactNode
  pageSize: number
}

export default function DataGrid<TData, TFilter>({ fetchFunction, filter, renderItem, pageSize }: DataGridProps<TData, TFilter>) {
  const [data, setData] = useState<TData[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<ErrorCustom | null>(null)

  const prevFilterRef = useRef(filter)
  const navigate = useNavigate()

  useEffect(() => {
    const filterChanged = prevFilterRef.current !== filter
    // Un filtro nuevo siempre arranca en la primera página
    const pageToLoad = filterChanged ? 0 : page

    prevFilterRef.current = filter

    async function load() {
      setLoading(true)
      setError(null)

      const req = {
        pageSize: pageSize,
        pageNumber: pageToLoad,
        ...(filter ? { filter } : {})
      }

      try {
        const res = await fetchFunction(req)

        setData(res.items)
        setTotalPages(res.totalPages)
      } catch (error: unknown) {
        setError(getMensajeError(error as ErrorResponse))
      } finally {
        setLoading(false)
      }

      if (filterChanged && page !== 0) {
        setPage(0)
      }
    }

    load()

  }, [page, filter])

  return (
    <div>

      {error ? (
        <ErrorCard
          error={error}
          onRetry={() => window.location.reload()}
          onVolver={() => navigate(-1)}
        />
      ) : loading ? (
        <Spinner />
      ) : data.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-stone-300 py-12 text-center text-stone-600">
          No hay resultados para esta búsqueda. Probá con otras fechas o sacá algún filtro.
        </div>
      ) : (
        <div className="grid grid-cols-3 gap-6">
          {data.map((item, i) => (
            <div key={i} className="h-full">
              {renderItem(item)}
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
      <div className="mt-10 flex justify-center gap-2">
        <button
          onClick={() => setPage(p => p - 1)}
          disabled={page === 0}
          className="h-9 min-w-9 cursor-pointer rounded-md border border-stone-200 bg-white px-3 text-stone-700 hover:border-tinta-200 disabled:cursor-not-allowed disabled:opacity-40"
        >
          ‹
        </button>

        {Array.from({ length: totalPages }).map((_, i) => {

          const p = i + 1;

          return (
            <button
              key={p}
              onClick={() => setPage(p-1)}
              aria-current={p === page + 1 ? 'page' : undefined}
              className={`cifra h-9 min-w-9 cursor-pointer rounded-md px-3 ${
                p === page + 1
                  ? 'bg-tinta-700 font-semibold text-white'
                  : 'border border-stone-200 bg-white text-stone-700 hover:border-tinta-200'
              }`}
            >
              {p}
            </button>
          );
        })}

        <button
          onClick={() => setPage(p => p + 1)}
          disabled={page === totalPages-1}
          className="h-9 min-w-9 cursor-pointer rounded-md border border-stone-200 bg-white px-3 text-stone-700 hover:border-tinta-200 disabled:cursor-not-allowed disabled:opacity-40"
        >
          ›
        </button>
      </div>
      )}
    </div>
  );
}