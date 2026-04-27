-- Punto 1 - Conocer los Libros que reservó un determinado usuario en el corriente año.

CREATE OR REPLACE VIEW libros_reservados_usuario_anio_corriente AS
SELECT
    r.id_usuario,
    r.id_libro,
    l.titulo,
    r.fecha_desde
FROM public.reservas r
         JOIN public.libro l ON r.id_libro = l.id
WHERE EXTRACT(YEAR FROM r.fecha_desde) = EXTRACT(YEAR FROM CURRENT_DATE);

-- Se usa como SELECT * FROM libros_reservados_usuario_anio_corriente WHERE id_usuario = 1;

-- Punto 3 - Saber qué usuarios tienen más de N reservas

CREATE OR REPLACE FUNCTION usuarios_con_mas_de_n_reservas(n INTEGER)
    RETURNS TABLE (
        id_usuario INT,
        cantidad_reservas BIGINT
    )
AS $$
BEGIN
    RETURN QUERY
        SELECT r.id_usuario, COUNT(*) AS cantidad_reservas
        FROM reservas r
        GROUP BY r.id_usuario
        HAVING COUNT(*) > n;
END;
$$ LANGUAGE plpgsql;

-- Se usa como SELECT * FROM usuarios_con_mas_de_n_reservas(1);