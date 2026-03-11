export type BookFormData = {
    title: string;
    type: string;
    genre: string;
    author: string;
    language: string;
    status: string;
    editorial: string;
    publication_date:string;
    pages: string;
    isbn13: string;
    description: string;
}

export type BookErrors = Partial<Record<keyof BookFormData, string>>

export class Book {
    constructor(
        public id: string | number | null,
        public title: string,
        public type: string,
        public genre: string,
        public author: string,
        public language: string,
        public status: string,
        public editorial: string,
        public publication_date: string,
        public pages: string,
        public isbn13: string,
        public description: string
    ) {}

    // Validaciones para el formulario?
    validate() {
        const errors: BookErrors = {}
        if (!this.title.trim()) errors.title = 'El título es requerido'
        if (!this.type.trim()) errors.type = 'El tipo es requerido'
        if (!this.genre.trim()) errors.genre = 'El género es requerido'
        if (!this.author.trim()) errors.author = 'El autor es requerido'
        if (!this.language.trim()) errors.language = 'El título es requerido'
        if (!this.status.trim()) errors.status = 'El título es requerido'
        if (!this.editorial.trim()) errors.editorial = 'El título es requerido'
        if (this.pages && isNaN(Number(this.pages))) errors.pages = 'La cantidad de páginas debe ser un número'
        if (!this.isbn13.trim()) errors.isbn13 = 'El ISBN es requerido'
        if (!this.description.trim()) errors.description = 'Una descripción es requerida'
        return errors
    }

    static hasErrors(errors: BookErrors): boolean {
        return Object.keys(errors).length > 0
    }
}