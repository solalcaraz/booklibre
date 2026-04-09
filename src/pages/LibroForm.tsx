import { useState } from "react";
import { useNavigate, useParams } from 'react-router-dom'
import { libroService } from "../services/LibroService";
import { BaseInput } from "../components/BaseInput";
import { Libro, type LibroFormData, type LibroErrors } from "../classes/Libro";
import { GENERO_OPTIONS, TIPO_OPTIONS, IDIOMA_OPTIONS, ESTADO_OPTIONS } from "../utils/formateEnumLibros";
import { useOnInit } from "../utils/hooks";
import toast from "react-hot-toast";

const initialFormData: LibroFormData = {
    titulo: "", tipo: "", genero: "", autor: "", idioma: "",
    estado: "", editorial: "", fecha_publicacion: "", paginas: "0",
    isbn13: "", descripcion: "", portada: ""
};

const generoOptions = Object.values(GENERO_OPTIONS).map(label => ({ value: label, label }));
const idiomaOptions = Object.values(IDIOMA_OPTIONS).map(label => ({ value: label, label }));
const estadoOptions = Object.values(ESTADO_OPTIONS).map(label => ({ value: label, label }));

const tipoOptions = Object.entries(TIPO_OPTIONS).map(([value, label]) => ({ value, label })); // El back lo manda como Key en el Mapper

export default function LibroForm() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [formData, setFormData] = useState<LibroFormData>(initialFormData);
    const [errors, setErrors] = useState<LibroErrors>({});
    //Para el manejo de errores de acceso (editar un libro que no es tuyo/no existe)
    const [accessError, setAccessError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const loadLibro = async () => {
    if (!id) return;
    try {
        const data = await libroService.getByIdFor(id);

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
            setAccessError("El servidor no responde. Por favor, intenta más tarde.");
        } else {
            setAccessError("¡Ocurrió un error inesperado! " + mensajeBack);
        }
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
            formData.fecha_publicacion, formData.paginas, formData.isbn13, formData.descripcion, formData.portada, ""
        );

        const validationErrors = LibroInstance.validate();
        setErrors(validationErrors);

        if (Libro.hasErrors(validationErrors)) {
            toast.error("Por favor, revisá los errores en el formulario");
            return;
        }

        setIsSubmitting(true);
        try {
            if (id) {
                await libroService.update(id, formData);
                toast.success("¡Libro actualizado con éxito!");
                //navigate(0); //Refresh forzado
                // navigate(`/libros/editar/${id}`) //Para usar con toasts
            } else {
                await libroService.create(formData);
                toast.success("¡Libro creado con éxito!");
                navigate('/profile');
            }
        } catch (error: any) {
            const mensajeBack = error.response?.data?.message;
            toast.error(mensajeBack || "Hubo un problema al guardar el libro");
            //alert(mensajeBack || "¡Error al guardar el libro! No se pudo conectar con el servidor. Intente de nuevo más tarde.");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (accessError) {
        return (
            <div className="flex min-h-screen flex-col items-center justify-center bg-[#f8f7f4]">
                <h2 className="text-2xl font-bold text-red-600 mb-4">¡Ups! Algo salió mal</h2>
                <p className="text-slate-600 mb-6">{accessError}</p>
                <button 
                    onClick={() => navigate('/profile')} 
                    className="rounded-lg bg-sky-500 px-6 py-2 font-bold text-white hover:bg-sky-600"
                >
                    Volver a mi perfil
                </button>
            </div>
        );
    }

    return (
        <section className="flex min-h-screen flex-col items-center justify-center bg-[#f8f7f4] px-4 py-10">
            <div className="w-full max-w-4xl overflow-hidden rounded-2xl bg-white shadow-lg">
                <header className="bg-[#f3f9ff] px-8 py-6 text-center">
                    <h1 className="text-2xl font-bold text-slate-800">{id ? "Editar Libro" : "Nuevo Libro"}</h1>
                </header>

                <form onSubmit={handleSubmit} className="p-8 grid grid-cols-1 md:grid-cols-3 gap-8">
                    {/* COLUMNA IZQUIERDA: Portada e Input de Enlace */}
                    <div className="flex flex-col gap-4">
                        <label className="block text-sm font-semibold text-slate-600">Portada</label>
                        <div className="relative aspect-[3/4] w-full overflow-hidden rounded-lg border-2 border-dashed border-slate-200 bg-slate-50 flex items-center justify-center">
                            {formData.portada ? (
                                <img 
                                    src={formData.portada} 
                                    alt="Vista previa" 
                                    className="h-full w-full object-cover"
                                    onError={(e) => (e.currentTarget.src = "https://via.placeholder.com/300x400?text=Error+Imagen")}
                                />
                            ) : (
                                <span className="text-slate-400 text-sm text-center px-4 italic">Sin vista previa</span>
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

                    {/* COLUMNA DERECHA: Datos del Libro */}
                    <div className="md:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-5">
                        <div className="md:col-span-2">
                            <BaseInput label="Título" type="text" name="titulo" value={formData.titulo} onChange={handleChange} error={errors.titulo} />
                        </div>

                        <BaseInput label="Tipo" type="select" name="tipo" value={formData.tipo} onChange={handleChange} options={tipoOptions} error={errors.tipo} />
                        <BaseInput label="Género" type="select" name="genero" value={formData.genero} onChange={handleChange} options={generoOptions} error={errors.genero} />

                        <BaseInput label="Autor" type="text" name="autor" value={formData.autor} onChange={handleChange} error={errors.autor} />
                        <BaseInput label="Idioma" type="select" name="idioma" value={formData.idioma} onChange={handleChange} options={idiomaOptions} error={errors.idioma} />

                        <BaseInput label="Estado" type="select" name="estado" value={formData.estado} onChange={handleChange} options={estadoOptions} error={errors.estado} />
                        <BaseInput label="Editorial" type="text" name="editorial" value={formData.editorial} onChange={handleChange} error={errors.editorial} />

                        <BaseInput label="Fecha Publicación" type="date" name="fecha_publicacion" value={formData.fecha_publicacion} onChange={handleChange} error={errors.fecha_publicacion} />
                        <BaseInput label="Páginas" type="number" name="paginas" value={formData.paginas} onChange={handleChange} error={errors.paginas} />

                        <div className="md:col-span-2">
                            <BaseInput label="ISBN-13" type="text" name="isbn13" value={formData.isbn13} onChange={handleChange} error={errors.isbn13} />
                            <BaseInput label="Descripción" type="textarea" name="descripcion" value={formData.descripcion} onChange={handleChange} error={errors.descripcion} />
                        </div>

                        {/* Botones de acción */}
                        <div className="md:col-span-2 flex gap-4 mt-4">
                            <button 
                                type="submit" 
                                disabled={isSubmitting}
                                className={`flex-1 rounded-lg py-3 font-bold text-slate-800 transition cursor-pointer ${isSubmitting ? 'bg-slate-300' : 'bg-[#7bedac] hover:brightness-95'}`}
                            >
                                {isSubmitting ? "Guardando..." : (id ? "Guardar Cambios" : "Añadir Libro")}
                            </button>
                            <button type="button" onClick={() => navigate(-1)} className="flex-1 rounded-lg bg-slate-100 py-3 font-bold text-slate-500 hover:bg-slate-200 transition cursor-pointer">
                                Cancelar
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </section>
    );
}