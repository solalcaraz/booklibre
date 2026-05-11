//package ar.edu.unsam.phm.repository
//
//import ar.edu.unsam.phm.domain.Libro
//import org.springframework.stereotype.Repository
//import java.util.*
//import ar.edu.unsam.phm.dto.MiLibroProjection
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.Pageable
//import org.springframework.data.jpa.domain.Specification
//import org.springframework.data.jpa.repository.EntityGraph
//import org.springframework.data.jpa.repository.JpaRepository
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor
//import org.springframework.data.jpa.repository.Modifying
//import org.springframework.data.jpa.repository.Query
//import org.springframework.data.repository.query.Param
//
//@Repository
//interface LibroRepository : JpaRepository<Libro, Int>, JpaSpecificationExecutor<Libro> {
//    @EntityGraph(attributePaths = ["dueno"])
//    override fun findById(id: Int): Optional<Libro>
//    //en Kotlin preferimos el manejo de nulos nativo (Libro?), pero como estamos sobreescribiendo
//    //el método de la interfaz de Spring para agregar @EntityGraph, estamos obligados a mantener Optional
//
//    @Modifying
//    @Query(value = "UPDATE libro SET tipo_libro = :nuevoTipo WHERE id = :id", nativeQuery = true)
//    fun cambiarTipoDeLibro(id: Int, nuevoTipo: String)
//
//    @EntityGraph(attributePaths = ["dueno"])
//    override fun findAll(spec: Specification<Libro>, pageable: Pageable): Page<Libro>
//
//
//
//    /* Traigo todos los libros de un usuario y los etiqueto en una projection */
//    /* Para no hacer una projection que actúa como DTO se me ocurrió agregar una columna
//    calculada en Libro que sea estadoReserva, seguir trabajando con objetos Libro,
//    y luego serializar a MiLibroDTO, pero no sé qué tan feo puede ser */
//
//    @Query("""
//    select new ar.edu.unsam.phm.dto.MiLibroProjection(
//        l.id,
//        l.data.titulo,
//        l.data.autor,
//        l.data.genero,
//        case
//            when exists (
//                select 1 from Reserva r
//                where r.libro.id = l.id
//                and current_date between r.fechaDesde and r.fechaHasta
//            )
//            then 'PRESTADO'
//            else 'DISPONIBLE'
//        end,
//        l.data.fechaPublicacion,
//        l.data.portada
//    )
//    from Libro l
//    where l.dueno.id = :usuarioId
//    and l.activo = true
//    order by
//        case
//            when :sort = 'disponible,asc' then
//                case
//                    when exists (
//                        select 1
//                        from Reserva r
//                        where r.libro.id = l.id
//                        and current_date between r.fechaDesde and r.fechaHasta
//                    ) then 1 else 0
//                end
//        end asc,
//        case
//            when :sort = 'disponible,desc' then
//                case
//                    when exists (
//                        select 1
//                        from Reserva r
//                        where r.libro.id = l.id
//                        and current_date between r.fechaDesde and r.fechaHasta
//                    ) then 1 else 0
//                end
//        end desc,
//        case when :sort = 'agregado,asc' then l.data.fechaPublicacion end asc,
//        case when :sort = 'agregado,desc' then l.data.fechaPublicacion end desc
//    """)
//    fun todosLibrosDe(
//        @Param("usuarioId") usuarioId: Int,
//        @Param("sort") sort: String?,
//        pageable: Pageable
//    ): Page<MiLibroProjection>
//
//    @Query("""
//    select new ar.edu.unsam.phm.dto.MiLibroProjection(
//        l.id,
//        l.data.titulo,
//        l.data.autor,
//        l.data.genero,
//        'PRESTADO',
//        l.data.fechaPublicacion,
//        l.data.portada
//    )
//    from Libro l
//        where l.dueno.id = :usuarioId
//        and l.activo = true
//        and exists (
//            select 1
//            from Reserva r
//            where r.libro.id = l.id
//            and current_date between r.fechaDesde and r.fechaHasta
//        )
//        order by
//            case when :sort = 'agregado,asc' then l.data.fechaPublicacion end asc,
//            case when :sort = 'agregado,desc' then l.data.fechaPublicacion end desc
//    """)
//    fun librosPrestadosDe(
//        @Param("usuarioId") usuarioId: Int,
//        @Param("sort") sort: String?,
//        pageable: Pageable
//    ): Page<MiLibroProjection>
//
//    @Query("""
//    select new ar.edu.unsam.phm.dto.MiLibroProjection(
//        l.id,
//        l.data.titulo,
//        l.data.autor,
//        l.data.genero,
//        'DISPONIBLE',
//        l.data.fechaPublicacion,
//        l.data.portada
//    )
//    from Libro l
//        where l.dueno.id = :usuarioId
//        and l.activo = true
//        and not exists (
//            select 1
//            from Reserva r
//            where r.libro.id = l.id
//            and current_date between r.fechaDesde and r.fechaHasta
//        )
//        order by
//            case when :sort = 'agregado,asc' then l.data.fechaPublicacion end asc,
//            case when :sort = 'agregado,desc' then l.data.fechaPublicacion end desc
//    """)
//    fun librosDisponiblesDe(
//        @Param("usuarioId") usuarioId: Int,
//        @Param("sort") sort: String?,
//        pageable: Pageable
//    ): Page<MiLibroProjection>
//
//    //Lo uso en reserva para el DTO (evito query + 1)
//    @EntityGraph(attributePaths = ["dueno"])
//    fun findConDuenoById(id:Int): Optional<Libro>
//}