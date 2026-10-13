# Cuánto ayuda Redis en los libros populares

El home de BookLibre muestra una sección de libros populares que sale de Redis. Quise saber cuánto más rápida es que la alternativa sin Redis, y si la diferencia depende de cuántos libros haya cargados. Acá explico qué medí, cómo, y qué se puede (y qué no) concluir. El resumen está en el [README](../README.md).

## Qué se compara

Hice dos comparaciones.

**1. Lo que ve el usuario.** Dos pedidos al backend, los dos caminos que puede tomar el home:

- **Con Redis:** el pedido de libros populares, que lee un ranking ya armado.
- **Sin Redis:** la búsqueda normal de libros, a la que el home recurre cuando Redis no tiene suficientes libros populares guardados.

No son exactamente lo mismo: la búsqueda devuelve los primeros libros por título, no los más clickeados. Por eso hay una segunda comparación.

**2. El mismo trabajo, sin el resto del sistema.** Le pedí a cada base lo mismo: "dame los 10 libros más clickeados".

- **Redis** tiene el ranking guardado y lo lee directamente.
- **MongoDB** tiene que contar todos los clicks registrados, agruparlos por libro, ordenarlos y quedarse con los 10 primeros.

Esta es la comparación más justa del aporte de Redis, porque las dos hacen exactamente lo mismo.

## Cómo medí

El código está en [`medicion/`](../medicion). No modifica el backend ni el frontend.

1. **Cargué libros de prueba** (`seed.mjs`) hasta tener cuatro tamaños: los 14 libros originales, y 1.014, 10.014 y 100.014. Cada libro de prueba tiene 5 clicks, concentrados en pocos libros como pasa en la realidad.
2. **Medí el tiempo de cada pedido** (`medir.mjs`). Una *repetición* es hacer un pedido y anotar cuánto tardó. Un *camino* es cada una de las cuatro cosas que mido: los dos pedidos de la comparación 1 y las dos consultas de la comparación 2. Hice 500 repeticiones de cada uno.
3. **Los alterné al azar.** En cada ronda hago los cuatro en un orden distinto. Así, si la computadora se pone lenta un momento, afecta a todos por igual y ninguno sale favorecido.
4. **Dejé que el sistema entrara en calor.** Al arrancar, un programa suele ir más lento. Por eso antes de medir lo hago trabajar un rato (90 segundos la primera vez, 20 las siguientes) y descarto esos tiempos.
5. **Repetí todo tres veces** por cada tamaño, con unos minutos de diferencia, para comprobar que el resultado no fue casualidad de un momento.
6. **Descarté los pedidos en los que Redis había olvidado los datos.** Redis guarda el detalle de cada libro popular durante 30 segundos. Cuando venció, ese pedido no sirve para medir la velocidad de Redis. Fueron menos del 4 % en el peor caso.

Para repetirlo:

```bash
cd backend && docker compose up -d        # y el backend, como dice el README
cd ../medicion && npm install
node seed.mjs --libros 10000              # o --limpiar para volver a los datos originales
# reiniciar el backend para que reconstruya el ranking
node medir.mjs --repeticiones 500 --etiqueta "10000 libros" --salida resultados/10000.json
```

## Cómo leer los números

- **Mediana:** el tiempo del medio. Si ordeno los 500 tiempos de menor a mayor, es el que queda en el centro. Representa el caso típico y no se deja engañar por unos pocos pedidos muy lentos, como sí le pasa al promedio.
- **p95:** el tiempo "de los lentos". El 95 % de los pedidos tardó eso o menos, y solo 1 de cada 20 tardó más. Sirve para ver qué pasa en los peores momentos, que es lo que el usuario nota. Un *percentil* es esa idea: "el valor por debajo del cual queda tal porcentaje de los casos". La *p* es de percentil y el número es el porcentaje: p95 deja afuera solo el 5 % más lento.
- **Mejora:** cuánto menos tarda Redis, en porcentaje. Se calcula así: `1 − (tiempo con Redis ÷ tiempo sin Redis)`. Por ejemplo, con 10.014 libros: 11,61 ms con Redis y 25,14 ms sin Redis. `11,61 ÷ 25,14 = 0,46`, y `1 − 0,46 = 0,54`, o sea **54 % menos tiempo**. Si da cero o negativo, Redis no ayuda.

## Resultados

Tiempos típicos (mediana) de las tres corridas, en milisegundos (ms). Un milisegundo es una milésima de segundo.

### Lo que ve el usuario

| Libros | Con Redis | Sin Redis | Mejora | Caso lento con Redis (p95) | Caso lento sin Redis (p95) |
|---|---|---|---|---|---|
| 14 | 9,2 ms | 9,1 ms | ninguna | 10,2 ms | 10,2 ms |
| 1.014 | 11,6 ms | 11,5 ms | ninguna | 13,0 ms | 13,0 ms |
| 10.014 | 11,6 ms | 25,1 ms | **54 %** | 12,8 ms | 29,3 ms |
| 100.014 | 12,3 ms | 170,5 ms | **93 %** | 15,8 ms | 195,1 ms |

### El mismo trabajo en las dos bases

| Libros | Redis | MongoDB | Mejora |
|---|---|---|---|
| 14 | 5,1 ms | 4,1 ms | Redis es 26 % más lento |
| 1.014 | 6,9 ms | 9,8 ms | 29 % |
| 10.014 | 7,1 ms | 64,9 ms | **89 %** |
| 100.014 | 7,1 ms | 865,5 ms | **99 %** |

## Qué dicen los números

- **Con pocos libros, Redis no sirve de nada.** Con 14 y con 1.000 libros, las dos opciones tardan lo mismo. Con 14 libros, incluso Mongo gana en la comparación directa, porque Redis necesita varias consultas (una por libro) y Mongo lo resuelve en una.
- **Con muchos libros, Redis gana por mucho.** Redis tarda casi lo mismo sin importar cuántos libros haya (unos 12 ms), porque siempre lee los mismos 10. Mongo tarda cada vez más: de 11 ms a 170 ms en la búsqueda, y de 10 ms a 865 ms contando clicks.
- **El resultado es estable.** Las tres corridas dieron casi lo mismo. Con 10.014 libros, la mejora fue 53,5 %, 53,8 % y 54,3 %. La única excepción es con 1.014 libros, donde la diferencia es tan chica que a veces da a favor de uno y a veces del otro: eso es ruido.

## Qué no dice esta medición

- **No es un resultado de producción.** Todo corrió en una sola computadora de desarrollo, con un solo usuario a la vez. Sirve para ver *cómo cambia* la diferencia según el volumen, no para prometer un tiempo exacto en un servicio real.
- **Los libros son de prueba.** Son copias de los tres libros originales, con datos variados. Con libros y clicks reales los tiempos de Mongo podrían ser distintos.
- **Mongo está sin optimizar.** Un índice es como el índice de un libro: permite encontrar algo sin leer todas las páginas. No agregué índices a Mongo, tal como está el proyecto. Con los índices adecuados, Mongo sería más rápido y la ventaja de Redis, menor. La medición compara Redis contra el código actual, no contra la mejor consulta posible.
- **Un caso quedó sin medir.** Cuando Redis olvida los datos (a los 30 segundos), el pedido siguiente es más lento. Descarté esos pedidos.
- **Con 100.014 libros hubo picos aislados en Redis.** Algunos pedidos tardaron mucho más que lo normal (hasta unos 77 ms). No investigué la causa.
