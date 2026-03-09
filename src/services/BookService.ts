import axios from "axios";
import type { BookFormData } from "../classes/Book";
import type { BookDetailResponse } from "../responses/BookResponse";
const API_URL = 'http://localhost:9000';

class BookService {
    // async getAll() {
    //     const response = await axios.get<BookResponse>(API_URL + '/libros')
    //     return response.data
    // }
    async getById(id: string): Promise<BookDetailResponse> {
        const response = await axios.get<BookDetailResponse>(API_URL + '/libro/' + id)
        return response.data
    }
    async save(bookData: BookFormData): Promise<void> {
        await axios.post(`${API_URL}/libro/nuevo`, this.mapToDTO(bookData), {
            params: { usuarioId: 1 } // Hardcodeado momentáneamente, tiene que tomar el id del usuario logueado
        });
    }

    async update(id: string, bookData: BookFormData): Promise<void> {
        await axios.put(`${API_URL}/libro/${id}`, this.mapToDTO(bookData));
    }

    private mapToDTO(bookData: BookFormData) {
        return {
            titulo: bookData.title,
            tipo: bookData.type,
            genero: bookData.genre,
            autor: bookData.author,
            idioma: bookData.language,
            estado: bookData.status,
            editorial: bookData.editorial,
            fechaPublicacion: bookData.publication_date,
            paginas: Number(bookData.pages),
            isbn13: bookData.isbn13,
            descripcion: bookData.description
        };
    }
}

export const bookService = new BookService()