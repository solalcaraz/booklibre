package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.*
import io.kotest.core.spec.style.DescribeSpec
import java.time.LocalDate
import io.kotest.matchers.shouldBe

class LibroSpec : DescribeSpec({

    val dueno = Usuario(
        nombreCompleto = "Julio Cortázar",
        email = "julio@cortazar.com",
        password = "admin"
    ).apply { id = 1 }

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

    describe("LibroComun") {
        it("lector con menos de 1000 bibliokarmas recibe cantPaginas * 5") {
            LibroComun(libroData).plusBiblioKarma(500, 0) shouldBe 500
        }

        it("lector con exactamente 1000 bibliokarmas recibe cantPaginas * 2") {
            LibroComun(libroData).plusBiblioKarma(1000, 0) shouldBe 200
        }

        it("lector con mas de 1000 bibliokarmas recibe cantPaginas * 2") {
            LibroComun(libroData).plusBiblioKarma(2000, 0) shouldBe 200
        }
    }

    describe("LibroDedicatoria") {
        it("sin reservas previas devuelve 200") {
            LibroDedicatoria(libroData).plusBiblioKarma(0, 0) shouldBe 200
        }

        it("con reservas previas suma 10 por cada una") {
            LibroDedicatoria(libroData).plusBiblioKarma(0, 5) shouldBe 250
        }

        it("los bibliokarmas del usuario no afectan el calculo") {
            LibroDedicatoria(libroData).plusBiblioKarma(500, 3) shouldBe
                    LibroDedicatoria(libroData).plusBiblioKarma(2000, 3)
        }
    }

    describe("LibroColeccionable") {
        it("suma la quinta parte de los bibliokarmas del usuario mas las paginas") {
            LibroColeccionable(libroData).plusBiblioKarma(2000, 0) shouldBe 500
        }

        it("la quinta parte se redondea hacia arriba") {
            LibroColeccionable(libroData).plusBiblioKarma(503, 0) shouldBe 201
        }

        it("la cantidad de reservas no afecta el calculo") {
            LibroColeccionable(libroData).plusBiblioKarma(2000, 0) shouldBe
                    LibroColeccionable(libroData).plusBiblioKarma(2000, 99)
        }
    }
})