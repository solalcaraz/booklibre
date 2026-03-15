package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class TipoDeLibroSpec : DescribeSpec({

    val dueno = Usuario(
        nombreCompleto = "Julio Cortázar",
        email = "julio@cortazar.com",
        password = "admin"
    ).apply { id = 1 }

    val lectorPobre = Usuario(
        nombreCompleto = "Lector Pobre",
        email = "pobre@mail.com",
        password = "admin"
    ).apply {
        id = 2
        bibliokarmas = 500
    }
    val lectorCulto = Usuario(
        nombreCompleto = "Lector Rico",
        email = "rico@mail.com",
        password = "admin"
    ).apply {
        id = 3
        bibliokarmas = 2000
    }

    val libro = Libro(
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

    describe("Comun") {
        it("lector con menos de 1000 bibliokarmas recibe cantPaginas * 5") {
            // 100 * 5 = 500
            Comun.plusBiblioKarma(libro, lectorPobre, 0) shouldBe 500
        }

        it("lector con exactamente 1000 bibliokarmas recibe cantPaginas * 2") {
            val lectorNormal = Usuario(
                nombreCompleto = "Lector Justo",
                email = "justo@mail.com",
                password = "admin"
            ).apply {
                id = 4
                bibliokarmas = 1000
            }
            // 100 * 2 = 200
            Comun.plusBiblioKarma(libro, lectorNormal, 0) shouldBe 200
        }

        it("lector con mas de 1000 bibliokarmas recibe cantPaginas * 2") {
            // 100 * 2 = 200
            Comun.plusBiblioKarma(libro, lectorCulto, 0) shouldBe 200
        }
    }

    describe("Dedicatoria") {
        it("sin reservas previas devuelve 200") {
            Dedicatoria.plusBiblioKarma(libro, lectorPobre, 0) shouldBe 200
        }

        it("con reservas previas suma 10 por cada una") {
            // 200 + 10 * 5 = 250
            Dedicatoria.plusBiblioKarma(libro, lectorPobre, 5) shouldBe 250
        }

        it("los bibliokarmas del usuario no afectan el calculo") {
            Dedicatoria.plusBiblioKarma(libro, lectorPobre, 3) shouldBe
                    Dedicatoria.plusBiblioKarma(libro, lectorCulto, 3)
        }
    }

    describe("Coleccionable") {
        it("suma la quinta parte de los bibliokarmas del usuario mas las paginas") {
            // ceil(2000 / 5) + 100 = 400 + 100 = 500
            Coleccionable.plusBiblioKarma(libro, lectorCulto, 0) shouldBe 500
        }

        it("la quinta parte se redondea hacia arriba") {
            val lectorImpar = Usuario(
                nombreCompleto = "Lector Impar",
                email = "impar@mail.com",
                password = "admin"
            ).apply {
                id = 5
                bibliokarmas = 503
            }
            // ceil(503 / 5.0) + 100 = ceil(100.6) + 100 = 101 + 100 = 201
            Coleccionable.plusBiblioKarma(libro, lectorImpar, 0) shouldBe 201
        }

        it("la cantidad de reservas no afecta el calculo") {
            Coleccionable.plusBiblioKarma(libro, lectorCulto, 0) shouldBe
                    Coleccionable.plusBiblioKarma(libro, lectorCulto, 99)
        }
    }
})