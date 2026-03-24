import { useEffect, useRef, useState } from 'react';
import type { PagReqModel, PagResModel } from '../models/PaginationModel';
import { Spinner } from './Spinner';

type FetchFunction<TData, TFilter> = (
  request: PagReqModel<TFilter>
) => Promise<PagResModel<TData>>

interface DataGridProps<TData, TFilter> {
  fetchFunction: FetchFunction<TData, TFilter>  // metodo del servicio que llama al endpoint que trae la data
  filter?: TFilter
  renderItem: (item: TData) => React.ReactNode  //componente (fila/card) que quiero renderizar dentro de la grid, la grid itera sola
  pageSize: number
}

export default function DataGrid<TData, TFilter>({ fetchFunction, filter, renderItem, pageSize }: DataGridProps<TData, TFilter>) {
  const [data, setData] = useState<TData[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);

  const prevFilterRef = useRef(filter)

  useEffect(() => {
    const filterChanged = prevFilterRef.current !== filter
    // Si cambia el filtro → ir a página 0
    const pageToLoad = filterChanged ? 0 : page

    prevFilterRef.current = filter

    async function load() {
      setLoading(true)

      const req = {
        pageSize: pageSize,
        pageNumber: pageToLoad,
        ...(filter ? { filter } : {})
      }

      const res = await fetchFunction(req)

      setData(res.items)
      setTotalPages(res.totalPages)
      setLoading(false)

      // Si cambió filtro y no estábamos en 0 → sincronizar page
      if (filterChanged && page !== 0) {
        setPage(0)
      }
    }

    load()

  }, [page, filter])

  return (
    <div>
      {/* ------ CONTENIDO ------*/}
      {loading ? (
        <Spinner />
      ) : (
        <div className="grid grid-cols-3 gap-6">
          {data?.map((item, i) => (
            <div key={i}>
              {renderItem(item)}
            </div>
          ))}
        </div>
      )}

      {/* ----- PAGINATION ----- */}
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