package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.exceptions.BadRequestException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ReservaSpec : DescribeSpec({

    val dueno = Usuario(
        nombreCompleto = "Julio Cortázar",
        email = "julio@cortazar.com",
        password = "admin"
    ).apply { id = 1 }

    val lector = Usuario(
        nombreCompleto = "Lionel Messi",
        email = "lionel@messi.com",
        password = "admin"
    ).apply { id = 2 }

    val lectorMuchosBibliokarmas = Usuario(
        nombreCompleto = "Dieguito",
        email = "diego@mail.com",
        password = "admin"
    ).apply {
        id = 3
        bibliokarmas = 2000
    }

    val libroData = LibroData(
        titulo = "Rayuela",
        descripcion = "Novela experimental",
        genero = Genero.LITERATURA_CLASICA,
        autor = "Julio Cortázar",
        cantPaginas = 100,
        isbn13 = "9788437604947",
        idioma = Idioma.ESPANOL,
        editorial = "Cátedra",
        fechaPublicacion = LocalDate.of(1963, 1, 1),
        estado = Estado.BUENO,
        dueno = dueno
    )

    val libroComun = LibroComun(libroData)
    val libroDedicatoria = LibroDedicatoria(libroData)
    val libroColeccionable = LibroColeccionable(libroData)

    val desde = LocalDate.now().plusDays(1)
    val hasta = LocalDate.now().plusDays(11)

    describe("Validaciones del init") {
        it("no se puede crear una reserva con fechaHasta anterior a fechaDesde") {
            shouldThrow<IllegalArgumentException> {
                Reserva(
                    libro = libroComun,
                    lector = lector,
                    fechaDesde = hasta,
                    fechaHasta = desde
                )
            }
        }

        it("no se puede reservar un libro propio") {
            shouldThrow<IllegalArgumentException> {
                Reserva(
                    libro = libroComun,
                    lector = dueno,
                    fechaDesde = desde,
                    fechaHasta = hasta
                )
            }
        }

        it("se puede crear una reserva valida") {
            shouldNotThrow<IllegalArgumentException> {
                Reserva(
                    libro = libroComun,
                    lector = lector,
                    fechaDesde = desde,
                    fechaHasta = hasta
                )
            }
        }
    }

    describe("estaFinalizada") {
        it("está finalizada si fechaHasta es anterior a hoy") {
            val reservaPasada = Reserva(
                libro = libroComun,
                lector = lector,
                fechaDesde = LocalDate.now().minusDays(10),
                fechaHasta = LocalDate.now().minusDays(1)
            )
            reservaPasada.estaFinalizada() shouldBe true
        }

        it("no está finalizada si fechaHasta es hoy o en el futuro") {
            val reservaFutura = Reserva(
                libro = libroComun,
                lector = lector,
                fechaDesde = desde,
                fechaHasta = hasta
            )
            reservaFutura.estaFinalizada() shouldBe false
        }
    }

    describe("validarReseña") {
        val reservaFinalizada = Reserva(
            libro = libroComun,
            lector = lector,
            fechaDesde = LocalDate.now().minusDays(10),
            fechaHasta = LocalDate.now().minusDays(1)
        )

        it("se puede validar una reseña válida") {
            shouldNotThrow<BadRequestException> {
                reservaFinalizada.validarReseña(Resenia(libroComun.id!!, lector.nombreCompleto, 4, "Muy buen libro"))
            }
        }

        it("no se puede validar una reseña con puntaje mayor a 5") {
            shouldThrow<BadRequestException> {
                reservaFinalizada.validarReseña(Resenia(libroComun.id!!, lector.nombreCompleto, 6, "Excelente"))
            }
        }

        it("no se puede validar una reseña con puntaje negativo") {
            shouldThrow<BadRequestException> {
                reservaFinalizada.validarReseña(Resenia(libroComun.id!!, lector.nombreCompleto, -1, "Malo"))
            }
        }

        it("no se puede validar una reseña con comentario vacío") {
            shouldThrow<BadRequestException> {
                reservaFinalizada.validarReseña(Resenia(libroComun.id!!, lector.nombreCompleto, 3, ""))
            }
        }
    }

    describe("calcularBiblioKarmas") {
        it("libro comun con lector con menos de 1000 bibliokarmas") {
            val reserva = Reserva(libro = libroComun, lector = lector, fechaDesde = desde, fechaHasta = hasta)
            // 5 * 11 dias + 100 paginas * 5 = 55 + 500 = 555
            reserva.calcularBiblioKarmas(0) shouldBe 555
        }

        it("libro comun con lector con mas de 1000 bibliokarmas") {
            val reserva = Reserva(libro = libroComun, lector = lectorMuchosBibliokarmas, fechaDesde = desde, fechaHasta = hasta)
            // 5 * 11 dias + 100 paginas * 2 = 55 + 200 = 255
            reserva.calcularBiblioKarmas(0) shouldBe 255
        }

        it("libro dedicatoria suma 200 mas 10 por cada reserva previa") {
            val reserva = Reserva(libro = libroDedicatoria, lector = lector, fechaDesde = desde, fechaHasta = hasta)
            // 5 * 11 dias + 200 + 10 * 3 reservas = 55 + 230 = 285
            reserva.calcularBiblioKarmas(3) shouldBe 285
        }

        it("libro coleccionable suma quinta parte de bibliokarmas mas paginas") {
            val reserva = Reserva(libro = libroColeccionable, lector = lectorMuchosBibliokarmas, fechaDesde = desde, fechaHasta = hasta)
            // 5 * 11 dias + ceil(2000/5) + 100 paginas = 55 + 400 + 100 = 555
            reserva.calcularBiblioKarmas(0) shouldBe 555
        }
    }
})