-- Tabla de usuarios
CREATE TABLE usuarios (
-- CREATE TABLE IF NOT EXISTS public.usuarios(
    id               SERIAL PRIMARY KEY,
    nombre_completo  VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    ano_unido        INTEGER      NOT NULL,
    tipo_usuario     VARCHAR(50)  NOT NULL DEFAULT 'LECTOR_PUBLICADOR',
    descripcion      VARCHAR(255)          DEFAULT '',
    ciudad           VARCHAR(255)          DEFAULT '',
    telefono         VARCHAR(255)          DEFAULT '',
    bibliokarmas     INTEGER      NOT NULL DEFAULT 0,
    imagen           VARCHAR(255)          DEFAULT ''
);

-- Tabla de libros (SINGLE_TABLE)
CREATE TABLE libros (
-- CREATE TABLE IF NOT EXISTS public.libros (
    id                SERIAL PRIMARY KEY,
    tipo_libro        VARCHAR(20)  NOT NULL,
    -- FK al dueño
    id_usuario        INTEGER      NOT NULL REFERENCES usuarios(id),
    -- LibroData (embeddable)
    titulo            VARCHAR(255) NOT NULL,
    descripcion       TEXT         NOT NULL,
    genero            VARCHAR(50)  NOT NULL,
    autor             VARCHAR(255) NOT NULL,
    cant_paginas      INTEGER      NOT NULL,
    isbn13            VARCHAR(20)  NOT NULL,
    idioma            VARCHAR(50)  NOT NULL,
    editorial         VARCHAR(255) NOT NULL,
    fecha_publicacion DATE         NOT NULL,
    estado            VARCHAR(50)  NOT NULL,
    portada           VARCHAR(500) NOT NULL DEFAULT '',
    -- columna de baja lógica (usada por @SQLRestriction)
    activo            BOOLEAN      NOT NULL DEFAULT TRUE,
    -- constraint que documenta los valores válidos del discriminador tipo_libro
    CONSTRAINT chk_tipo_libro CHECK (tipo_libro IN ('COMUN', 'DEDICATORIA', 'COLECCIONABLE'))
);

-- Tabla de reservas
CREATE TABLE reservas (
-- CREATE TABLE IF NOT EXISTS public.reservas (
    id          SERIAL  PRIMARY KEY,
    id_libro    INTEGER NOT NULL REFERENCES libros(id),
    id_usuario  INTEGER NOT NULL REFERENCES usuarios(id),
    fecha_desde DATE    NOT NULL,
    fecha_hasta DATE    NOT NULL,
    ya_resenio  BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabla de reseñas
CREATE TABLE resenias (
-- CREATE TABLE IF NOT EXISTS public.resenias (
    id             SERIAL       PRIMARY KEY,
    nombre_usuario VARCHAR(255) NOT NULL,
    puntuacion     INTEGER      NOT NULL,
    comentario     VARCHAR(255) NOT NULL,
    id_libro       INTEGER      NOT NULL REFERENCES libros(id),
    fecha          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabla de auditoría de puntaje (para el trigger)
CREATE TABLE auditoria_puntaje_libro (
-- CREATE TABLE IF NOT EXISTS public.auditoria_puntaje_libro (
    id                  SERIAL           PRIMARY KEY,
    id_libro            INTEGER          NOT NULL,
    fecha_actualizacion TIMESTAMP        NOT NULL DEFAULT NOW(),
    puntaje_anterior    DOUBLE PRECISION NOT NULL,
    puntaje_nuevo       DOUBLE PRECISION NOT NULL
);