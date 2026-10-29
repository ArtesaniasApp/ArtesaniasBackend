package cl.duoc.artesanias_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Define qué rutas son públicas, cuáles requieren login y cuáles son solo para el admin
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFiltro jwtFiltro;

    public SecurityConfig(JwtFiltro jwtFiltro) {
        this.jwtFiltro = jwtFiltro;
    }

    // Encriptador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF no es necesario en una API REST con JWT (no usamos cookies de sesión)
                .csrf(csrf -> csrf.disable())

                // Sin sesiones: cada petición se identifica solo con su token
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(rutas -> rutas
                        // Públicas: login, registro y ver el catálogo
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/clasificaciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/estados-producto").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/estados-pedido").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/metodos-pago").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Solo administradores
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Todo lo demás (favoritos, pedidos, perfil) requiere estar logueado
                        .anyRequest().authenticated()
                )

                // Si falta el token responde 401 (no autenticado)
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                // Nuestro filtro JWT se ejecuta antes del filtro de login estándar de Spring
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
