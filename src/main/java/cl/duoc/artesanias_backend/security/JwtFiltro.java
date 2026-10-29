package cl.duoc.artesanias_backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Se ejecuta en CADA petición, antes de llegar al controlador.
// Si la petición trae "Authorization: Bearer <token>" válido, deja al usuario autenticado.
@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFiltro(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtService.esValido(token)) {
                Claims datos = jwtService.leerToken(token);
                String email = datos.getSubject();
                String rol = datos.get("rol", String.class);

                // Spring Security espera los roles con el prefijo "ROLE_"
                List<SimpleGrantedAuthority> permisos = List.of(new SimpleGrantedAuthority("ROLE_" + rol));

                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(email, null, permisos);

                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            }
        }

        // Continúa con el resto de la cadena (si no hubo token, Spring decidirá si la ruta es pública)
        filterChain.doFilter(request, response);
    }
}
