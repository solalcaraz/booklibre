import { useState } from "react";
import { useNavigate, useParams } from 'react-router-dom'
import { libroService } from "../services/LibroService";
import { BaseInput } from "../components/BaseInput";
import { Libro, type LibroFormData, type LibroErrors } from "../classes/Libro";
import { GENERO_OPTIONS, TIPO_OPTIONS, IDIOMA_OPTIONS, ESTADO_OPTIONS } from "../utils/formateEnumLibros";
import { useOnInit } from "../utils/hooks";
import toast from "react-hot-toast";
import { authService } from "../services/AuthService";

const initialFormData: LibroFormData = {
    titulo: "", tipo: "", genero: "", autor: "", idioma: "",
    estado: "", editorial: "", fecha_publicacion: "", paginas: "0",
    isbn13: "", descripcion: "", portada: ""
};

const generoOptions = Object.values(GENERO_OPTIONS).map(label => ({ value: label, label }));
const idiomaOptions = Object.values(IDIOMA_OPTIONS).map(label => ({ value: label, label }));
const estadoOptions = Object.values(ESTADO_OPTIONS).map(label => ({ value: label, label }));

// El back devuelve el tipo como clave del enum (COMUN) y el resto de los campos como label
const tipoOptions = Object.entries(TIPO_OPTIONS).map(([value, label]) => ({ value, label }));

export default function LibroForm() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [formData, setFormData] = useState<LibroFormData>(initialFormData);
    const [errors, setErrors] = useState<LibroErrors>({});
    const [accessError, setAccessError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const checkPermisos = async () => {
        try {      
            if (!authService.puedePublicar()) {
                setAccessError("No tenés permisos para publicar libros.");
            }
        } catch (error) {
            setAccessError("Error de acceso al usuario: " + error)
        }
    }

    const loadLibro = async () => {
    if (!id) return;
    try {
        const data = await libroService.getByIdForUser(id);

        setFormData({
            ...initialFormData,
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
            portada: data.portada,

            paginas: data.paginas ? data.paginas.toString() : "0",
        });
    } catch (error: any) {
        const status = error.response?.status;
        const mensajeBack = error.response?.data?.message;

        if (status === 403) {
            setAccessError(mensajeBack || "No tenés permisos para modificar este libro.");
        } else if (status === 404) {
            setAccessError(mensajeBack || "Este libro no existe.");
        } else if (status >= 500 || !status) { 
            setAccessError("El servidor no responde. Probá de nuevo en un rato.");
        } else {
            setAccessError("No pudimos cargar el libro: " + mensajeBack);
        }
    }
};

    useOnInit(() => {
        checkPermisos();
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
            formData.fecha_publicacion, formData.paginas, formData.isbn13, formData.descripcion, formData.portada
        );

        const validationErrors = LibroInstance.validate();
        setErrors(validationErrors);

        if (Libro.hasErrors(validationErrors)) {
            toast.error("Revisá los campos marcados en rojo");
            return;
        }

        setIsSubmitting(true);
        try {
            if (id) {
                await libroService.update(id, formData);
                toast.success("Guardamos los cambios del libro");
            } else {
                await libroService.create(formData);
                toast.success("Tu libro ya está publicado");
                navigate('/profile');
            }
        } catch (error: any) {
            const mensajeBack = error.response?.data?.message;
            toast.error(mensajeBack || "Hubo un problema al guardar el libro");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (accessError) {
        return (
            <div className="flex min-h-screen flex-col items-center justify-center bg-papel">
                <h2 className="mb-4 font-display text-2xl font-semibold text-stone-900">No pudimos abrir el formulario</h2>
                <p className="text-stone-600 mb-6">{accessError}</p>
                <button 
                    onClick={() => navigate('/profile')} 
                    className="cursor-pointer rounded-lg bg-tinta-700 px-6 py-2 font-semibold text-white hover:bg-tinta-800"
                >
                    Volver a mi perfil
                </button>
            </div>
        );
    }

    return (
        <section className="flex min-h-screen flex-col items-center justify-center bg-papel px-4 py-10">
            <div className="w-full max-w-4xl overflow-hidden rounded-2xl border border-stone-200 bg-white">
                <header className="border-b border-dashed border-stone-200 px-8 py-6">
                    <h1 className="font-display text-2xl font-semibold text-stone-900">{id ? "Editar libro" : "Publicar un libro"}</h1>
                    <p className="mt-1 text-sm text-stone-600">
                        {id ? "El tipo no se puede cambiar: define cómo se calculan los bibliokarmas." : "El tipo define cuántos bibliokarmas suma cada reserva y no se puede cambiar después."}
                    </p>
                </header>

                <form onSubmit={handleSubmit} className="p-8 grid grid-cols-1 md:grid-cols-3 gap-8">
                    <div className="flex flex-col gap-4">
                        <label className="block text-sm font-medium text-stone-700">Portada</label>
                        <div className="relative aspect-[3/4] w-full overflow-hidden rounded-lg border-2 border-dashed border-stone-200 bg-stone-50 flex items-center justify-center">
                            {formData.portada ? (
                                <img 
                                    src={formData.portada} 
                                    alt="Vista previa" 
                                    className="h-full w-full object-cover"
                                    onError={(e) => (e.currentTarget.src = "https://via.placeholder.com/300x400?text=Error+Imagen")}
                                />
                            ) : (
                                <span className="px-4 text-center text-sm text-stone-500">Pegá el enlace de una imagen para ver la portada</span>
                            )}
                        </div>
                        <BaseInput 
                            label="Enlace de la imagen" 
                            type="text" 
                            name="portada" 
                            placeholder="https://ejemplo.com/imagen.jpg"
                            value={formData.portada} 
                            onChange={handleChange} 
                            error={errors.portada} 
                        />
                    </div>

                    <div className="md:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-5">
                        <div className="md:col-span-2">
                            <BaseInput label="Título" type="text" name="titulo" value={formData.titulo} onChange={handleChange} error={errors.titulo} />
                        </div>

                        <BaseInput label="Tipo" type="select" name="tipo" value={formData.tipo} onChange={handleChange} options={tipoOptions} error={errors.tipo} disabled={!!id} />
                        <BaseInput label="Género" type="select" name="genero" value={formData.genero} onChange={handleChange} options={generoOptions} error={errors.genero} />

                        <BaseInput label="Autor" type="text" name="autor" value={formData.autor} onChange={handleChange} error={errors.autor} />
                        <BaseInput label="Idioma" type="select" name="idioma" value={formData.idioma} onChange={handleChange} options={idiomaOptions} error={errors.idioma} />

                        <BaseInput label="Estado" type="select" name="estado" value={formData.estado} onChange={handleChange} options={estadoOptions} error={errors.estado} />
                        <BaseInput label="Editorial" type="text" name="editorial" value={formData.editorial} onChange={handleChange} error={errors.editorial} />

                        <BaseInput label="Fecha de publicación" type="date" name="fecha_publicacion" value={formData.fecha_publicacion} onChange={handleChange} error={errors.fecha_publicacion} />
                        <BaseInput label="Páginas" type="number" name="paginas" value={formData.paginas} onChange={handleChange} error={errors.paginas} />

                        <div className="md:col-span-2">
                            <BaseInput label="ISBN-13" type="text" name="isbn13" value={formData.isbn13} onChange={handleChange} error={errors.isbn13} />
                            <BaseInput label="Descripción" type="textarea" name="descripcion" value={formData.descripcion} onChange={handleChange} error={errors.descripcion} />
                        </div>

                        <div className="md:col-span-2 flex gap-4 mt-4">
                            <button 
                                type="submit" 
                                disabled={isSubmitting}
                                className={`flex-1 cursor-pointer rounded-lg py-3 font-semibold text-white transition ${isSubmitting ? 'bg-stone-400' : 'bg-tinta-700 hover:bg-tinta-800'}`}
                            >
                                {isSubmitting ? "Guardando…" : (id ? "Guardar cambios" : "Añadir libro")}
                            </button>
                            <button type="button" onClick={() => navigate(-1)} className="flex-1 cursor-pointer rounded-lg py-3 font-medium text-stone-600 transition hover:bg-stone-100">
                                Cancelar
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </section>
    );
}