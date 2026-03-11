import { useState } from "react";
import { useNavigate, useParams } from "react-router";
import { libroService } from "../services/LibroService";
import { BaseInput } from "../components/BaseInput";
import { Libro, type LibroFormData, type LibroErrors } from "../classes/Libro";
import { GENERO_OPTIONS, IDIOMA_OPTIONS, ESTADO_OPTIONS, TIPO_OPTIONS } from "../constants/LibroConstants";
import { useOnInit } from "../utils/hooks";

const initialFormData: LibroFormData = {
    titulo: "", tipo: "", genero: "", autor: "", idioma: "",
    estado: "", editorial: "", fecha_publicacion: "", paginas: "0",
    isbn13: "", descripcion: ""
};

const generoOptions = Object.entries(GENERO_OPTIONS).map(([value, label]) => ({ value, label }));
const tipoOptions = Object.entries(TIPO_OPTIONS).map(([value, label]) => ({ value, label }));
const idiomaOptions = Object.entries(IDIOMA_OPTIONS).map(([value, label]) => ({ value, label }));
const estadoOptions = Object.entries(ESTADO_OPTIONS).map(([value, label]) => ({ value, label }));

export default function LibroForm() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [formData, setFormData] = useState<LibroFormData>(initialFormData);
    const [errors, setErrors] = useState<LibroErrors>({});

    const loadLibro = async () => {
    if (!id) return;
    try {
        const data = await libroService.getById(id);

        setFormData({
            titulo: data.titulo,
            tipo: data.tipo,
            genero: data.genero,
            autor: data.autor,
            idioma: data.idioma,
            estado: data.estado,
            editorial: data.editorial,
            fecha_publicacion: data.fechaPublicacion,
            isbn13: data.isbn13,
            descripcion: data.descripcion,

            paginas: data.paginas ? data.paginas.toString() : "0",
        });
    } catch (error) {
        console.error("Error al cargar el detalle del libro", error);
    }
};

    useOnInit(() => {
        loadLibro();
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setErrors(prev => ({ ...prev, [name]: undefined }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const LibroInstance = new Libro(
            id || null, formData.titulo, formData.tipo, formData.genero, formData.autor,
            formData.idioma, formData.estado, formData.editorial,
            formData.fecha_publicacion, formData.paginas, formData.isbn13, formData.descripcion
        );

        const validationErrors = LibroInstance.validate();
        setErrors(validationErrors);

        if (Libro.hasErrors(validationErrors)) return;

        try {
            if (id) {
                await libroService.update(id, formData);
            } else {
                await libroService.save(formData);
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
                        <BaseInput label="Título" type="text" name="titulo" value={formData.titulo} onChange={handleChange} error={errors.titulo} />
                    </div>

                    <BaseInput label="Tipo" type="select" name="type" value={formData.tipo} onChange={handleChange} options={tipoOptions} error={errors.tipo} />
                    <BaseInput label="Género" type="select" name="genero" value={formData.genero} onChange={handleChange} options={generoOptions} error={errors.genero} />

                    <BaseInput label="Autor" type="text" name="autor" value={formData.autor} onChange={handleChange} error={errors.autor} />
                    <BaseInput label="Idioma" type="select" name="idioma" value={formData.idioma} onChange={handleChange} options={idiomaOptions} error={errors.idioma} />

                    <BaseInput label="Estado" type="select" name="estado" value={formData.estado} onChange={handleChange} options={estadoOptions} error={errors.estado} />
                    <BaseInput label="Editorial" type="text" name="editorial" value={formData.editorial} onChange={handleChange} error={errors.editorial} />

                    <BaseInput label="Fecha Publicación" type="date" name="fecha_publicacion" value={formData.fecha_publicacion} onChange={handleChange} />
                    <BaseInput label="Páginas" type="number" name="paginas" value={formData.paginas} onChange={handleChange} error={errors.paginas} />

                    <div className="md:col-span-2">
                        <BaseInput label="ISBN-13" type="text" name="isbn13" value={formData.isbn13} onChange={handleChange} error={errors.isbn13} />
                        <BaseInput label="Descripción" type="textarea" name="descripcion" value={formData.descripcion} onChange={handleChange} error={errors.descripcion} />
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