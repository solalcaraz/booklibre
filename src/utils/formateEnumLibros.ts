export const TIPO_OPTIONS: Record<string, string> = {
    COMUN: "Común",
    DEDICATORIA: "Dedicatoria",
    COLECCIONABLE: "Coleccionable"
};

// Los labels tienen que coincidir con los del Back
export const GENERO_OPTIONS: Record<string, string> = {
    DRAMA: "Drama",
    CIENCIA_FICCION: "Ciencia Ficción",
    ROMANCE: "Romance",
    AUTOAYUDA: "Autoayuda",
    DISENO: "Diseño",
    LITERATURA_CLASICA: "Literatura Clásica"
};

export const IDIOMA_OPTIONS: Record<string, string> = {
    ESPANOL: "Español",
    INGLES: "Inglés",
    FRANCES: "Francés",
    PORTUGUES: "Portugués"
};

export const ESTADO_OPTIONS: Record<string, string> = {
    EXCELENTE: "Excelente",
    MUY_BUENO: "Muy bueno",
    BUENO: "Bueno",
    MALO: "Malo",
    REGULAR: "Regular"
};

// Recibe un Label (del back) y devuelve la Key (para el back)
export const getKeyFromLabel = (options: Record<string, string>, label: string) => {
    return Object.keys(options).find(key => options[key] === label) || label;
}

export const formatEnum = (value: string): string => {
    return value
    .toLowerCase()
    .split("_")
    .map(palabra => palabra.charAt(0).toUpperCase() + palabra.slice(1))
    .join(" ")
}