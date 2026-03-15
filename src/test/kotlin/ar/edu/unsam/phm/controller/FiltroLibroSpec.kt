package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.helpers.FiltrosLibro
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

    val libroBase = Libro(
        titulo = "Rayuela",
        tipo = Comun,
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
            FiltrosLibro().aplicarA(libroBase) shouldBe true
        }

        it("acepta el libro si su genero está en la lista") {
            FiltrosLibro(generos = listOf(Genero.LITERATURA_CLASICA)).aplicarA(libroBase) shouldBe true
        }

        it("rechaza el libro si su genero no está en la lista") {
            FiltrosLibro(generos = listOf(Genero.ROMANCE)).aplicarA(libroBase) shouldBe false
        }
    }

    describe("cumpleCantPaginas") {
        it("acepta el libro si está dentro del rango") {
            FiltrosLibro(cantPaginasMin = 100, cantPaginasMax = 500).aplicarA(libroBase) shouldBe true
        }

        it("acepta el libro si tiene exactamente el minimo de paginas") {
            FiltrosLibro(cantPaginasMin = 300).aplicarA(libroBase) shouldBe true
        }

        it("acepta el libro si tiene exactamente el maximo de paginas") {
            FiltrosLibro(cantPaginasMax = 300).aplicarA(libroBase) shouldBe true
        }

        it("rechaza el libro si tiene menos paginas que el minimo") {
            FiltrosLibro(cantPaginasMin = 400).aplicarA(libroBase) shouldBe false
        }

        it("rechaza el libro si tiene mas paginas que el maximo") {
            FiltrosLibro(cantPaginasMax = 200).aplicarA(libroBase) shouldBe false
        }
    }

    describe("cumpleIsbn") {
        it("sin isbn filtrando acepta cualquier libro") {
            FiltrosLibro().aplicarA(libroBase) shouldBe true
        }

        it("acepta el libro si el isbn coincide exactamente") {
            FiltrosLibro(isbn = "9788437604947").aplicarA(libroBase) shouldBe true
        }

        it("rechaza el libro si el isbn no coincide") {
            FiltrosLibro(isbn = "0000000000000").aplicarA(libroBase) shouldBe false
        }
    }

    describe("cumpleUsuario") {
        it("sin usuarioPrestador filtrando acepta cualquier libro") {
            FiltrosLibro().aplicarA(libroBase) shouldBe true
        }

        it("acepta el libro si el nombre del dueno contiene el texto buscado") {
            FiltrosLibro(usuarioPrestador = "Cortázar").aplicarA(libroBase) shouldBe true
        }

        it("la busqueda por dueno es case insensitive") {
            FiltrosLibro(usuarioPrestador = "cortázar").aplicarA(libroBase) shouldBe true
        }

        it("rechaza el libro si el nombre del dueno no contiene el texto buscado") {
            FiltrosLibro(usuarioPrestador = "Borges").aplicarA(libroBase) shouldBe false
        }
    }
})