-- Hash fijo generado previamente que traducido es "admin"
-- $2a$10$FJ8PeifnHjsBhGI5CAPn/uQwzCrSyybEtsa6Owz9HOWH.zfMRfRTi

-- ============================================================
-- USUARIOS
-- ============================================================
INSERT INTO usuarios (nombre_completo, email, password, ano_unido, tipo_usuario, descripcion, ciudad, telefono, bibliokarmas, imagen)
VALUES
    -- id = 1 : Diego Maradona
    (
        'Diego Maradona',
        'diego@maradona.com',
        '$2a$10$FJ8PeifnHjsBhGI5CAPn/uQwzCrSyybEtsa6Owz9HOWH.zfMRfRTi',
        EXTRACT(YEAR FROM NOW()),
        'LECTOR_PUBLICADOR',
        'La Mano de Dios',
        'Lanús, BA',
        '+54 (11) 123-4567',
        1986,
        'http://localhost:9000/imagenes/diego.jpg'
    ),
    -- id = 2 : Max Verstappen
    (
        'Max Verstappen',
        'max@verstappen.com',
        '$2a$10$FJ8PeifnHjsBhGI5CAPn/uQwzCrSyybEtsa6Owz9HOWH.zfMRfRTi',
        EXTRACT(YEAR FROM NOW()),
        'LECTOR_PUBLICADOR',
        'The Flying Dutchman',
        'Maaseik, Bélgica',
        '+54 (11) 123-4567',
        2023,
        'https://cdn.crash.net/2025-10/si202510191331.jpg?width=1600&aspect_ratio=1:1'
    ),
    -- id = 3 : Ye West
    (
        'Ye West',
        'yewest@bully.com',
        '$2a$10$FJ8PeifnHjsBhGI5CAPn/uQwzCrSyybEtsa6Owz9HOWH.zfMRfRTi',
        EXTRACT(YEAR FROM NOW()),
        'PUBLICADOR',
        '',
        '',
        '',
        0,
        'http://localhost:9000/imagenes/default_user.png'
    ),
    -- id = 4 : Yiya Murano
    (
        'Yiya Murano',
        'yiyi@killer.com',
        '$2a$10$FJ8PeifnHjsBhGI5CAPn/uQwzCrSyybEtsa6Owz9HOWH.zfMRfRTi',
        EXTRACT(YEAR FROM NOW()),
        'LECTOR',
        '',
        '',
        '',
        0,
        'http://localhost:9000/imagenes/default_user.png'
    );

-- ============================================================
-- LIBROS
-- id_usuario 1 = Maradona   → libros 1, 2, 3
-- id_usuario 2 = Verstappen → libros 4, 5, 6, 9, 10, 11, 12, 13, 14
-- id_usuario 3 = Ye West    → libros 7, 8
-- ============================================================
-- INSERT INTO libros (tipo_libro, id_usuario, titulo, descripcion, genero, autor, cant_paginas, isbn13, idioma, editorial, fecha_publicacion, estado, portada, activo)
-- VALUES
--     -- 1
--     ('COLECCIONABLE', 1, 'Orgullo y Prejuicio',    'Novela romántica clásica',
--      'ROMANCE',           'Jane Austen',            300,  '9780141439518', 'INGLES',   'Penguin',           '1800-01-01', 'MUY_BUENO',   'http://localhost:9000/imagenes/orgullo.webp',  TRUE),
--     -- 2
--     ('DEDICATORIA',   1, 'Rayuela',                'Novela experimental latinoamericana',
--      'LITERATURA_CLASICA','Julio Cortázar',          736,  '9788437604947', 'ESPANOL',  'Cátedra',           '1960-01-01', 'BUENO',       'http://localhost:9000/imagenes/Rayuela.jpg',   TRUE),
--     -- 3
--     ('COLECCIONABLE', 1, '1984',                   'Distopía política sobre vigilancia y control social',
--      'CIENCIA_FICCION',   'George Orwell',           328,  '9780451524935', 'INGLES',   'Signet Classics',   '1900-01-01', 'MUY_BUENO',   'http://localhost:9000/imagenes/1984.webp',     TRUE),
--     -- 4
--     ('COLECCIONABLE', 2, 'Dune',                   'Épica de ciencia ficción sobre política, religión y ecología',
--      'CIENCIA_FICCION',   'Frank Herbert',           688,  '9780441172719', 'INGLES',   'Ace',               '1900-01-01', 'EXCELENTE',   'http://localhost:9000/imagenes/dune.jpg',      TRUE),
--     -- 5
--     ('DEDICATORIA',   2, 'Cien Años de Soledad',   'Obra maestra del realismo mágico latinoamericano',
--      'LITERATURA_CLASICA','Gabriel García Márquez',  417,  '9780307474728', 'ESPANOL',  'Sudamericana',      '1900-01-01', 'MUY_BUENO',   'http://localhost:9000/imagenes/100anos.webp',  TRUE),
--     -- 6
--     ('COMUN',         2, 'Hábitos Atómicos',       'Guía práctica para construir buenos hábitos y eliminar los malos',
--      'AUTOAYUDA',         'James Clear',             320,  '9780735211292', 'INGLES',   'Avery',             '1900-01-01', 'BUENO',       'http://localhost:9000/imagenes/habitos.webp',  TRUE),
--     -- 7
--     ('COLECCIONABLE', 3, 'El Nombre del Viento',   'Primera parte de la Crónica del Asesino de Reyes',
--      'CIENCIA_FICCION',   'Patrick Rothfuss',        662,  '9780756404741', 'INGLES',   'DAW Books',         '1900-01-01', 'EXCELENTE',   'http://localhost:9000/imagenes/viento.webp',   TRUE),
--     -- 8
--     ('DEDICATORIA',   3, 'El Gran Gatsby',         'Retrato de la alta sociedad estadounidense en los años 20',
--      'LITERATURA_CLASICA','F. Scott Fitzgerald',     180,  '9780743273565', 'INGLES',   'Scribner',          '1900-01-01', 'BUENO',       'http://localhost:9000/imagenes/libro-nuevo.jpg', TRUE),
--     -- 9
--     ('COMUN',         2, 'El Principito',          'Clásico de la literatura sobre la amistad y la vida',
--      'CIENCIA_FICCION',   'Antoine de Saint-Exupéry', 96, '9780156013987', 'FRANCES',  'Reynal & Hitchcock','1943-04-06', 'BUENO',       'https://image.cdn1.buscalibre.com/5b57fc1690f0b5295a8b4567.RS500x500.jpg', TRUE),
--     -- 10
--     ('COMUN',         2, '1984',                   'Distopía sobre un régimen totalitario',
--      'CIENCIA_FICCION',   'George Orwell',           328,  '9780451524935', 'INGLES',   'Secker & Warburg',  '1949-06-08', 'BUENO',       'http://localhost:9000/imagenes/1984.webp',     TRUE),
--     -- 11
--     ('COMUN',         2, 'Clean Code',             'Buenas prácticas para escribir código limpio',
--      'DISENO',            'Robert C. Martin',        464,  '9780132350884', 'INGLES',   'Prentice Hall',     '2008-08-01', 'BUENO',       'https://m.media-amazon.com/images/I/81Rnac2Fq+L._AC_UF1000,1000_QL80_.jpg', TRUE),
--     -- 12
--     ('COMUN',         2, 'El Señor de los Anillos','Épica aventura en la Tierra Media',
--      'LITERATURA_CLASICA','J.R.R. Tolkien',          1178, '9780618640157', 'INGLES',   'Allen & Unwin',     '1954-07-29', 'BUENO',       'https://image.cdn0.buscalibre.com/5b5ae93e4463b568748b4567.RS500x500.jpg', TRUE),
--     -- 13
--     ('COMUN',         2, 'Padre Rico Padre Pobre', 'Educación financiera básica',
--      'AUTOAYUDA',         'Robert Kiyosaki',         336,  '9781612680194', 'INGLES',   'Plata Publishing',  '1997-04-01', 'BUENO',       'https://www.edicontinente.com.ar/image/titulos/9788466373005.jpg', TRUE),
--     -- 14
--     ('COMUN',         2, 'Sapiens',                'Historia breve de la humanidad',
--      'LITERATURA_CLASICA','Yuval Noah Harari',       443,  '9780062316097', 'INGLES',   'Harper',            '2011-01-01', 'BUENO',       'https://images.cdn1.buscalibre.com/fit-in/360x360/57/3d/573d2d38b86840ce6c919c61ac6d217f.jpg', TRUE);

-- ============================================================
-- RESERVAS
-- ============================================================
INSERT INTO reservas (id_libro, id_usuario, fecha_desde, fecha_hasta, ya_resenio)
VALUES
    -- reserva 1 : libro 1, lector = usuario 2 (Verstappen reserva libro de Maradona)
    ('libro_001',  2, '2026-03-07', '2026-03-14', FALSE),
    -- reserva 2 : libro 2, lector = usuario 2
    ('libro_002',  2, '2026-03-03', '2026-03-12', FALSE),
    -- reserva 3 : libro 3, lector = usuario 2
    ('libro_003',  2, '2026-03-08', '2026-03-14', FALSE),
    -- reserva 4 : libro 4, lector = usuario 1 (Maradona reserva libro de Verstappen)
    ('libro_004',  1, '2026-03-07', '2026-05-25', FALSE),
    -- reserva 5 : libro 5, lector = usuario 1
    ('libro_005',  1, '2026-03-28', '2026-04-14', FALSE),
    -- reserva 6 : libro 6, lector = usuario 1
    ('libro_006',  1, '2026-03-07', '2026-03-10', FALSE),
    -- reserva 7 : libro 7, lector = usuario 2 (Verstappen reserva libro de Ye West)
    ('libro_007',  2, '2026-01-05', '2026-01-10', FALSE),
    -- reserva 8 : libro 8, lector = usuario 2
    ('libro_008',  2, '2026-01-10', '2026-01-12', FALSE),
    -- reserva 9 : libro 7, lector = usuario 1 (Maradona reserva libro de Ye West)
    ('libro_007',  1, '2026-03-20', '2026-03-25', FALSE),
    -- reserva 10 : libro 9, lector = usuario 1
    ('libro_009',  1, '2026-03-17', '2026-03-25', FALSE),
    -- reserva 11 : libro 11, lector = usuario 1
    ('libro_011', 1, '2026-03-10', '2026-04-21', FALSE),
    -- reserva 12 : libro 13, lector = usuario 1
    ('libro_013', 1, '2026-02-24', '2026-04-30', FALSE),
    -- reserva 13 : libro 1, lector = usuario 3 (Ye West reserva libro de Maradona)
    ('libro_001',  3, '2025-02-24', '2025-04-30', FALSE);

-- ============================================================
-- RESEÑAS
-- reserva 1  → 4 estrellas  "Libro buenardo"       (lector = Verstappen, libro = Orgullo y Prejuicio)
-- reserva 2  → 3 estrellas  "Lo lei y no lo entendi"
-- reserva 13 → 5 estrellas  "Epico"                (lector = Ye West,    libro = Orgullo y Prejuicio)
-- ============================================================
INSERT INTO resenias (nombre_usuario, puntuacion, comentario, id_libro, fecha)
VALUES
    ('Max Verstappen', 4, 'Libro buenardo',         'libro_001', NOW()),
    ('Max Verstappen', 3, 'Lo lei y no lo entendi', 'libro_002', NOW()),
    ('Ye West',        5, 'Epico',                  'libro_001', NOW());

-- Marcar las reservas que ya tienen reseña
UPDATE reservas SET ya_resenio = TRUE WHERE id IN (1, 2, 13);