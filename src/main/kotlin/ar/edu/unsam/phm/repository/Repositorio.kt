package ar.edu.unsam.phm.repository
import ar.edu.unsam.phm.dto.PaginationResponseDTO
import ar.edu.unsam.phm.exceptions.NotFoundException

// Se implementa el patron strategy para determinar el método de búsqueda según el tipo de repositorio
interface SearchStrategy<T> {
    fun matches(obj: T, value: String): Boolean
}

interface EntidadConId {
    var id: Int?
}

open class Repositorio<T : EntidadConId>(
    private val searchStrategy: SearchStrategy<T>,
) { // le exijo a T tener id para poder manipular T.id
    val objetos = mutableMapOf<Int, T>()
    private var proximoId = 1

    fun create(obj: T) {
        if (obj.id != null) {
            throw IllegalArgumentException("No se puede crear un objeto que ya tiene ID.")
        }

        obj.id = proximoId++
        objetos[obj.id!!] = obj
    }

    fun update(obj: T) {
        validarConId(obj)
        val id = obj.id!!

        if (!objetos.containsKey(id)) {
            throw NotFoundException("No se encontró el objeto con ID $id.")
        }

        objetos[id] = obj
    }

    fun delete(obj: T) {
        validarConId(obj)
        val id = obj.id!!

        if (objetos.remove(id) == null) {
            throw NotFoundException("No se encontró el objeto con ID $id.")
        }
    }

    fun getById(id: Int): T {
        return objetos[id] ?: throw NotFoundException("No se encontró el objeto con ID $id.") // no deberia explotar
    }

    fun getAll(): List<T> = objetos.values.toList()

    fun existeConId(id: Int): Boolean = objetos.containsKey(id)

    private fun validarConId(obj: T) {
        if (obj.id == null) {
            throw IllegalArgumentException("El objeto no tiene ID asignado.")
        }
    }

    fun search(value: String): List<T> {
        return objetos.values.filter { searchStrategy.matches(it, value) }
    }

    fun clearInit(){
        objetos.clear()
        proximoId = 0
    }

    //Consultar: puede ir aca y directamente llamar desde el mismo repo
//    fun <T> paginate(
//        items: List<T>,
//        page: Int,
//        size: Int
//    ): PaginationResponseDTO<T> {
//        ...
//    }
}