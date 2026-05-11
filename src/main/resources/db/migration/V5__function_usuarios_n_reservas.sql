-- Punto 3 - Saber qué usuarios tienen más de N reservas

-- CREATE OR REPLACE FUNCTION usuarios_con_mas_de_n_reservas(n INTEGER)
--     RETURNS TABLE (
--         id_usuario INT,
--         cantidad_reservas BIGINT
--     )
-- AS $$
-- BEGIN
--     RETURN QUERY
--         SELECT r.id_usuario, COUNT(*) AS cantidad_reservas
--         FROM reservas r
--         GROUP BY r.id_usuario
--         HAVING COUNT(*) > n;
-- END;
-- $$ LANGUAGE plpgsql;

-- Se usa como SELECT * FROM usuarios_con_mas_de_n_reservas(1);