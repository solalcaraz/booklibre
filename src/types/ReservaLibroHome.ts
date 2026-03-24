import type { Estado, Genero, Idioma, TipoDeLibro } from "../models/LibroResponseModel"

export type ReservaLibroHomeJSON = {
    id: number
    titulo: string
    genero: Genero
    autor: string
    tipo: TipoDeLibro
    isbn13: string
    idioma: Idioma
    estado: Estado
    dueno: string
    calificacionPromedio: number
    biblioKarmasTotal: number
    portada: string
}

export type FiltrosLibro = {
    busqueda?: string,
    generos?: Genero[],
    cantPaginasMin?: number,
    cantPaginasMax?: number,
    fechaDesde?: string,
    fechaHasta?: string,
    isbn?: string,
    usuarioPrestador?: string,
    orden?: Orden,
    numeroPagina?: number,
    tamanoPaginado?: number
}

export type Orden = | 'POPULARIDAD' | 'TITULO_ASC' | 'TITULO_DESC' | 'AUTOR_ASC' | 'DUENO_ASC'
