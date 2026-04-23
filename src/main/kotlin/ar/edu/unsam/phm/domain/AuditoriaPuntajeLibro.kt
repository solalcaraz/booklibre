package ar.edu.unsam.phm.domain

import jakarta.annotation.PostConstruct
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.OffsetDateTime
import javax.sql.DataSource

@Entity
@Table(name = "auditoria_puntaje_libro")
class AuditoriaPuntajeLibro(
    @Column(name = "id_libro", nullable = false)
    val libroId: Int,

    @Column(name = "fecha_actualizacion", nullable = false)
    val fechaActualizacion: LocalDateTime = LocalDateTime.now(),

    @Column(name = "puntaje_anterior", nullable = false)
    val puntajeAnterior: Double,

    @Column(name = "puntaje_nuevo", nullable = false)
    val puntajeNuevo: Double,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)

@Component
class TriggerInitializer(private val dataSource: DataSource) {

    /* Es para indicar que se ejecute automaticamente el metodo una sola vez despues de que el bean se crea e inicializa sus dependencias.
    Entonces cuando arranca Spring, el metodo se ejecuta y crea el trigger en la bd
     */
    @PostConstruct
    fun crearTrigger() {
        val sql = """
            -- Función que ejecuta el trigger
            CREATE OR REPLACE FUNCTION fn_auditar_puntaje_libro()
            RETURNS TRIGGER AS $$
            
            DECLARE
                puntaje_viejo DOUBLE PRECISION;
                puntaje_actual DOUBLE PRECISION;
            
            BEGIN
                -- Promedio anterior: todas las reseñas del libro EXCEPTO la recién insertada
                SELECT COALESCE(AVG(r.puntuacion), 0)
                INTO puntaje_viejo
                FROM resenias r
                WHERE r.id_libro = NEW.id_libro
                    AND r.id <> NEW.id; -- excluye la reseña recién insertada para calcular el promedio anterior

                -- Promedio nuevo: todas las reseñas del libro INCLUYENDO la recién insertada
                SELECT COALESCE(AVG(r.puntuacion), 0)
                INTO puntaje_actual
                FROM resenias r
                WHERE r.id_libro = NEW.id_libro;

                -- Solo registra el puntaje si el puntaje realmente cambió
                IF puntaje_viejo <> puntaje_actual THEN
                    INSERT INTO auditoria_puntaje_libro
                        (id_libro, fecha_actualizacion, puntaje_anterior, puntaje_nuevo)
                    VALUES 
                        (NEW.id_libro, NOW(), puntaje_viejo, puntaje_actual);
                END IF;

                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql;

            -- Trigger que se dispara cuando se inserta una nueva reseña
            DROP TRIGGER IF EXISTS tg_auditar_puntaje_libro ON resenias;
            CREATE TRIGGER tg_auditar_puntaje_libro
                AFTER INSERT ON resenias
                FOR EACH ROW
                EXECUTE FUNCTION fn_auditar_puntaje_libro();
        """.trimIndent()

        // Este bloque de codigo abre la conexion con la bd y ejecuta el bloque de sql directamente desde kotlin
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(sql)
            }
        }
    }
}