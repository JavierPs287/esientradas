package edu.esi.ds.esientradas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable()) // Modificado: CSRF deshabilitado ya que allowCredentials es false
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/compras/**",
                    "/busqueda/**",
                    "/pagar/**",
                    "/reservas/**",
                    "/escenarios/**"
                ).permitAll()
                .anyRequest().denyAll()
            )
            .sessionManagement(session -> session
                .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
            );
        
        return http.build();
    }
}