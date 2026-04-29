/*
package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.helpers.FiltrosLibro
import ar.edu.unsam.phm.helpers.cumpleFiltros
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class FiltrosLibroSpec : DescribeSpec({

    val dueno = Usuario(
        nombreCompleto = "Julio Cortázar",
        email = "julio@cortazar.com",
        password = "admin"
    ).apply { id = 1 }

    val libroBase = LibroComun(
        data = LibroData(
            titulo = "Rayuela",
            descripcion = "Novela experimental",
            genero = Genero.LITERATURA_CLASICA,
            autor = "Julio Cortázar",
            cantPaginas = 300,
            isbn13 = "9788437604947",
            idioma = Idioma.ESPANOL,
            editorial = "Cátedra",
            fechaPublicacion = LocalDate.of(1963, 1, 1),
            estado = Estado.BUENO,
            dueno = dueno
        )
    )

    describe("Validaciones del init") {
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

        it("se puede crear un filtro valido sin parametros") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro()
            }
        }

        it("se puede crear un filtro con rango de paginas valido") {
            shouldNotThrow<IllegalArgumentException> {
                FiltrosLibro(cantPaginasMin = 100, cantPaginasMax = 500)
            }
        }
    }

    describe("cumpleGenero") {
        it("sin generos filtrando acepta cualquier libro") {
            libroBase.cumpleFiltros(FiltrosLibro()) shouldBe true
        }

        it("acepta el libro si su genero está en la lista") {
            libroBase.cumpleFiltros(FiltrosLibro(generos = listOf(Genero.LITERATURA_CLASICA))) shouldBe true
        }

        it("rechaza el libro si su genero no está en la lista") {
            libroBase.cumpleFiltros(FiltrosLibro(generos = listOf(Genero.ROMANCE))) shouldBe false
        }
    }

    describe("cumpleCantPaginas") {
        it("acepta el libro si está dentro del rango") {
            libroBase.cumpleFiltros(FiltrosLibro(cantPaginasMin = 100, cantPaginasMax = 500)) shouldBe true
        }

        it("acepta el libro si tiene exactamente el minimo de paginas") {
            libroBase.cumpleFiltros(FiltrosLibro(cantPaginasMin = 300)) shouldBe true
        }

        it("acepta el libro si tiene exactamente el maximo de paginas") {
            libroBase.cumpleFiltros(FiltrosLibro(cantPaginasMax = 300)) shouldBe true
        }

        it("rechaza el libro si tiene menos paginas que el minimo") {
            libroBase.cumpleFiltros(FiltrosLibro(cantPaginasMin = 400)) shouldBe false
        }

        it("rechaza el libro si tiene mas paginas que el maximo") {
            libroBase.cumpleFiltros(FiltrosLibro(cantPaginasMax = 200)) shouldBe false
        }
    }

    describe("cumpleIsbn") {
        it("sin isbn filtrando acepta cualquier libro") {
            libroBase.cumpleFiltros(FiltrosLibro()) shouldBe true
        }

        it("acepta el libro si el isbn coincide exactamente") {
            libroBase.cumpleFiltros(FiltrosLibro(isbn = "9788437604947")) shouldBe true
        }

        it("rechaza el libro si el isbn no coincide") {
            libroBase.cumpleFiltros(FiltrosLibro(isbn = "0000000000000")) shouldBe false
        }
    }

    describe("cumpleUsuario") {
        it("sin usuarioPrestador filtrando acepta cualquier libro") {
            libroBase.cumpleFiltros(FiltrosLibro()) shouldBe true
        }

        it("acepta el libro si el nombre del dueno contiene el texto buscado") {
            libroBase.cumpleFiltros(FiltrosLibro(usuarioPrestador = "Cortázar")) shouldBe true
        }

        it("la busqueda por dueno es case insensitive") {
            libroBase.cumpleFiltros(FiltrosLibro(usuarioPrestador = "cortázar")) shouldBe true
        }

        it("rechaza el libro si el nombre del dueno no contiene el texto buscado") {
            libroBase.cumpleFiltros(FiltrosLibro(usuarioPrestador = "Borges")) shouldBe false
        }
    }
})*/
