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
- **Los filtros del home se arman con `MongoTemplate` y `Criteria`.** Género, páginas, fechas, ISBN, dueño y texto son opcionales y se combinan entre sí; una consulta derivada por cada combinación no escalaba.
- **La colección de libros se particiona con una clave hasheada sobre el dueño.** Las consultas del perfil de un publicador filtran por dueño, así que van a un solo shard, y la clave hasheada reparte los libros parejo entre los dos.
- **El rol del usuario viaja dentro del JWT.** El filtro de seguridad no consulta la base en cada request. El access token dura un minuto y el refresh token, siete días; el front renueva el access token cuando recibe un 401 y reintenta.
- **En la nube, Mongo quedó como una instancia única.** El plan gratuito de Atlas no permite sharding, así que el cluster de dos shards se mantiene para correr en local.

Las reglas de bibliokarma (5 por día más un plus que depende del tipo de libro) están en el enum `TipoDeLibroDTO`, porque las usan tanto el libro en Mongo como su copia cacheada en Redis.

## Cómo correrlo

Necesitás Docker, Java 21 y Node 20 o superior.

**1. Bases de datos.** Levanta PostgreSQL, Redis y el cluster de MongoDB (diez contenedores entre config servers, shards y router), además de pgAdmin y RedisInsight:

```bash
git clone https://github.com/solalcaraz/booklibre.git
cd booklibre/backend
docker compose up -d
```

El contenedor `mongo-cluster-init` arma el cluster y crea las vistas de las consultas; tarda alrededor de un minuto. Cuando termina, `docker compose ps -a` lo muestra como `Exited (0)`.

**2. Backend.** Corre en `http://localhost:9000`. Flyway crea las tablas y carga los datos de ejemplo, y al arrancar se cargan los libros en Mongo:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/booklibre
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
./gradlew bootRun
```

En Windows con PowerShell, las variables se definen con `$env:SPRING_DATASOURCE_URL="..."` y la app se levanta con `.\gradlew.bat bootRun`.

**3. Frontend.** Corre en `http://localhost:5173` y por defecto apunta a `http://localhost:9000` (se puede cambiar con `VITE_SERVER_CONNECTION`, ver `frontend/.env.example`):

```bash
cd ../frontend
corepack enable
yarn install
yarn dev
```

**Usuarios de prueba** (contraseña `admin` en todos):

| Email | Rol |
|---|---|
| `diego@maradona.com` | Lector y publicador: ve todas las pantallas |
| `yiyi@killer.com` | Lector: busca y reserva |
| `yewest@bully.com` | Publicador: publica libros y ve el tablero |

**Tests:** `./gradlew test` en `backend/` y `yarn test --run` en `frontend/`.

## Qué aprendí y qué mejoraría

**Qué aprendí**

- Que cambiar la base de un dominio que ya funciona no es cambiar un repositorio: al pasar los libros a Mongo hubo que repensar qué dato vive dónde y qué copia se guarda en la otra base para no cruzarlas en cada consulta.
- Que el sharding se nota en el código. Con la colección particionada, Mongo rechaza las actualizaciones que no incluyen la shard key; al preparar el deploy en Atlas sacamos la anotación que la declaraba y dejaron de funcionar las reservas en local.
- Que una caché con vencimiento tiene bordes que no se ven en la primera prueba. El KPI de conversión dependía de un hash de 30 segundos y quedaba vacío cuando nadie abría el home en ese lapso.

**Qué mejoraría**

- Las vistas, la función y el trigger de la Entrega 1 quedaron comentados cuando los libros pasaron a Mongo. Los volvería a activar con una migración nueva adaptada al modelo actual.
- Reservar escribe en tres bases y la transacción solo cubre PostgreSQL. Si falla la escritura en Mongo, PostgreSQL se revierte, pero el puntaje que ya se sumó en el ranking de Redis queda. Lo resolvería actualizando Redis recién después de confirmar la reserva, o con una tarea que reconcilie.
- El detalle del libro no recalcula el bibliokarma si cambiás las fechas ahí mismo (la consigna lo proponía como bonus).
- La lógica de bibliokarma no tiene tests desde la migración a Mongo; los de la versión JPA ya no aplicaban.
- La interfaz está pensada solo para escritorio, como pedía la consigna; no es responsive.

## Autoría y mejoras

Este repositorio junta los dos repositorios originales del trabajo práctico que hicimos en equipo entre marzo y junio de 2026, con todo su historial. El tag [`tp-original-2026`](https://github.com/solalcaraz/booklibre/tree/tp-original-2026) marca el TP tal como lo entregamos.

**Equipo:** Thomas Reemeerie, Illed Huilen Nacucchio, Denise Mayra Higa, Luis Mazo y yo.

**Mi parte en la versión original**:

- Entrega 0: el home, con búsqueda por título o autor, filtros, orden y paginado en el back y el front, y el cálculo de bibliokarmas por tipo de libro, con sus tests.
- Entrega 1: la persistencia de libros con JPA y Specifications para los filtros, los permisos por rol en los endpoints y las guardas del front, la migración a Flyway con los datos iniciales y el trigger que audita los cambios de puntaje.
- Entrega 2: el endpoint de libros disponibles sobre MongoDB, el registro de clicks y la consulta de libros con al menos 3 reservas activas.
- Entrega 3: la caché de libros populares en Redis y el leaderboard de bibliokarma por GraphQL, en el back y el front, más la configuración de Redis para el deploy.

**Lo que hice después**:

- Uní los dos repos en uno solo sin reescribir el historial y marqué con tags el cierre de cada entrega.
- Corregí que crear reservas, editar y despublicar libros fallaran con el cluster de MongoDB local (faltaba declarar la shard key).
- Corregí el filtro por dueño del libro, que respondía 500 por una opción de regex inválida.
- Corregí las validaciones de filtros y reseñas, que respondían 500 en vez de 400, y que el mensaje real llegue al front.
- Corregí que el refresh token sirviera como token de acceso.
- Corregí las pantallas que no renovaban el token vencido.
- Corregí que el front sin `.env` no supiera a qué backend conectarse.
- Corregí las fechas, que se mostraban un día antes por la zona horaria.
- Corregí el KPI de conversión, que quedaba vacío 30 segundos después del último click.
- Corregí las estrellas de la reseña, que nunca aparecían en "Mis préstamos".
- Corregí el montaje del script de MongoDB en Linux y una tarjeta que navegaba a una ruta inexistente.
- Hice funcionar el checkbox "Recordarme" del login.
- Arreglé los tests que ya no compilaban o fallaban en la versión entregada y sumé tests para las validaciones de filtros.
- Limpié código muerto y comentado, unifiqué código repetido y saqué una dependencia sin uso.
- Rediseñé la interfaz con un sistema de colores y tipografía propio, con contraste WCAG AA y textos en voseo.
- Grabé la demo, documenté las entregas y escribí este README.

Para verificar que el comportamiento no cambiara, corrí el mismo recorrido de 82 requests contra la versión entregada y contra la final, con las bases recién creadas. 61 respuestas son idénticas; las otras 21 son los bugs corregidos (y los datos que esas escrituras ahora sí guardan) y el puntaje de la reseña que agregué al listado de préstamos. Los tests pasan en los dos proyectos.
