-- Función que ejecuta el trigger para adutiar los cambios en el puntaje promedio de un libro cada vez que se inserta una nueva reseña
CREATE OR REPLACE FUNCTION fn_auditar_puntaje_libro()
    RETURNS TRIGGER AS $$

DECLARE
    puntaje_viejo  DOUBLE PRECISION;
    puntaje_actual DOUBLE PRECISION;

BEGIN
    -- Promedio anterior: todas las reseñas del libro EXCEPTO la recién insertada
    SELECT COALESCE(AVG(r.puntuacion), 0)
    INTO puntaje_viejo
    FROM resenias r
    WHERE r.id_libro = NEW.id_libro
      AND r.id <> NEW.id; -- excluye la reseña recién insertada para calcular el promedio anterior

    -- Promedio nuevo: todas las reseñas del libro INCLUYENDO la recién insertada
    SELECT COALESCE(AVG(r.puntuacion), 0)
    INTO puntaje_actual
    FROM resenias r
    WHERE r.id_libro = NEW.id_libro;

    -- Solo registra si el puntaje realmente cambió
    IF puntaje_viejo <> puntaje_actual THEN
        INSERT INTO auditoria_puntaje_libro
        (id_libro, fecha_actualizacion, puntaje_anterior, puntaje_nuevo)
        VALUES
            (NEW.id_libro, NOW(), puntaje_viejo, puntaje_actual);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger que se dispara al insertar una nueva reseña
DROP TRIGGER IF EXISTS tg_auditar_puntaje_libro ON resenias;
CREATE TRIGGER tg_auditar_puntaje_libro
    AFTER INSERT ON resenias
    FOR EACH ROW
EXECUTE FUNCTION fn_auditar_puntaje_libro();

-- function para ver el historial completo de un libro
CREATE OR REPLACE FUNCTION fn_historial_puntaje_libro(p_id_libro INTEGER)
    RETURNS TABLE (
                      id_libro            INTEGER,
                      titulo              VARCHAR,
                      veces_actualizado   BIGINT,
                      fecha_actualizacion TIMESTAMP,
                      puntaje_anterior    DOUBLE PRECISION,
                      puntaje_nuevo       DOUBLE PRECISION
                  )
AS $$
BEGIN
    RETURN QUERY
        SELECT
            a.id_libro,
            l.titulo,
            COUNT(*) OVER (PARTITION BY a.id_libro),
            a.fecha_actualizacion,
            a.puntaje_anterior,
            a.puntaje_nuevo
        FROM auditoria_puntaje_libro a
                 JOIN libros l ON a.id_libro = l.id
        WHERE a.id_libro = p_id_libro
        ORDER BY a.fecha_actualizacion;
END;
$$ LANGUAGE plpgsql;