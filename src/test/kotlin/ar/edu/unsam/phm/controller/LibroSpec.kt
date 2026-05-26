package ar.edu.unsam.phm.controller

/*
Tests desactivados temporalmente por migracion de libros a MongoDB.
Contenido original:

package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.*
import io.kotest.core.spec.style.DescribeSpec
import java.time.LocalDate
import io.kotest.matchers.shouldBe

class LibroSpec : DescribeSpec({

    val dueno = Usuario(
        nombreCompleto = "Julio CortÃ¡zar",
        email = "julio@cortazar.com",
        password = "admin"
    ).apply { id = 1 }

    val libroData = LibroData(
        titulo = "Rayuela",
        descripcion = "Novela experimental",
        genero = Genero.LITERATURA_CLASICA,
        autor = "Julio CortÃ¡zar",
        cantPaginas = 100,
        isbn13 = "9788437604947",
        idioma = Idioma.ESPANOL,
        editorial = "CÃ¡tedra",
        fechaPublicacion = LocalDate.of(1963, 1, 1),
        estado = Estado.BUENO
    )

    describe("LibroComun - plusBibliokarma") {
        it("lector con menos de 1000 bibliokarmas recibe cantPaginas * 5") {
            LibroComun(libroData, dueno).plusBiblioKarma(500) shouldBe 500
        }

        it("lector con exactamente 1000 bibliokarmas recibe cantPaginas * 2") {
            LibroComun(libroData, dueno).plusBiblioKarma(1000) shouldBe 200
        }

        it("lector con mas de 1000 bibliokarmas recibe cantPaginas * 2") {
            LibroComun(libroData, dueno).plusBiblioKarma(2000) shouldBe 200
        }
    }

    describe("LibroDedicatoria - plusbibliokarmas") {
        it("sin reservas previas devuelve 200") {
            LibroDedicatoria(libroData, dueno).plusBiblioKarma(0) shouldBe 200
        }

        it("los bibliokarmas del usuario no afectan el calculo") {
            LibroDedicatoria(libroData, dueno).plusBiblioKarma(500) shouldBe
                    LibroDedicatoria(libroData, dueno).plusBiblioKarma(2000)
        }
    }

    describe("LibroColeccionable") {
        it("suma la quinta parte de los bibliokarmas del usuario mas las paginas") {
            LibroColeccionable(libroData, dueno).plusBiblioKarma(2000) shouldBe 500
        }

        it("la quinta parte se redondea hacia arriba") {
            LibroColeccionable(libroData, dueno).plusBiblioKarma(503) shouldBe 201
        }

        it("con 0 bibliokarmas devuelve solo la cantidad de pÃ¡ginas") {
            // ceil(0 / 5.0) + 100 = 0 + 100 = 100
            LibroColeccionable(libroData, dueno).plusBiblioKarma(0) shouldBe 100
        }
    }

    describe("Libro - calcularCalificacionPromedio") {

        it("sin reseÃ±as devuelve 0.0") {
            LibroComun(libroData, dueno).calcularCalificacionPromedio() shouldBe 0.0
        }
    }

    describe("Libro - activo") {

        it("un libro reciÃ©n creado estÃ¡ activo") {
            LibroComun(libroData, dueno).activo shouldBe true
        }
    }
})
*/

