package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/* UserDetails representa la información core del Usuario del dominio, Spring no trabaja con nuestro
dominio, sino con su propio modelo. Entonces necesita un adapter que convierta nuestro usuario en algo
que spring entienda */
/* UserDetailsService es una interfaz que se encarga de justamente eso, le estamos
diciendo a Spring: cuando necesites un usuario, busca en mi DB usando este repo y adaptalo a tu modelo */
/* Spring Security la usa para interactuar con nuestra fuente de datos y validar usuarios
durante la autenticación */

@Service
class CustomUserDetailsService(
    private val usuarioRepository: UsuarioRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        usuarioRepository.findByEmail(username)
            ?.mapToUserDetails() // extension method implementado abajo
            ?: throw UsernameNotFoundException("User not found") // misma guarda implementada en los services

    private fun Usuario.mapToUserDetails(): UserDetails =
        User.builder()
            .username(this.email)
            .password(this.password)
            .roles(this.tipoUsuario.label)
            .build()
}