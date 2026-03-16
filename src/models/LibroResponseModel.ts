export interface LibroResumenResponseModel {
  titulo: string;
  autor: string;
  puntuacion: number;
  imgUrl: string;
}

export enum TipoDeLibro {
    COMUN = 'COMUN',
    DEDICATORIA = 'DEDICATORIA',
    COLECCIONABLE = 'COLECCIONABLE'
}

export enum Genero {
    DRAMA = 'DRAMA',
    CIENCIA_FICCION = 'CIENCIA_FICCION',
    ROMANCE = 'ROMANCE',
    AUTOAYUDA = 'AUTOAYUDA',
    DISENO = 'DISENO',
    LITERATURA_CLASICA = 'LITERATURA_CLASICA'
}
export enum Idioma {
    ESPANOL = 'ESPANOL',
    INGLES = 'INGLES',
    FRANCES = 'FRANCES',
    PORTUGUES = 'PORTUGUES'
}

export enum Estado {
    EXCELENTE = 'EXCELENTE',
    MUY_BUENO = 'MUY_BUENO',
    BUENO = 'BUENO',
    MALO = 'MALO',
    REGULAR = 'REGULAR'
}

export interface LibroDetalleResponse {
    id: number
    titulo: string
    tipo: TipoDeLibro
    genero: Genero
    autor: string
    idioma: Idioma
    estado: Estado
    editorial: string
    fechaPublicacion: string
    paginas: number
    isbn13: string
    descripcion: string
    portada: string
    idDueno: number
}