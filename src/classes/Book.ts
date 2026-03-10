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

    validate() {
        const errors: BookErrors = {}
        const requiredFields: (keyof BookFormData)[] = [
            'title', 'type', 'genre', 'author', 'language', 
            'status', 'editorial', 'isbn13', 'description'
        ];
        requiredFields.forEach(field => {
            if (!this[field as keyof this]?.toString().trim()) {
                errors[field] = `El campo ${field} es requerido`;
            }
        });
        if (this.pages && isNaN(Number(this.pages))) {
            errors.pages = 'La cantidad de páginas debe ser un número';
        }
        return errors
    }

    static hasErrors(errors: BookErrors): boolean {
        return Object.keys(errors).length > 0
    }
}