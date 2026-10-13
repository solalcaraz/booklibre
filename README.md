# BookLibre

BookLibre es una aplicación web para prestar y pedir prestados libros entre lectores. Cada reserva suma *bibliokarma*, un puntaje que depende del tipo de libro y de los días de préstamo. La hicimos en equipo de cinco como trabajo práctico de Programación con Herramientas Modernas, en la Tecnicatura en Programación Informática de la UNSAM, entre marzo y junio de 2026.

## Problema que resuelve

Para quien la usa, BookLibre resuelve algo concreto: encontrar un libro disponible en las fechas que necesitás, reservarlo y llevar registro de lo que prestaste y te prestaron.

El desafío técnico estuvo en otro lado. El TP se entregó en cuatro etapas y cada una sumó una tecnología de persistencia sobre el mismo dominio, sin poder empezar de cero:

- La entrega 0 arrancó con repositorios en memoria y la 1 los pasó a PostgreSQL, con autenticación JWT y componentes en la base (vistas, triggers, funciones y constraints).
- En la 2, los libros pasaron a MongoDB, sobre un cluster de dos shards con replica sets de tres nodos, mientras las reservas y los usuarios seguían en PostgreSQL.
- En la 3 se sumaron Redis como caché, una API GraphQL para un tablero de métricas y el deploy en la nube.

El resultado es un monolito que lee y escribe en tres bases distintas para una misma operación. Por ejemplo, reservar un libro guarda la reserva en PostgreSQL, actualiza la disponibilidad del libro en MongoDB y mueve el ranking de usuarios en Redis. La dificultad era mantener todo eso coherente y que cada pantalla consulte una sola base siempre que se pueda.

El detalle de cada entrega, con las consultas y sus capturas, está en [docs/entregas.md](docs/entregas.md).

## Demo

![Recorrido completo por BookLibre](docs/img/demo.gif)

El recorrido arranca con el login y pasa por los libros más consultados (servidos desde Redis), una búsqueda con filtros, el detalle de un libro y una reserva que suma bibliokarma. Después muestra la calificación de un préstamo devuelto, el perfil, la publicación de un libro y el tablero de métricas por GraphQL.

No está deployada: necesita PostgreSQL, un cluster de MongoDB y Redis, y los planes gratuitos no sostienen esa combinación sin vencerse o dormirse. Para probarla, la sección [Cómo correrlo](#cómo-correrlo) la levanta completa con Docker.

## Tecnologías

**Backend:** Kotlin, Spring Boot 3, Spring Security con JWT, Spring Data JPA, Spring Data MongoDB, Spring Data Redis, Netflix DGS (GraphQL) y Flyway. Los tests usan JUnit 5, Kotest y Mockito.

**Bases de datos:** PostgreSQL 16, MongoDB 7 (cluster de dos shards) y Redis 7, levantados con Docker Compose.

**Frontend:** React 19, TypeScript, Vite, Tailwind CSS 4, React Router y Axios. Los tests usan Vitest y Testing Library.

## Cómo funciona

El repositorio tiene dos proyectos: [`backend/`](backend) expone una API REST y una API GraphQL, y [`frontend/`](frontend) es una SPA que consume las dos.

Cada dato vive donde mejor se consulta:

| Dónde | Qué guarda | Por qué ahí |
|---|---|---|
| PostgreSQL | Usuarios, reservas, reseñas | Relaciones y transacciones; el esquema y los datos de ejemplo se versionan con Flyway |
| MongoDB | Libros y clicks | Un libro tiene tres tipos con comportamiento distinto y se consulta con filtros que se combinan |
| Redis | Ranking de populares, leaderboard de bibliokarma | Lecturas muy frecuentes que no necesitan estar al segundo |

Las decisiones que tomé y por qué:

- **Cada libro en Mongo guarda una copia de las fechas de sus reservas pendientes.** La reserva vive en PostgreSQL, pero para saber si un libro está libre entre dos fechas no conviene cruzar bases en cada búsqueda. Con esa copia, el filtro de disponibilidad del home es una sola consulta a Mongo.
- **La reserva guarda un resumen del libro (título, autor, portada, dueño).** Así, "Mis préstamos" se lista con una consulta a PostgreSQL sin pedir cada libro a Mongo.
- **El ranking de populares usa dos estructuras de Redis.** Un sorted set con los clicks de cada libro, que no vence y se actualiza en cada click, y un hash por libro con TTL de 30 segundos con los datos que muestra el home. Si del top 10 no quedan al menos 6 libros cacheados, el home vuelve a buscar en Mongo.
- **Medí cuándo vale la pena esa caché.** En entorno de desarrollo, con 14 y con 1.000 libros no hay diferencia: la sección de populares tarda lo mismo que la búsqueda normal (entre 9 y 12 ms). Con 10.000 libros, Redis responde en una mediana de 11,6 ms contra 25,1 ms de la búsqueda (54 % menos), y con 100.000, en 12,3 ms contra 170,5 ms (93 % menos). Fueron 3 corridas de 500 repeticiones por tamaño, con datos de prueba, sin índices extra en Mongo y con todo en una misma máquina, así que sirve para ver cómo escala y no como cifra de producción. El método, las tablas y las limitaciones están en [docs/medicion-redis.md](docs/medicion-redis.md).
- **Los filtros del home se arman con `MongoTemplate` y `Criteria`.** Género, páginas, fechas, ISBN, dueño y texto son opcionales y se combinan entre sí; una consulta derivada por cada combinación no escalaba.
- **La colección de libros se particiona con una clave hasheada sobre el dueño.** Las consultas del perfil de un publicador filtran por dueño, así que van a un solo shard, y la clave hasheada reparte los libros parejo entre los dos.
- **El rol del usuario viaja dentro del JWT.** El filtro de seguridad no consulta la base en cada request. El access token dura un minuto y el refresh token, siete días; el front renueva el access token cuando recibe un 401 y reintenta.
- **En la nube, Mongo quedó como una instancia única.** El plan gratuito de Atlas no permite sharding, así que el cluster de dos shards se mantiene para correr en local.

Las reglas de bibliokarma (5 por día más un plus que depende del tipo de libro) están en el enum `TipoDeLibroDTO`, porque las usan tanto el libro en Mongo como su copia cacheada en Redis.

## Cómo correrlo

Requisitos: Docker, Java 21 y Node 20 o superior.

**1. Bases de datos** (el cluster de Mongo tarda alrededor de un minuto en armarse):

```bash
git clone https://github.com/solalcaraz/booklibre.git
cd booklibre/backend
docker compose up -d
```

**2. Backend** en `http://localhost:9000`:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/booklibre
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
./gradlew bootRun
```

En Windows: `$env:VARIABLE="valor"` y `.\gradlew.bat bootRun`.

**3. Frontend** en `http://localhost:5173`:

```bash
cd ../frontend
corepack enable
yarn install
yarn dev
```

**Usuarios** (contraseña `admin`):

| Email | Rol |
|---|---|
| `diego@maradona.com` | Lector y publicador |
| `yiyi@killer.com` | Lector |
| `yewest@bully.com` | Publicador |

**Tests:** `./gradlew test` en `backend/` y `yarn test --run` en `frontend/`.

## Qué aprendí y qué mejoraría

**Qué aprendí**

- A modelar los tipos de libro con herencia: cada tipo calcula su propio plus de bibliokarma, y la reserva le pide ese cálculo sin saber de qué tipo es. Después llevé ese modelo a JPA con herencia en una sola tabla.
- A armar consultas complejas con JPA Specifications (Criteria API). Cada filtro del home es una condición opcional que se combina con las demás, y la disponibilidad entre dos fechas es una subconsulta sobre las reservas.
- A detectar y resolver un problema de N+1: al listar libros, cada uno disparaba otra consulta para traer a su dueño, y lo resolví con un `EntityGraph`.
- A versionar la base con Flyway: esquema, datos iniciales y un trigger que audita cada cambio de puntaje de un libro.
- A usar una base documental para registrar eventos, con el log de clicks en MongoDB.
- A usar Redis como caché de punta a punta. Lo levanté con Docker, lo inicialicé desde los clicks guardados en Mongo y armé el ranking de populares con un sorted set y el detalle de cada libro con hashes con TTL. Para que el bibliokarma se calcule igual con el libro de Mongo o con su copia en Redis, definí una interfaz común para los dos.
- A exponer una métrica por GraphQL que lee primero de Redis y, si no encuentra el dato, lo calcula desde PostgreSQL y lo vuelve a cachear: el ranking de usuarios con más bibliokarma.

**Qué mejoraría**

- Dos personas pueden reservar el mismo libro en fechas superpuestas si confirman al mismo tiempo, porque el chequeo de superposición y el guardado no son atómicos. Lo bloquearía en la base con un constraint de exclusión sobre el rango de fechas de cada libro, o con un lock por libro, para que la segunda reserva falle aunque lleguen juntas.
- Reservar escribe en tres bases y la transacción solo cubre PostgreSQL. Si falla la escritura en Mongo, PostgreSQL se revierte, pero el puntaje que ya se sumó en el ranking de Redis queda. Actualizaría Redis recién después de confirmar la reserva.
- Las vistas, la función y el trigger de la Entrega 1 quedaron comentados cuando los libros pasaron a Mongo. Los volvería a activar con una migración nueva adaptada al modelo actual.
- El detalle del libro no recalcula el bibliokarma si cambiás las fechas ahí mismo (la consigna lo proponía como bonus).
- La lógica de bibliokarma no tiene tests desde la migración a Mongo; los de la versión JPA ya no aplicaban.
- La interfaz está pensada solo para escritorio, como pedía la consigna; no es responsive.

## Autoría y mejoras

Este repositorio junta los dos repositorios originales del trabajo práctico que hicimos en equipo entre marzo y junio de 2026, con todo su historial. El tag [`tp-original-2026`](https://github.com/solalcaraz/booklibre/tree/tp-original-2026) marca el TP tal como lo entregamos.

**Equipo:** María Sol Alcaraz, Thomas Reemeerie, Illed Huilen Nacucchio, Denise Mayra Higa y Luis Mazo.

**Mi parte en la versión original**:

- Entrega 0: el home, con búsqueda por título o autor, filtros, orden y paginado en el back y el front, y el cálculo de bibliokarmas por tipo de libro, con sus tests.
- Entrega 1: la persistencia de libros con JPA y Specifications para los filtros, los permisos por rol en los endpoints y las guardas del front, la migración a Flyway con los datos iniciales y el trigger que audita los cambios de puntaje.
- Entrega 2: el endpoint de libros disponibles sobre MongoDB, el registro de clicks y la consulta de libros con al menos 3 reservas activas.
- Entrega 3: la caché de libros populares en Redis y el leaderboard de bibliokarma por GraphQL, en el back y el front, más la configuración de Redis para el deploy.

**Lo que hice después**:

- Uní los dos repos en uno solo sin reescribir el historial y marqué con tags el cierre de cada entrega.
- Arreglé bugs que venían de la versión entregada:
  - reservas, ediciones y bajas de libros que fallaban con el cluster local de MongoDB, porque faltaba declarar la shard key;
  - el filtro por dueño del libro, que respondía 500 por una opción de regex inválida;
  - validaciones de filtros y reseñas que respondían 500 en vez de 400, y cuyo mensaje no llegaba al front;
  - el refresh token, que servía como token de acceso;
  - pantallas que no renovaban el token vencido, y un front que sin `.env` no sabía a qué backend conectarse;
  - fechas que se mostraban un día antes por la zona horaria;
  - el KPI de conversión, que quedaba vacío 30 segundos después del último click;
  - las estrellas de la reseña, que nunca aparecían en "Mis préstamos";
  - el montaje del script de MongoDB en Linux y una tarjeta que navegaba a una ruta inexistente.
- Hice funcionar el checkbox "Recordarme" del login.
- Puse al día los tests que ya no compilaban o fallaban, y sumé tests para las validaciones de filtros.
- Limpié código muerto y comentado, unifiqué código repetido y saqué una dependencia sin uso.
- Rediseñé la interfaz con un sistema de colores y tipografía propio, con contraste WCAG AA y textos en voseo.
- Armé en [`medicion/`](medicion) un script para sembrar libros de prueba y otro para medir la sección de populares con Redis contra la búsqueda normal, y documenté el resultado.
- Grabé la demo, documenté las entregas y escribí este README.
