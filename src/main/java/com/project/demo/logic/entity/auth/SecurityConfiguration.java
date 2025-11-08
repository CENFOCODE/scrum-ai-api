package com.project.demo.logic.entity.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración principal de Spring Security para la aplicación.
 *
 * <p>Responsable de definir:</p>
 * <ul>
 *     <li>Requisitos de autenticación para los endpoints REST.</li>
 *     <li>Rutas públicas y rutas protegidas.</li>
 *     <li>Manejo de sesiones sin estado (stateless) basado en JWT.</li>
 *     <li>Integración del filtro personalizado para validar tokens JWT.</li>
 * </ul>
 *
 * <p>Esta clase se encarga de:</p>
 * <ul>
 *     <li>Deshabilitar CSRF (necesario para APIs REST sin estado).</li>
 *     <li>Permitir acceso público a:
 *         <ul>
 *             <li>Endpoints de autenticación.</li>
 *             <li>Señalización WebRTC (endpoint /webrtc).</li>
 *             <li>Handshake de WebSocket.</li>
 *         </ul>
 *     </li>
 *     <li>Exigir autenticación para cualquier otra ruta.</li>
 *     <li>Registrar el filtro {@link JwtAuthenticationFilter} antes del filtro estándar de Spring.</li>
 * </ul>
 *
 * <p><strong>Relación con otros componentes:</strong></p>
 * <ul>
 *     <li>{@code JwtAuthenticationFilter} — Intercepta peticiones y valida el token JWT.</li>
 *     <li>{@code AuthenticationProvider} — Carga los detalles del usuario y valida credenciales.</li>
 *     <li>Frontend — Debe enviar el token JWT dentro del header Authorization.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor para inyectar los componentes necesarios.
     *
     * @param jwtAuthenticationFilter Filtro personalizado para validar tokens JWT.
     * @param authenticationProvider  Proveedor de autenticación que procesa credenciales
     *                                y carga la información del usuario.
     */
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
                                 AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad de Spring.
     *
     * <p>Define:</p>
     * <ul>
     *     <li>Qué endpoints son públicos o requieren autenticación.</li>
     *     <li>El uso de sesiones sin estado para trabajar con JWT.</li>
     *     <li>La integración del filtro JWT antes del filtro de autenticación estándar.</li>
     * </ul>
     *
     * @param http Objeto de configuración proporcionado por Spring Security.
     * @return Instancia configurada de {@link SecurityFilterChain}.
     * @throws Exception Si la configuración presenta errores.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Endpoint público para WebRTC (señalización)
                        .requestMatchers("/webrtc/**").permitAll()

                        // Rutas públicas de autenticación
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

                        // Handshake WebSocket debe ser público
                        .requestMatchers("/ws/**").permitAll()

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // Manejo de sesión sin estado (JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Registrar proveedor de autenticación y filtro JWT
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
