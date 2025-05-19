package oliveiradev.inventario.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true) // Habilita segurança em nível de método (@Secured, @RolesAllowed, @PreAuthorize)
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Define o BCrypt como o codificador de senhas
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilitar CSRF para APIs stateless (com JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sessões stateless para JWT
                .authorizeHttpRequests(authz -> authz
                        // Por enquanto, permitindo tudo para facilitar o desenvolvimento inicial dos endpoints de usuário.
                        // Depois vamos restringir:
                        // .requestMatchers("/api/auth/**").permitAll() // Endpoints de autenticação
                        // .requestMatchers("/api/usuarios/registrar").permitAll() // Exemplo de endpoint público de registro
                        // .requestMatchers("/api/admin/**").hasRole("ADMIN") // Exemplo de restrição
                        // .anyRequest().authenticated() // Todas as outras requisições exigem autenticação
                        .anyRequest().permitAll() // CUIDADO: Permitindo tudo temporariamente!
                );
        return http.build();
    }
}