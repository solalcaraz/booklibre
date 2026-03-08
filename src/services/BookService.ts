/* import axios from "axios"; */

/* const API_URL = 'http://localhost:9000' */

import { Book } from "../classes/Book";

class BookService {
    async getAll() {
    // Lógica para traer todos los libros
    }
    async getById(id: string) {
    // Lógica para traer un libro específico para editar
    }
    async save(book: Book) {
    // Lógica para POST (crear) o PUT (editar)
    }
}

export const bookService = new BookService()