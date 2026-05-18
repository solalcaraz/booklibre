package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.AuthenticationResponse
import ar.edu.unsam.phm.dto.UsuarioPatchRequest
import ar.edu.unsam.phm.exceptions.BadRequestException
import ar.edu.unsam.phm.exceptions.ConflictException
import ar.edu.unsam.phm.exceptions.NotFoundException
import ar.edu.unsam.phm.helpers.UsuarioMapper
import ar.edu.unsam.phm.repository.LibroDocumentRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.security.JwtProperties
import ar.edu.unsam.phm.security.TokenHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import kotlin.text.toIntOrNull


@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val reservaRepository: ReservaRepository,
    private val libroRepository: LibroDocumentRepository,
    private val usuarioMapper: UsuarioMapper,
    private val tokenHelper: TokenHelper,
    private val jwtProperties: JwtProperties
) {
    @Transactional(readOnly = true)
    fun getUsuarioById(id: Int): Usuario {
        val usuario = usuarioRepository.findById(id).orElseThrow { NotFoundException("Usuario no encontrado") }

        usuario.librosLeidos = reservaRepository.cantidadLeidosDe(usuario.id!!)
        usuario.librosPrestados = cantidadPrestadosDe(usuario.id!!)

        return usuario
    }

    @Transactional
    fun updateUsuario(userPatch: UsuarioPatchRequest, id: Int): AuthenticationResponse {
        val usuario = usuarioRepository.findById(id).orElseThrow { NotFoundException("Usuario no encontrado") }

        val prevUser = usuarioRepository.findByEmail(userPatch.email)

        if (prevUser != null && prevUser.id != usuario.id) {
            throw ConflictException("Email ya utilizado por otro usuario")
        }

        val usuarioActualizado = usuarioMapper.fromDto(userPatch, usuario)

        usuarioRepository.save(usuarioActualizado)

        /* Si cambia el rol del usuario tengo que actualizar el token en esta request, o bien mandar al usuario
        a que inicie sesion de nuevo pero destroza la UX */
        val accessToken = tokenHelper.generateAccessToken(
            usuarioActualizado,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        val refreshToken = tokenHelper.generateRefreshToken(
            usuarioActualizado,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
        )

        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun getBibliokarma(id: String): Int {
        val idNumerico = id.toIntOrNull() ?: throw BadRequestException("Id incorrecto")
        val usuario = usuarioRepository.findById(idNumerico).orElseThrow { NotFoundException("Usuario no encontrado") }
        return usuario.bibliokarmas
    }

    /* Acá solamente estoy teniendo en cuenta los libros activos, deberían contar los dados de baja también */
    /* Y hago dos queries, una para todos los libros del usuario, y la siguiente para contar la
    cantidad de reservas correspondientes a esos libros */
    private fun cantidadPrestadosDe(usuarioId: Int): Int {
        val idsLibrosDelUsuario = libroRepository.findByDuenoIdAndActivoTrue(usuarioId).mapNotNull { it.id }

        if (idsLibrosDelUsuario.isEmpty()) return 0

        return reservaRepository.countByLibroMongoIdIn(idsLibrosDelUsuario)
    }
}
