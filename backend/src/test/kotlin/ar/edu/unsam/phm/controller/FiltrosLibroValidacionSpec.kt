package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.helpers.ReservaMapper
import ar.edu.unsam.phm.security.JwtAuthenticationFilter
import ar.edu.unsam.phm.security.SecurityConfiguration
import ar.edu.unsam.phm.service.ClickLibroService
import ar.edu.unsam.phm.service.ReseniaService
import ar.edu.unsam.phm.service.ReservaService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(ReservaController::class)
@AutoConfigureMockMvc
@Import(SecurityConfiguration::class)
class FiltrosLibroValidacionSpec(
    @Autowired val mockMvc: MockMvc
) {
    @MockBean lateinit var reservaService: ReservaService
    @MockBean lateinit var reservaMapper: ReservaMapper
    @MockBean lateinit var reseniaService: ReseniaService
    @MockBean lateinit var clickLibroService: ClickLibroService
    @MockBean lateinit var authenticationProvider: AuthenticationProvider
    @MockBean lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @BeforeEach
    fun dejarPasarFiltroJwt() {
        doAnswer { invocation ->
            invocation.getArgument<FilterChain>(2).doFilter(
                invocation.getArgument<ServletRequest>(0),
                invocation.getArgument<ServletResponse>(1)
            )
            null
        }.`when`(jwtAuthenticationFilter).doFilter(any(), any(), any())
    }

    private fun buscar(query: String) = mockMvc.perform(get("/reserva/librosDisponibles?$query"))

    @Test
    @WithMockUser(username = "1", roles = ["LECTOR"])
    @DisplayName("Una fecha de inicio pasada responde 400 con el mensaje de la validación")
    fun fecha_pasada() {
        val ayer = LocalDate.now().minusDays(1)
        buscar("fechaDesde=$ayer&fechaHasta=${LocalDate.now()}")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("La fecha de inicio no puede ser en el pasado"))
    }

    @Test
    @WithMockUser(username = "1", roles = ["LECTOR"])
    @DisplayName("Un rango de fechas invertido responde 400 con el mensaje de la validación")
    fun rango_invertido() {
        val hoy = LocalDate.now()
        buscar("fechaDesde=${hoy.plusDays(5)}&fechaHasta=${hoy.plusDays(2)}")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("La fecha de fin no puede ser anterior a la de inicio"))
    }

    @Test
    @WithMockUser(username = "1", roles = ["LECTOR"])
    @DisplayName("Un rango de páginas invertido responde 400 con el mensaje de la validación")
    fun rango_paginas_invertido() {
        buscar("cantPaginasMin=500&cantPaginasMax=100")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("El rango de páginas es inválido"))
    }
}
