-- Punto 1 - Conocer los Libros que reservó un determinado usuario en el corriente año.

-- CREATE OR REPLACE VIEW libros_reservados_usuario_anio_corriente AS
-- SELECT
--     r.id_usuario,
--     r.id_libro,
--     l.titulo,
--     r.fecha_desde
-- FROM reservas r
--          JOIN libros l ON r.id_libro = l.id
-- WHERE EXTRACT(YEAR FROM r.fecha_desde) = EXTRACT(YEAR FROM CURRENT_DATE);

-- Se usa como SELECT * FROM libros_reservados_usuario_anio_corriente WHERE id_usuario = 1;
