ALTER TABLE reservas
    ADD COLUMN libro_titulo VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN libro_autor VARCHAR(150) NOT NULL DEFAULT '',
    ADD COLUMN libro_puntuacion DOUBLE PRECISION DEFAULT 0,
    ADD COLUMN libro_img_url VARCHAR(500) NOT NULL DEFAULT '',
    ADD COLUMN libro_dueno_id INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN libro_dueno_nombre VARCHAR(100) NOT NULL DEFAULT '',
    ADD COLUMN bibliokarmas_ganados INTEGER NOT NULL DEFAULT 0;

UPDATE reservas
SET
    libro_titulo = CASE id_libro
        WHEN 'libro_001' THEN 'Orgullo y Prejuicio'
        WHEN 'libro_002' THEN 'Rayuela'
        WHEN 'libro_003' THEN '1984'
        WHEN 'libro_004' THEN 'Dune'
        WHEN 'libro_005' THEN 'Cien Años de Soledad'
        WHEN 'libro_006' THEN 'Hábitos Atómicos'
        WHEN 'libro_007' THEN 'El Nombre del Viento'
        WHEN 'libro_008' THEN 'El Gran Gatsby'
        WHEN 'libro_009' THEN 'El Principito'
        WHEN 'libro_010' THEN '1984'
        WHEN 'libro_011' THEN 'Clean Code'
        WHEN 'libro_012' THEN 'El Señor de los Anillos'
        WHEN 'libro_013' THEN 'Padre Rico Padre Pobre'
        WHEN 'libro_014' THEN 'Sapiens'
        ELSE libro_titulo
    END,
    libro_autor = CASE id_libro
        WHEN 'libro_001' THEN 'Jane Austen'
        WHEN 'libro_002' THEN 'Julio Cortázar'
        WHEN 'libro_003' THEN 'George Orwell'
        WHEN 'libro_004' THEN 'Frank Herbert'
        WHEN 'libro_005' THEN 'Gabriel García Márquez'
        WHEN 'libro_006' THEN 'James Clear'
        WHEN 'libro_007' THEN 'Patrick Rothfuss'
        WHEN 'libro_008' THEN 'F. Scott Fitzgerald'
        WHEN 'libro_009' THEN 'Antoine de Saint-Exupéry'
        WHEN 'libro_010' THEN 'George Orwell'
        WHEN 'libro_011' THEN 'Robert C. Martin'
        WHEN 'libro_012' THEN 'J.R.R. Tolkien'
        WHEN 'libro_013' THEN 'Robert Kiyosaki'
        WHEN 'libro_014' THEN 'Yuval Noah Harari'
        ELSE libro_autor
    END,
    libro_img_url = CASE id_libro
        WHEN 'libro_001' THEN 'http://localhost:9000/imagenes/orgullo.webp'
        WHEN 'libro_002' THEN 'http://localhost:9000/imagenes/Rayuela.jpg'
        WHEN 'libro_003' THEN 'http://localhost:9000/imagenes/1984.webp'
        WHEN 'libro_004' THEN 'http://localhost:9000/imagenes/dune.jpg'
        WHEN 'libro_005' THEN 'http://localhost:9000/imagenes/100anos.webp'
        WHEN 'libro_006' THEN 'http://localhost:9000/imagenes/habitos.webp'
        WHEN 'libro_007' THEN 'http://localhost:9000/imagenes/viento.webp'
        WHEN 'libro_008' THEN 'http://localhost:9000/imagenes/libro-nuevo.jpg'
        WHEN 'libro_009' THEN 'https://image.cdn1.buscalibre.com/5b57fc1690f0b5295a8b4567.RS500x500.jpg'
        WHEN 'libro_010' THEN 'http://localhost:9000/imagenes/1984.webp'
        WHEN 'libro_011' THEN 'https://m.media-amazon.com/images/I/81Rnac2Fq+L._AC_UF1000,1000_QL80_.jpg'
        WHEN 'libro_012' THEN 'https://image.cdn0.buscalibre.com/5b5ae93e4463b568748b4567.RS500x500.jpg'
        WHEN 'libro_013' THEN 'https://www.edicontinente.com.ar/image/titulos/9788466373005.jpg'
        WHEN 'libro_014' THEN 'https://images.cdn1.buscalibre.com/fit-in/360x360/57/3d/573d2d38b86840ce6c919c61ac6d217f.jpg'
        ELSE libro_img_url
    END,
    libro_puntuacion = CASE id_libro
        WHEN 'libro_001' THEN 4.5
        WHEN 'libro_002' THEN 3.0
        ELSE libro_puntuacion
    END,
    libro_dueno_id = CASE
        WHEN id_libro IN ('libro_001', 'libro_002', 'libro_003') THEN 1
        WHEN id_libro IN ('libro_004', 'libro_005', 'libro_006', 'libro_009', 'libro_010', 'libro_011', 'libro_012', 'libro_013', 'libro_014') THEN 2
        WHEN id_libro IN ('libro_007', 'libro_008') THEN 3
        ELSE libro_dueno_id
    END,
    libro_dueno_nombre = CASE
        WHEN id_libro IN ('libro_001', 'libro_002', 'libro_003') THEN 'Diego Maradona'
        WHEN id_libro IN ('libro_004', 'libro_005', 'libro_006', 'libro_009', 'libro_010', 'libro_011', 'libro_012', 'libro_013', 'libro_014') THEN 'Max Verstappen'
        WHEN id_libro IN ('libro_007', 'libro_008') THEN 'Ye West'
        ELSE libro_dueno_nombre
    END;
