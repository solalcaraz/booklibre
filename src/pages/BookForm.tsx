import { useState } from "react";
import { useNavigate, useParams } from "react-router";
import { bookService } from "../services/BookService";
import { BaseInput } from "../components/BaseInput";
import { Book, type BookFormData, type BookErrors } from "../classes/Book";
import { GENRE_OPTIONS, LANGUAGE_OPTIONS, STATUS_OPTIONS, TYPE_OPTIONS } from "../constants/bookConstants";
import { useOnInit } from "../utils/hooks";

const initialFormData: BookFormData = {
    title: "", type: "", genre: "", author: "", language: "",
    status: "", editorial: "", publication_date: "", pages: "0",
    isbn13: "", description: ""
};

const genreOptions = Object.entries(GENRE_OPTIONS).map(([value, label]) => ({ value, label }));
const typeOptions = Object.entries(TYPE_OPTIONS).map(([value, label]) => ({ value, label }));
const languageOptions = Object.entries(LANGUAGE_OPTIONS).map(([value, label]) => ({ value, label }));
const statusOptions = Object.entries(STATUS_OPTIONS).map(([value, label]) => ({ value, label }));

export default function BookForm() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [formData, setFormData] = useState<BookFormData>(initialFormData);
    const [errors, setErrors] = useState<BookErrors>({});

    const loadBook = async () => {
    if (!id) return;
    try {
        const data = await bookService.getById(id);

        setFormData({
            title: data.titulo,
            type: data.tipo,
            genre: data.genero,
            author: data.autor,
            language: data.idioma,
            status: data.estado,
            editorial: data.editorial,
            publication_date: data.fechaPublicacion,
            isbn13: data.isbn13,
            description: data.descripcion,

            pages: data.paginas ? data.paginas.toString() : "0",
        });
    } catch (error) {
        console.error("Error al cargar el detalle del libro", error);
    }
};

    useOnInit(() => {
        loadBook();
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setErrors(prev => ({ ...prev, [name]: undefined }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const bookInstance = new Book(
            id || null, formData.title, formData.type, formData.genre, formData.author,
            formData.language, formData.status, formData.editorial,
            formData.publication_date, formData.pages, formData.isbn13, formData.description
        );

        const validationErrors = bookInstance.validate();
        setErrors(validationErrors);

        if (Book.hasErrors(validationErrors)) return;

        try {
            if (id) {
                await bookService.update(id, formData);
            } else {
                await bookService.save(formData);
            }
            navigate("/mis-libros");
        } catch (error) {
            console.error("Error al persistir el libro:", error);
        }
    };

    return (
        <section className="flex min-h-screen flex-col items-center justify-center bg-[#f8f7f4] px-4 py-10">
            <div className="w-full max-w-2xl overflow-hidden rounded-2xl bg-white shadow-lg">
                <header className="bg-[#f3f9ff] px-8 py-6 text-center">
                    <h1 className="text-2xl font-bold text-slate-800">{id ? "Editar Libro" : "Nuevo Libro"}</h1>
                </header>

                <form onSubmit={handleSubmit} className="p-8 grid grid-cols-1 md:grid-cols-2 gap-5">
                    <div className="md:col-span-2">
                        <BaseInput label="Título" type="text" name="title" value={formData.title} onChange={handleChange} error={errors.title} />
                    </div>

                    <BaseInput label="Tipo" type="select" name="type" value={formData.type} onChange={handleChange} options={typeOptions} error={errors.type} />
                    <BaseInput label="Género" type="select" name="genre" value={formData.genre} onChange={handleChange} options={genreOptions} error={errors.genre} />

                    <BaseInput label="Autor" type="text" name="author" value={formData.author} onChange={handleChange} error={errors.author} />
                    <BaseInput label="Idioma" type="select" name="language" value={formData.language} onChange={handleChange} options={languageOptions} error={errors.language} />

                    <BaseInput label="Estado" type="select" name="status" value={formData.status} onChange={handleChange} options={statusOptions} error={errors.status} />
                    <BaseInput label="Editorial" type="text" name="editorial" value={formData.editorial} onChange={handleChange} error={errors.editorial} />

                    <BaseInput label="Fecha Publicación" type="date" name="publication_date" value={formData.publication_date} onChange={handleChange} />
                    <BaseInput label="Páginas" type="number" name="pages" value={formData.pages} onChange={handleChange} error={errors.pages} />

                    <div className="md:col-span-2">
                        <BaseInput label="ISBN-13" type="text" name="isbn13" value={formData.isbn13} onChange={handleChange} error={errors.isbn13} />
                        <BaseInput label="Descripción" type="textarea" name="description" value={formData.description} onChange={handleChange} error={errors.description} />
                    </div>

                    <div className="md:col-span-2 flex gap-4 mt-4">
                        <button type="submit" className="flex-1 rounded-lg bg-[#7bedac] py-3 font-bold text-slate-800 hover:brightness-95 transition cursor-pointer">
                            {id ? "Guardar Cambios" : "Añadir Libro"}
                        </button>
                        <button type="button" onClick={() => navigate(-1)} className="flex-1 rounded-lg bg-slate-100 py-3 font-bold text-slate-500 hover:bg-slate-200 transition cursor-pointer">
                            Cancelar
                        </button>
                    </div>
                </form>
            </div>
        </section>
    );
}