// Carga libros y clicks de prueba en MongoDB para medir con volúmenes realistas.
//
//   node seed.mjs --libros 10000   deja 10000 libros de prueba (ids "bench_...") además de los originales
//   node seed.mjs --limpiar        borra los libros y clicks de prueba
//
// Después de sembrar hay que reiniciar el backend: al arrancar con el ranking de Redis vacío,
// lo reconstruye desde los clicks de Mongo (este script lo vacía).
import { MongoClient } from 'mongodb'
import Redis from 'ioredis'

const args = process.argv.slice(2)
const valor = (nombre, porDefecto) => {
  const i = args.indexOf(nombre)
  return i >= 0 ? args[i + 1] : porDefecto
}
const limpiar = args.includes('--limpiar')
const cantidad = Number(valor('--libros', 0))
const CLICKS_POR_LIBRO = 5
const LOTE = 5000

if (!limpiar && !(cantidad > 0)) {
  console.error('Uso: node seed.mjs --libros N | --limpiar')
  process.exit(1)
}

const mongo = new MongoClient(process.env.MONGO_URI ?? 'mongodb://127.0.0.1:27019')
const redis = new Redis(process.env.REDIS_URL ?? 'redis://localhost:6379')
const db = mongo.db('booklibre_mongodb')
const libros = db.collection('libros')
const clicks = db.collection('clicks_libros')

// Generador pseudoaleatorio con semilla: el mismo N siempre arma los mismos datos
let estado = 20261012
const azar = () => {
  estado = (estado + 0x6d2b79f5) | 0
  let t = Math.imul(estado ^ (estado >>> 15), 1 | estado)
  t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t
  return ((t ^ (t >>> 14)) >>> 0) / 4294967296
}
const elegir = (lista) => lista[Math.floor(azar() * lista.length)]
const dia = 24 * 3600 * 1000

async function borrarPrueba() {
  const l = await libros.deleteMany({ _id: /^bench_/ })
  const c = await clicks.deleteMany({ libroId: /^bench_/ })
  console.log(`Borrados ${l.deletedCount} libros y ${c.deletedCount} clicks de prueba`)
}

async function sembrar() {
  const originales = await libros.find({ _id: { $not: /^bench_/ } }).toArray()
  const plantillas = Object.fromEntries(originales.map((l) => [l._class, l]))
  const tipos = Object.keys(plantillas)
  const duenos = [...new Map(originales.map((l) => [l.dueno._id, l.dueno])).values()]
  const generos = [...new Set(originales.map((l) => l.genero))]
  const idiomas = [...new Set(originales.map((l) => l.idioma))]
  const estados = [...new Set(originales.map((l) => l.estado))]
  const hoy = new Date()
  hoy.setHours(0, 0, 0, 0)

  for (let desde = 0; desde < cantidad; desde += LOTE) {
    const lote = []
    for (let i = desde; i < Math.min(desde + LOTE, cantidad); i++) {
      const base = plantillas[elegir(tipos)]
      // Los primeros 50 quedan siempre libres: son los que van a encabezar el ranking de populares
      const reservas = i >= 50 && azar() < 0.15
        ? [{
            idReserva: 100000 + i,
            fechaInicio: new Date(hoy.getTime() + Math.floor(azar() * 20 - 5) * dia),
            fechaFin: new Date(hoy.getTime() + Math.floor(azar() * 20 + 15) * dia),
          }]
        : []
      lote.push({
        ...base,
        _id: `bench_${String(i).padStart(6, '0')}`,
        titulo: `Libro de prueba ${i}`,
        autor: `Autor ${Math.floor(azar() * 2000)}`,
        genero: elegir(generos),
        idioma: elegir(idiomas),
        estado: elegir(estados),
        isbn13: String(9780000000000 + i),
        cantPaginas: 50 + Math.floor(azar() * 1500),
        dueno: elegir(duenos),
        cantidadReservas: reservas.length,
        calificacionPromedio: 0,
        activo: true,
        reservasPendientes: reservas,
      })
    }
    await libros.insertMany(lote, { ordered: false })
  }

  // Clicks con distribución muy desigual: pocos libros concentran casi todos, como un ranking real
  const totalClicks = cantidad * CLICKS_POR_LIBRO
  for (let desde = 0; desde < totalClicks; desde += LOTE) {
    const lote = []
    for (let i = desde; i < Math.min(desde + LOTE, totalClicks); i++) {
      const n = Math.floor(cantidad * azar() ** 3)
      lote.push({
        fechaHora: new Date(hoy.getTime() - Math.floor(azar() * 30 * dia)),
        usuarioId: 1 + Math.floor(azar() * 3),
        usuarioNombre: 'Usuario de prueba',
        libroId: `bench_${String(n).padStart(6, '0')}`,
        libroTitulo: `Libro de prueba ${n}`,
        _class: 'ar.edu.unsam.phm.domain.ClickLibroDocument',
      })
    }
    await clicks.insertMany(lote, { ordered: false })
  }
  console.log(`Sembrados ${cantidad} libros y ${totalClicks} clicks de prueba`)
}

async function vaciarRedis() {
  await redis.del('libros:ranking')
  const claves = await redis.keys('LibroPopular*')
  if (claves.length) await redis.del(...claves)
  console.log('Ranking y caché de populares vaciados en Redis: reiniciá el backend para reconstruirlos')
}

await borrarPrueba()
if (!limpiar) await sembrar()
await vaciarRedis()
console.log(`Total ahora: ${await libros.countDocuments()} libros, ${await clicks.countDocuments()} clicks`)
await mongo.close()
redis.disconnect()
