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
        <div className="text-center py-10 text-gray-600">
          No se encontraron resultados para los filtros aplicados.
        </div>
      ) : (
        <div className="grid grid-cols-3 gap-6">
          {data.map((item, i) => (
            <div key={i}>
              {renderItem(item)}
            </div>
          ))}
        </div>
      )}

      <div className="flex justify-center gap-2 mt-10">
        <button
          onClick={() => setPage(p => p - 1)}
          disabled={page === 0}
          className="px-3 py-1 border border-gray-300 rounded disabled:opacity-50 disabled:cursor-not-allowed"
        >
          ‹
        </button>

        {Array.from({ length: totalPages }).map((_, i) => {

          const p = i + 1;

          return (
            <button
              key={p}
              onClick={() => setPage(p-1)}
              className={`px-3 py-1 rounded
                ${p === page+1
                  ? "bg-blue-500 text-white"
                  : "border border-gray-300"}`}
            >
              {p}
            </button>
          );
        })}

        <button
          onClick={() => setPage(p => p + 1)}
          disabled={page === totalPages-1}
          className="px-3 py-1 border border-gray-300 rounded disabled:opacity-50 disabled:cursor-not-allowed"
        >
          ›
        </button>
      </div>
    </div>
  );
}