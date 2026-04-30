-- Punto 5 - Listar los usuarios que tengan más de 2 reservas devueltas.

CREATE OR REPLACE VIEW usuarios_con_mas_de_2_reservas_devueltas AS
SELECT
    r.id_usuario,
    u.nombre_completo,
    COUNT(*) AS cantidad_reservas_devueltas
FROM reservas r
         JOIN usuarios u ON r.id_usuario = u.id
WHERE r.fecha_hasta < CURRENT_DATE
GROUP BY r.id_usuario, u.nombre_completo
HAVING COUNT(*) > 2;

-- Se usa como: SELECT * FROM usuarios_con_mas_de_2_reservas_devueltas;