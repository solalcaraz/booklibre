// components/ui/PaginatedFetch.jsx
import { useEffect, useState } from "react";
import type { PaginationResponseModel } from "../models/PaginationResponseModel";

type FetchFunction<T> = (
  params: Record<string, any>
) => Promise<PaginationResponseModel<T>>

interface DataGridProps<T> {
  fetchFunction: FetchFunction<T>
  query: Record<string, any>
  children: (data: T[]) => React.ReactNode
}

export default function DataGrid<T>({ fetchFunction, query, children }: DataGridProps<T>) {

  const [data, setData] = useState<T[]>([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);

  async function load() {
    setLoading(true);

    const res = await fetchFunction({
      page,
      ...query,
    });

    setData(res.data);
    setTotalPages(res.totalPages);

    setLoading(false);
  }

  useEffect(() => {
    load();
  }, [page, JSON.stringify(query)]);

  return (
    <div>

      {/* CONTENT */}
      {loading ? (
        <div className="text-center py-10">Loading...</div>
      ) : (
        children(data)
      )}

      {/* PAGINATION */}
      <div className="flex justify-center gap-2 mt-10">

        <button
          onClick={() => setPage(p => p - 1)}
          disabled={page === 1}
          className="px-3 py-1 border rounded"
        >
          ‹
        </button>

        {Array.from({ length: totalPages }).map((_, i) => {

          const p = i + 1;

          return (
            <button
              key={p}
              onClick={() => setPage(p)}
              className={`px-3 py-1 rounded
                ${p === page
                  ? "bg-blue-500 text-white"
                  : "border"}`}
            >
              {p}
            </button>
          );
        })}

        <button
          onClick={() => setPage(p => p + 1)}
          disabled={page === totalPages}
          className="px-3 py-1 border rounded"
        >
          ›
        </button>

      </div>

    </div>
  );
}