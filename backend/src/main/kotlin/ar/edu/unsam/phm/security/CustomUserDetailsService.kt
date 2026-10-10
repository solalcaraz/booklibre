package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val usuarioRepository: UsuarioRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        usuarioRepository.findByEmail(username)
            ?.mapToUserDetails()
            ?: throw UsernameNotFoundException("User not found")

    private fun Usuario.mapToUserDetails(): UserDetails =
        User.builder()
            // El login busca por email, pero el username interno es el id para que
            // authentication.name sea el id del usuario en el resto de la app
            .username(this.id.toString())
            .password(this.password)
            .roles(this.tipoUsuario.name)
            .build()
}