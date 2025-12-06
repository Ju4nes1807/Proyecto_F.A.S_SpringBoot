package com.fas.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.fas.project.service.CustomUserDetailsService;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        private final CustomUserDetailsService userDetailsService;

        public SecurityConfig(CustomUserDetailsService userDetailsService) {
                this.userDetailsService = userDetailsService;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/landingpage", "/register", "/proceso-registro",
                                                                "/css/**", "/js/**", "/images/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/lider/**").hasRole("LIDER")
                                                .requestMatchers("/jugador/**").hasRole("JUGADOR")
                                                .requestMatchers("/escuelas/**", "/categorias/**", "/canchas/**", "/jugadores/**")
                                                .hasAnyRole("ADMIN", "LIDER")
                                                // NUEVAS RUTAS DE TORNEOS
                                                .requestMatchers("/torneos/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/torneos/lider/**").hasRole("LIDER")
                                                .requestMatchers("/torneos/jugador/**").hasRole("JUGADOR")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/proceso-login")
                                                .successHandler((request, response, authentication) -> {
                                                        // Redirigir según el rol
                                                        String redirectUrl = "/";
                                                        if (authentication.getAuthorities().stream()
                                                                        .anyMatch(a -> a.getAuthority()
                                                                                        .equals("ROLE_ADMIN"))) {
                                                                redirectUrl = "/admin/dashboard";
                                                        } else if (authentication.getAuthorities().stream()
                                                                        .anyMatch(a -> a.getAuthority()
                                                                                        .equals("ROLE_LIDER"))) {
                                                                redirectUrl = "/lider/dashboard";
                                                        } else if (authentication.getAuthorities().stream()
                                                                        .anyMatch(a -> a.getAuthority()
                                                                                        .equals("ROLE_JUGADOR"))) {
                                                                redirectUrl = "/jugador/dashboard";
                                                        }
                                                        response.sendRedirect(redirectUrl);
                                                })
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/landingpage?logout=true")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}