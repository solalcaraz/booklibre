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
    )

    val libroComun = LibroComun(libroData, dueno).apply { id = 1 }
    val libroDedicatoria = LibroDedicatoria(libroData, dueno)
    val libroColeccionable = LibroColeccionable(libroData, dueno)

    val desde = LocalDate.now().plusDays(1)
    val hasta = LocalDate.now().plusDays(11)

    describe("Validaciones del init") {
        it("no se puede crear una reserva con fechaHasta anterior a fechaDesde") {
            shouldThrow<BadRequestException> {
                Reserva(
                    libro = libroComun,
                    lector = lector,
                    fechaDesde = hasta,
                    fechaHasta = desde
                )
            }
        }

        it("no se puede reservar un libro propio") {
            shouldThrow<BadRequestException> {
                Reserva(
                    libro = libroComun,
                    lector = dueno,
                    fechaDesde = desde,
                    fechaHasta = hasta
                )
            }
        }

        it("se puede crear una reserva valida") {
            shouldNotThrow<BadRequestException> {
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

    describe("calcularBiblioKarmas") {

        it("libro común con lector con menos de 1000 bibliokarmas") {
            // lector.bibliokarmas = 0 (default)
            // 5 * 11 días + 100 páginas * 5 = 55 + 500 = 555
            val reserva = Reserva(libro = libroComun, lector = lector, fechaDesde = desde, fechaHasta = hasta)
            reserva.calcularBiblioKarmas() shouldBe 555
        }

        it("libro común con lector con más de 1000 bibliokarmas recibe cantPaginas * 2") {
            // lectorMuchosBibliokarmas.bibliokarmas = 2000
            // 5 * 11 días + 100 páginas * 2 = 55 + 200 = 255
            val reserva = Reserva(libro = libroComun, lector = lectorMuchosBibliokarmas, fechaDesde = desde, fechaHasta = hasta)
            reserva.calcularBiblioKarmas() shouldBe 255
        }

        it("libro dedicatoria sin reservas previas (cantidadReservas = 0 en memoria)") {
            // 5 * 11 días + 200 + 10 * 0 = 55 + 200 = 255
            val reserva = Reserva(libro = libroDedicatoria, lector = lector, fechaDesde = desde, fechaHasta = hasta)
            reserva.calcularBiblioKarmas() shouldBe 255
        }

        it("libro coleccionable suma quinta parte de bibliokarmas más páginas") {
            // lectorMuchosBibliokarmas.bibliokarmas = 2000
            // 5 * 11 días + ceil(2000/5) + 100 páginas = 55 + 400 + 100 = 555
            val reserva = Reserva(libro = libroColeccionable, lector = lectorMuchosBibliokarmas, fechaDesde = desde, fechaHasta = hasta)
            reserva.calcularBiblioKarmas() shouldBe 555
        }
    }
})