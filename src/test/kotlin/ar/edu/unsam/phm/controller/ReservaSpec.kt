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
    val lector2 = Usuario(
        nombreCompleto = "Dieguito",
        email = "diego@mail.com",
        password = "admin"
    ).apply {
        id = 3
        bibliokarmas = 2000
    }

    val libroComun = Libro(
        titulo = "Rayuela",
        tipo = Comun,
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
    val libroDedicatoria = Libro(
        titulo = "Rayuela",
        tipo = Dedicatoria,
        descripcion = "Novela experimental",
        genero = Genero.LITERATURA_CLASICA,
        autor = "Julio Cortázar",
        cantPaginas = 100,
        isbn13 = "9788437604948",  // isbn distinto para evitar conflictos
        idioma = Idioma.ESPANOL,
        editorial = "Cátedra",
        fechaPublicacion = LocalDate.of(1963, 1, 1),
        estado = Estado.BUENO,
        dueno = dueno
    )
    val libroColeccionable = Libro(
        titulo = "Rayuela",
        tipo = Coleccionable,
        descripcion = "Novela experimental",
        genero = Genero.LITERATURA_CLASICA,
        autor = "Julio Cortázar",
        cantPaginas = 100,
        isbn13 = "9788437604949",  // isbn distinto para evitar conflictos
        idioma = Idioma.ESPANOL,
        editorial = "Cátedra",
        fechaPublicacion = LocalDate.of(1963, 1, 1),
        estado = Estado.BUENO,
        dueno = dueno
    )

    describe("Validaciones del init") {
        it("no se puede crear una reserva con fechaHasta anterior a fechaDesde") {
            shouldThrow<IllegalArgumentException> {
                Reserva(
                    libro = libroComun,
                    lector = lector,
                    fechaDesde = LocalDate.of(2026, 3, 20),
                    fechaHasta = LocalDate.of(2026, 3, 10)
                )
            }
        }

        it("no se puede reservar un libro propio") {
            shouldThrow<IllegalArgumentException> {
                Reserva(
                    libro = libroComun,
                    lector = dueno,
                    fechaDesde = LocalDate.of(2026, 3, 10),
                    fechaHasta = LocalDate.of(2026, 3, 20)
                )
            }
        }

        it("se puede crear una reserva valida") {
            shouldNotThrow<IllegalArgumentException> {
                Reserva(
                    libro = libroComun,
                    lector = lector,
                    fechaDesde = LocalDate.of(2026, 3, 10),
                    fechaHasta = LocalDate.of(2026, 3, 20)
                )
            }
        }
    }

    describe("estaFinalizada") {
        it("está finalizada si fechaHasta es anterior a hoy") {
            val reservaPasada = Reserva(
                libro = libroComun,
                lector = lector,
                fechaDesde = LocalDate.of(2025, 1, 1),
                fechaHasta = LocalDate.of(2025, 1, 10)
            )
            reservaPasada.estaFinalizada() shouldBe true
        }

        it("no está finalizada si fechaHasta es hoy o en el futuro") {
            val reservaFutura = Reserva(
                libro = libroComun,
                lector = lector,
                fechaDesde = LocalDate.now(),
                fechaHasta = LocalDate.now().plusDays(5)
            )
            reservaFutura.estaFinalizada() shouldBe false
        }
    }

    describe("validarReseña") {
        val reserva = Reserva(
            libro = libroComun,
            lector = lector,
            fechaDesde = LocalDate.of(2025, 1, 1),
            fechaHasta = LocalDate.of(2025, 1, 10)
        )

        it("se puede validar una reseña válida") {
            shouldNotThrow<BadRequestException> {
                reserva.validarReseña(Resenia(libroComun.id!!, lector.nombreCompleto, 4, "Muy buen libro"))
            }
        }

        it("no se puede validar una reseña con puntaje mayor a 5") {
            shouldThrow<BadRequestException> {
                reserva.validarReseña(Resenia(libroComun.id!!, lector.nombreCompleto, 6, "Excelente"))
            }
        }

        it("no se puede validar una reseña con puntaje negativo") {
            shouldThrow<BadRequestException> {
                reserva.validarReseña(Resenia(libroComun.id!!, lector.nombreCompleto, -1, "Malo"))
            }
        }

        it("no se puede validar una reseña con comentario vacío") {
            shouldThrow<BadRequestException> {
                reserva.validarReseña(Resenia(libroComun.id!!, lector.nombreCompleto, 3, ""))
            }
        }
    }

    describe("calcularBiblioKarmas") {
        val reserva = Reserva(
            libro = libroComun,
            lector = lector,
            fechaDesde = LocalDate.of(2026, 3, 10),
            fechaHasta = LocalDate.of(2026, 3, 20) // 11 dias
        )

        it("libro comun con lector con menos de 1000 bibliokarmas") {
            // 5 * 11 dias + 100 paginas * 5 = 55 + 500 = 555
            reserva.calcularBiblioKarmas(0) shouldBe 555
        }

        it("libro comun con lector con mas de 1000 bibliokarmas") {
            val reservaRico = Reserva(
                libro = libroComun,
                lector = lector2,
                fechaDesde = LocalDate.of(2026, 3, 10),
                fechaHasta = LocalDate.of(2026, 3, 20)
            )
            // 5 * 11 dias + 100 paginas * 2 = 55 + 200 = 255
            reservaRico.calcularBiblioKarmas(0) shouldBe 255
        }

        it("libro dedicatoria suma 200 mas 10 por cada reserva previa") {
            val reservaDedicatoria = Reserva(
                libro = libroDedicatoria,
                lector = lector,
                fechaDesde = LocalDate.of(2026, 3, 10),
                fechaHasta = LocalDate.of(2026, 3, 20)
            )
            // 5 * 11 dias + 200 + 10 * 3 reservas = 55 + 230 = 285
            reservaDedicatoria.calcularBiblioKarmas(3) shouldBe 285
        }

        it("libro coleccionable suma quinta parte de bibliokarmas mas paginas") {
            val reservaColeccionable = Reserva(
                libro = libroColeccionable,
                lector = lector2, // 2000 bibliokarmas
                fechaDesde = LocalDate.of(2026, 3, 10),
                fechaHasta = LocalDate.of(2026, 3, 20)
            )
            // 5 * 11 dias + ceil(2000/5) + 100 paginas = 55 + 400 + 100 = 555
            reservaColeccionable.calcularBiblioKarmas(0) shouldBe 555
        }
    }
})