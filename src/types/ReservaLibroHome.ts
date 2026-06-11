import type { Genero } from "../models/LibroResponseModel"

export type ReservaLibroHomeJSON = {
    id: number
    titulo: string
    genero: string
    autor: string
    tipo: string
    isbn13: string
    idioma: string
    estado: string
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
