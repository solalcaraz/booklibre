export type LibroFormData = {
    titulo: string;
    tipo: string;
    genero: string;
    autor: string;
    idioma: string;
    estado: string;
    editorial: string;
    fecha_publicacion:string;
    paginas: string;
    isbn13: string;
    descripcion: string;
    portada: string;
}

export type LibroErrors = Partial<Record<keyof LibroFormData, string>>

export class Libro {
    constructor(
        public id: string | number | null,
        public titulo: string,
        public tipo: string,
        public genero: string,
        public autor: string,
        public idioma: string,
        public estado: string,
        public editorial: string,
        public fecha_publicacion: string,
        public paginas: string,
        public isbn13: string,
        public descripcion: string,
        public portada: string,
        public dueno: string
    ) {}

    validate() {
        const errors: LibroErrors = {}

        const requiredFields: (keyof LibroFormData)[] = [
            'titulo', 'tipo', 'genero', 'autor', 'idioma', 
            'estado', 'editorial', 'isbn13', 'descripcion'
        ];
        requiredFields.forEach(field => {
            if (!this[field as keyof this]?.toString().trim()) {
                errors[field] = `El campo ${field} es requerido`;
            }
        });

        if (this.paginas && isNaN(Number(this.paginas))) {
            errors.paginas = 'La cantidad de páginas debe ser un número';
        }
        
        return errors
    }

    static hasErrors(errors: LibroErrors): boolean {
        return Object.keys(errors).length > 0
    }
}