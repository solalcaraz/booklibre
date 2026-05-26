package ar.edu.unsam.phm.controller

/*
Tests desactivados temporalmente por migracion de libros a MongoDB.
Contenido original:

package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.helpers.Ordenamiento
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

/*
Ya no hay logica, entonces solo se cubren las validaciones del init
y el comportamiento del enum Ordenamiento
 */
class FiltrosLibroSpec : DescribeSpec({

    describe("Validaciones del init de FiltrosLibro") {

        it("no se puede crear un filtro con fechaDesde en el pasado") {
            shouldThrow<IllegalArgumentException> {
                FiltrosLibro(fechaDesde = LocalDate.now().minusDays(1))
            }
        }

        it("no se puede crear un filtro con fechaHasta anterior a fechaDesde") {
            shouldThrow<IllegalArgumentException> {
                FiltrosLibro(
                    fechaDesde = LocalDate.now().plusDays(5),
                    fechaHasta = LocalDate.now().plusDays(2)
                )
            }
        }

        it("no se puede crear un filtro con cantPaginasMin mayor a cantPaginasMax") {
            shouldThrow<IllegalArgumentException> {
                FiltrosLibro(cantPaginasMin = 500, cantPaginasMax = 100)
            }
        }

        it("se puede crear un filtro vÃ¡lido sin parÃ¡metros opcionales") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro()
            }
        }

        it("se puede crear un filtro con rango de pÃ¡ginas vÃ¡lido") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro(cantPaginasMin = 100, cantPaginasMax = 500)
            }
        }

        it("se puede crear un filtro con cantPaginasMin igual a cantPaginasMax") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro(cantPaginasMin = 300, cantPaginasMax = 300)
            }
        }

        it("se puede crear un filtro con fechaDesde igual a hoy") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro(fechaDesde = LocalDate.now())
            }
        }

        it("se puede crear un filtro con fechaDesde igual a fechaHasta") {
            val fecha = LocalDate.now().plusDays(3)
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro(fechaDesde = fecha, fechaHasta = fecha)
            }
        }

        it("se puede crear un filtro con gÃ©neros") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro(generos = listOf(Genero.ROMANCE, Genero.DRAMA))
            }
        }

        it("se puede crear un filtro con isbn") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro(isbn = "9788437604947")
            }
        }

        it("se puede crear un filtro con usuario prestador") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro(usuarioPrestador = "CortÃ¡zar")
            }
        }
    }

    describe("Valores por defecto de FiltrosLibro") {

        it("el orden por defecto es TITULO_ASC") {
            FiltrosLibro().orden shouldBe Ordenamiento.TITULO_ASC
        }

        it("la pÃ¡gina por defecto es 0") {
            FiltrosLibro().numeroPagina shouldBe 0
        }

        it("el tamaÃ±o de pÃ¡gina por defecto es 6") {
            FiltrosLibro().tamanoPaginado shouldBe 6
        }

        it("los gÃ©neros por defecto son una lista vacÃ­a") {
            FiltrosLibro().generos shouldBe emptyList()
        }

        it("cantPaginasMin y cantPaginasMax por defecto son null") {
            val filtros = FiltrosLibro()
            filtros.cantPaginasMin shouldBe null
            filtros.cantPaginasMax shouldBe null
        }
    }

    describe("Ordenamiento.toSort()") {

        it("TITULO_ASC genera un sort ascendente por data.titulo") {
            val sort = Ordenamiento.TITULO_ASC.toSort()
            sort.isSorted shouldBe true
            sort.getOrderFor("data.titulo")?.isAscending shouldBe true
        }

        it("TITULO_DESC genera un sort descendente por data.titulo") {
            val sort = Ordenamiento.TITULO_DESC.toSort()
            sort.isSorted shouldBe true
            sort.getOrderFor("data.titulo")?.isDescending shouldBe true
        }

        it("AUTOR_ASC genera un sort ascendente por data.autor") {
            val sort = Ordenamiento.AUTOR_ASC.toSort()
            sort.isSorted shouldBe true
            sort.getOrderFor("data.autor")?.isAscending shouldBe true
        }

        it("DUENO_ASC genera un sort ascendente por data.dueno.nombreCompleto") {
            val sort = Ordenamiento.DUENO_ASC.toSort()
            sort.isSorted shouldBe true
            sort.getOrderFor("data.dueno.nombreCompleto")?.isAscending shouldBe true
        }
    }
})
*/

