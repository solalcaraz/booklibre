import { Genero, Idioma, Estado, TipoDeLibro } from "../models/LibroResponseModel";

export const TIPO_OPTIONS: Record<TipoDeLibro, string> = {
    [TipoDeLibro.COMUN]: "Común",
    [TipoDeLibro.DEDICATORIA]: "Dedicatoria",
    [TipoDeLibro.COLECCIONABLE]: "Coleccionable"
};
export const GENERO_OPTIONS: Record<Genero, string> = {
    [Genero.DRAMA]: "Drama",
    [Genero.CIENCIA_FICCION]: "Ciencia ficción",
    [Genero.ROMANCE]: "Romance",
    [Genero.AUTOAYUDA]: "Autoayuda",
    [Genero.DISENO]: "Diseño",
    [Genero.LITERATURA_CLASICA]: "Literatura clásica"
};
export const IDIOMA_OPTIONS: Record<Idioma, string> = {
    [Idioma.ESPANOL]: "Español",
    [Idioma.INGLES]: "Inglés",
    [Idioma.FRANCES]: "Francés",
    [Idioma.PORTUGUES]: "Portugués"
};
export const ESTADO_OPTIONS: Record<Estado, string> = {
    [Estado.EXCELENTE]: "Excelente",
    [Estado.MUY_BUENO]: "Muy bueno",
    [Estado.BUENO]: "Bueno",
    [Estado.MALO]: "Malo",
    [Estado.REGULAR]: "Regular"
};