import { Book } from "../classes/Book";
import axios from "axios";
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
    async save(book: Book) {
    // Lógica para POST (crear) o PUT (editar)
    }
}

export const bookService = new BookService()