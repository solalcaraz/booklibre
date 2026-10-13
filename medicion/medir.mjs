// Mide la sección de libros populares de BookLibre en dos niveles. Necesita el backend, Mongo y Redis levantados.
//
//   node medir.mjs [--repeticiones 500] [--calentamiento 60] [--etiqueta "10000 libros"] [--salida resultados/x.json]
//
// Nivel HTTP (lo que ve el usuario en el home):
//   http_redis     GET /reserva/librosPopulares      (ranking + hashes de Redis)
//   http_busqueda  GET /reserva/librosDisponibles    (búsqueda normal en Mongo, a la que cae el front)
// Nivel datos (el mismo trabajo, sin HTTP ni PostgreSQL):
//   datos_redis    ZREVRANGE del ranking + un HGETALL por libro, como hace LibroPopularRedisService
//   datos_mongo    el top 10 por clicks agregando clicks_libros + la búsqueda de esos libros,
//                  que es lo que Redis evita (misma agregación que ClickLibroRepository)
//
// Las cuatro operaciones se corren juntas en cada vuelta, en orden aleatorio, para que el ruido de la
// máquina (y el orden) afecte a todas por igual.
import { MongoClient } from 'mongodb'
import Redis from 'ioredis'
import { mkdirSync, writeFileSync } from 'node:fs'
import { dirname } from 'node:path'

const args = process.argv.slice(2)
const valor = (nombre, porDefecto) => {
  const i = args.indexOf(nombre)
  return i >= 0 ? args[i + 1] : porDefecto
}
const REPETICIONES = Number(valor('--repeticiones', 500))
const CALENTAMIENTO_S = Number(valor('--calentamiento', 60))
const ETIQUETA = valor('--etiqueta', '')
const SALIDA = valor('--salida', '')
const BASE = process.env.API_URL ?? 'http://localhost:9000'
const RESAMPLEOS = 5000

const mongo = new MongoClient(process.env.MONGO_URI ?? 'mongodb://127.0.0.1:27019')
const redis = new Redis(process.env.REDIS_URL ?? 'redis://localhost:6379')
const db = mongo.db('booklibre_mongodb')

// --- token (dura 1 minuto: se renueva antes de que venza) ---
let token = ''
let tokenDesde = 0
async function conToken() {
  if (Date.now() - tokenDesde > 40_000) {
    const r = await fetch(`${BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'diego@maradona.com', password: 'admin' }),
    })
    token = (await r.json()).accessToken
    tokenDesde = Date.now()
  }
  return { Authorization: `Bearer ${token}` }
}

const hoy = new Date()
const hoyTexto = `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, '0')}-${String(hoy.getDate()).padStart(2, '0')}`
hoy.setHours(0, 0, 0, 0)
// La URL que manda el front en el home con los filtros iniciales (Home.tsx + api.ts)
const RUTA_BUSQUEDA = `/reserva/librosDisponibles?cantPaginasMin=0&cantPaginasMax=2000&fechaDesde=${hoyTexto}&fechaHasta=${hoyTexto}&orden=TITULO_ASC&numeroPagina=0&tamanoPaginado=6`

// Cada operación devuelve false si la muestra no es válida (caché vencida)
const operaciones = {
  async http_redis() {
    const r = await fetch(`${BASE}/reserva/librosPopulares`, { headers: await conToken() })
    if (r.status === 404) return false // Redis sin 6 libros cacheados; el propio pedido los vuelve a cachear
    if (!r.ok) throw new Error(`librosPopulares respondió ${r.status}`)
    await r.json()
    return true
  },
  async http_busqueda() {
    const r = await fetch(`${BASE}${RUTA_BUSQUEDA}`, { headers: await conToken() })
    if (!r.ok) throw new Error(`librosDisponibles respondió ${r.status}`)
    await r.json()
    return true
  },
  async datos_redis() {
    const ids = await redis.zrevrange('libros:ranking', 0, 9)
    let cacheados = 0
    for (const id of ids) {
      const hash = await redis.hgetall(`LibroPopular:${id}`)
      if (hash.libroId) cacheados++
    }
    return cacheados >= 6
  },
  async datos_mongo() {
    const top = await db.collection('clicks_libros').aggregate([
      { $group: { _id: { libroId: '$libroId', libroTitulo: '$libroTitulo' }, clicks: { $sum: 1 } } },
      { $sort: { clicks: -1, '_id.libroTitulo': 1 } },
      { $limit: 10 },
      { $project: { _id: 0, libroId: '$_id.libroId', libroTitulo: '$_id.libroTitulo', clicks: 1 } },
    ]).toArray()
    await db.collection('libros').find({
      _id: { $in: top.map((t) => t.libroId) },
      activo: true,
      reservasPendientes: { $not: { $elemMatch: { fechaInicio: { $lte: hoy }, fechaFin: { $gte: hoy } } } },
    }).toArray()
    return true
  },
}
const nombres = Object.keys(operaciones)

function mezclar(lista) {
  const copia = [...lista]
  for (let i = copia.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    const aux = copia[i]
    copia[i] = copia[j]
    copia[j] = aux
  }
  return copia
}

// Una vuelta: las cuatro operaciones en orden aleatorio. Devuelve los tiempos (null si la muestra no es válida).
async function vuelta() {
  const tiempos = {}
  for (const nombre of mezclar(nombres)) {
    const t0 = performance.now()
    const valida = await operaciones[nombre]()
    const ms = performance.now() - t0
    tiempos[nombre] = valida ? ms : null
  }
  return tiempos
}

// --- estadística ---
const ordenar = (xs) => [...xs].sort((a, b) => a - b)
const mediana = (ord) => {
  const m = ord.length / 2
  return ord.length % 2 ? ord[Math.floor(m)] : (ord[m - 1] + ord[m]) / 2
}
const percentil = (ord, p) => ord[Math.min(ord.length - 1, Math.ceil(p * ord.length) - 1)] // rango más cercano
const redondear = (x) => Math.round(x * 100) / 100

function resumen(xs) {
  const ord = ordenar(xs)
  return {
    n: ord.length,
    promedio: redondear(xs.reduce((a, b) => a + b, 0) / xs.length),
    mediana: redondear(mediana(ord)),
    p95: redondear(percentil(ord, 0.95)),
    p99: redondear(percentil(ord, 0.99)),
    min: redondear(ord[0]),
    max: redondear(ord[ord.length - 1]),
  }
}

// Mejora de la mediana (1 - mediana_a / mediana_b) con un intervalo de confianza del 95 % por bootstrap:
// se remuestrean las vueltas completas (pares), así se conserva la relación entre las dos operaciones.
function mejora(a, b) {
  const puntual = 1 - mediana(ordenar(a)) / mediana(ordenar(b))
  const n = a.length
  const valores = []
  for (let r = 0; r < RESAMPLEOS; r++) {
    const ra = new Array(n)
    const rb = new Array(n)
    for (let i = 0; i < n; i++) {
      const k = Math.floor(Math.random() * n)
      ra[i] = a[k]
      rb[i] = b[k]
    }
    valores.push(1 - mediana(ordenar(ra)) / mediana(ordenar(rb)))
  }
  const ord = ordenar(valores)
  return {
    porcentaje: redondear(puntual * 100),
    ic95_bajo: redondear(ord[Math.floor(0.025 * RESAMPLEOS)] * 100),
    ic95_alto: redondear(ord[Math.floor(0.975 * RESAMPLEOS)] * 100),
  }
}

// --- corrida ---
console.log(`Calentando ${CALENTAMIENTO_S} s...`)
for (const fin = Date.now() + CALENTAMIENTO_S * 1000; Date.now() < fin;) await vuelta()

const pares = { http: [], datos: [] }
const vencidas = { http_redis: 0, datos_redis: 0 }
let vueltas = 0
const inicio = new Date().toISOString()
while ((pares.http.length < REPETICIONES || pares.datos.length < REPETICIONES) && vueltas < REPETICIONES * 3) {
  const t = await vuelta()
  vueltas++
  if (t.http_redis === null) vencidas.http_redis++
  if (t.datos_redis === null) vencidas.datos_redis++
  if (t.http_redis !== null && pares.http.length < REPETICIONES) pares.http.push([t.http_redis, t.http_busqueda])
  if (t.datos_redis !== null && pares.datos.length < REPETICIONES) pares.datos.push([t.datos_redis, t.datos_mongo])
}

const columna = (lista, i) => lista.map((p) => p[i])
const resultado = {
  etiqueta: ETIQUETA,
  inicio,
  repeticiones: REPETICIONES,
  vueltas,
  muestrasVencidas: vencidas,
  libros: await db.collection('libros').countDocuments(),
  clicks: await db.collection('clicks_libros').countDocuments(),
  http: {
    redis: resumen(columna(pares.http, 0)),
    busqueda: resumen(columna(pares.http, 1)),
    mejoraMediana: mejora(columna(pares.http, 0), columna(pares.http, 1)),
  },
  datos: {
    redis: resumen(columna(pares.datos, 0)),
    mongo: resumen(columna(pares.datos, 1)),
    mejoraMediana: mejora(columna(pares.datos, 0), columna(pares.datos, 1)),
  },
  muestras: {
    http_redis: columna(pares.http, 0).map(redondear),
    http_busqueda: columna(pares.http, 1).map(redondear),
    datos_redis: columna(pares.datos, 0).map(redondear),
    datos_mongo: columna(pares.datos, 1).map(redondear),
  },
}

console.log(`\n${ETIQUETA} (${resultado.libros} libros, ${resultado.clicks} clicks, ${vueltas} vueltas)`)
console.table({
  'HTTP con Redis': resultado.http.redis,
  'HTTP búsqueda normal': resultado.http.busqueda,
  'Datos Redis': resultado.datos.redis,
  'Datos Mongo (agregación)': resultado.datos.mongo,
})
const f = (m) => `${m.porcentaje} % (IC 95 %: ${m.ic95_bajo} a ${m.ic95_alto} %)`
console.log(`Mejora de la mediana, HTTP:  ${f(resultado.http.mejoraMediana)}`)
console.log(`Mejora de la mediana, datos: ${f(resultado.datos.mejoraMediana)}`)
console.log(`Muestras descartadas por caché vencida: ${JSON.stringify(vencidas)}`)

if (SALIDA) {
  mkdirSync(dirname(SALIDA), { recursive: true })
  writeFileSync(SALIDA, JSON.stringify(resultado, null, 2))
}
await mongo.close()
redis.disconnect()
