import { useState } from "react"
import { LibroCard } from "../components/LibroCard"
import type { FiltrosLibro, Orden, ReservaLibroHomeJSON } from "../types/ReservaLibroHome"
import { reservaService } from "../services/ReservaService"
import DataGrid from "../components/DataGrid"
import { Genero } from "../models/LibroResponseModel"
import { PanelFiltros } from "../components/Home/FiltrosCard"
import { BarraBusqueda } from "../components/Home/BarraBusqueda"

const hoy = new Date().toISOString().split('T')[0] // para quedarme solo con la fecha sin el horario

const filtrosIniciales: FiltrosLibro = {
  generos: [],
  cantPaginasMin: 0,
  cantPaginasMax: 1000,
  fechaDesde: hoy,
  fechaHasta: hoy,
  isbn: '',
  usuarioPrestador: '',
  orden: 'POPULARIDAD',
  numeroPagina: 0,
  tamanoPaginado: 3
}

export const Home = () => {
  const [filtros, setFiltros] = useState<FiltrosLibro>(filtrosIniciales)
  const [filtrosAplicados, setFiltrosAplicados] = useState<FiltrosLibro>(filtrosIniciales)
  const [errores, setErrores] = useState<string[]>([])
  
  //TODO: cambiar por local storage
  const userId = 2 //+JSON.parse(localStorage.getItem('user')!).id

  // metodo para actualizar inputs dinamicamente
  const actualizar = (campo: keyof FiltrosLibro, valor: unknown) => {
    setFiltros(prev => ({ ...prev, [campo]: valor }))
  }

  // validaciones para los filtros
  const validar = (): boolean => {
    const nuevosErrores: string[] = []

    if  (filtros.fechaDesde && filtros.fechaDesde < hoy)
      nuevosErrores.push('La fecha de inicio no puede ser anterior a hoy')
    if (filtros.fechaDesde && filtros.fechaHasta && filtros.fechaHasta < filtros.fechaDesde)
      nuevosErrores.push('La fecha hasta no puede ser anterior a la fecha desde')

    setErrores(nuevosErrores)
    return nuevosErrores.length === 0
  }

  const handleGenero = (genero: Genero, checked: boolean) => {
    const nuevosGeneros = checked 
    ? [...(filtros.generos ?? []), genero] : (filtros.generos ?? []).filter(g => g !== genero)
    actualizar('generos', nuevosGeneros)
  }
  
  const handleAplicarFiltros = (e: React.FormEvent) => {
    e.preventDefault()
    if (validar()) setFiltrosAplicados({ ...filtros })
  }
  
  const handleBusqueda = (e: React.FormEvent) => {
    e.preventDefault()
    if (validar()) setFiltrosAplicados({ ...filtros })
  }

  const handleOrden = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const nuevosFiltros = { ...filtros, orden: e.target.value as Orden}
    setFiltros(nuevosFiltros)
    setFiltrosAplicados(nuevosFiltros)
  }

  const limpiarFiltros = () => {
    setFiltros(filtrosIniciales)
    setFiltrosAplicados(filtrosIniciales)
    setErrores([])
  }

  return (
    <main className="min-h-screen px-12 flex gap-8">
      
      <PanelFiltros
        filtros={filtros}
        errores={errores}
        onAplicar={handleAplicarFiltros}
        onLimpiar={limpiarFiltros}
        onActualizar={actualizar}
        onGenero={handleGenero}
        hoy={hoy}
      />

      <div className="flex flex-col gap-6 w-full">
        <BarraBusqueda
          busqueda={filtros.busqueda ?? ''}
          onChangeBusqueda={v => actualizar('busqueda', v)}
          onBuscar={handleBusqueda}
          onLimpiar={() => {
            actualizar('busqueda', '')
            setFiltrosAplicados(prev => ({ ...prev, busqueda: '' }))
          }}
        />
        
        {/* Header resultados */}
        <section className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-slate-800">Libros Populares</h2>

          <div className="flex items-center gap-2 text-sm text-slate-500">
            <span>Ordenar por:</span>
            <select value={filtros.orden} 
              onChange={handleOrden}
              className="border border-slate-200 rounded-md px-2 py-1 text-slate-700"
            >
              <option value="POPULARIDAD">Popularidad</option>
              <option value="TITULO_ASC">Título A→Z</option>
              <option value="TITULO_DESC">Título Z→A</option>
              <option value="AUTOR_ASC">Autor A→Z</option>
              <option value="DUENO_ASC">Dueño A→Z</option>
            </select>
          </div>
        </section>

        {/* Lista del resultado de libros con paginado */}
        <DataGrid<ReservaLibroHomeJSON, FiltrosLibro>
          fetchFunction={(req) => reservaService.getLibrosDisponiblesReserva(userId, req)}
          filter={filtrosAplicados}
          renderItem={(libro) => <LibroCard libro={libro} />}
          pageSize={filtros.tamanoPaginado!}
        />
      </div>
    </main>
  )
}