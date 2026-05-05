package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Qualifier("customUserDetailsService")
    private final UserDetailsService userDetailsService;

    // ═══════════════════════════════════════════════════════════
    // URLS PUBLIQUES (accessibles sans authentification)
    // ═══════════════════════════════════════════════════════════

    private static final String[] PUBLIC_URL = {
            "/login",
            "/refresh-token",
            "/error",
            "/notFound",
            "/accessDenied",
            "/sessionExpired",
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/manifest.json",
            "/sw.js",
            "/icon-512.png",
            "/favicon.ico",
            "/"
    };

    // ═══════════════════════════════════════════════════════════
    // BEANS
    // ═══════════════════════════════════════════════════════════

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ═══════════════════════════════════════════════════════════
    // CONFIGURATION DE SÉCURITÉ
    // ═══════════════════════════════════════════════════════════

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver CSRF pour JWT
                .csrf(AbstractHttpConfigurer::disable)

                // Gestion de session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // ═══════════════════════════════════════════════
                // GESTION DES EXCEPTIONS → REDIRECTION VERS VUES
                // ═══════════════════════════════════════════════
                .exceptionHandling(ex -> ex
                        // 401 - Non authentifié → page login
                        .authenticationEntryPoint((request, response, authException) -> {
                            CookieUtils.deleteCookie(response, "JWT_TOKEN");
                            CookieUtils.deleteCookie(response, "REFRESH_TOKEN");
                            response.sendRedirect("/login?expired=true");
                        })
                        // 403 - Accès refusé → page accessDenied
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/accessDenied");
                        })
                )

                // ═══════════════════════════════════════════════
                // DÉCONNEXION
                // ═══════════════════════════════════════════════
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JWT_TOKEN", "REFRESH_TOKEN")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )

                // ═══════════════════════════════════════════════
                // AUTORISATIONS
                // ═══════════════════════════════════════════════
                .authorizeHttpRequests(auth -> auth
                        // ✅ Routes publiques
                        .requestMatchers(PUBLIC_URL).permitAll()

                        // ✅ Zone admin
                        .requestMatchers("/admin/**")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")

                        // ✅ Journal
                        .requestMatchers("/journal/**")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")

                        // ✅ Enseignant (+ SUPER_ADMIN + ADMIN_INSTITUT)
                        .requestMatchers("/enseignant/**")
                        .hasAnyRole("ENSEIGNANT", "SUPER_ADMIN", "ADMIN_INSTITUT")


                        .requestMatchers("/api/**")
                        //.hasAnyRole("ENSEIGNANT", "SUPER_ADMIN, "ADMIN_INSTITUT", "ASSISTANT")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT"," ASSISTANT", "ENSEIGNANT")
                        // ✅ Étudiant
                        .requestMatchers("/etudiant/**")
                        .hasAnyRole("ETUDIANT", "SUPER_ADMIN", "ADMIN_INSTITUT")

                        // ✅ Surveillant
                        .requestMatchers("/surveillant/**")
                        .hasAnyRole("SURVEILLANT", "SUPER_ADMIN", "ADMIN_INSTITUT")

                        // ✅ Assistant
                        .requestMatchers("/assistant/**")
                        .hasAnyRole("ASSISTANT", "SUPER_ADMIN", "ADMIN_INSTITUT")

                        // ✅ Dashboard
                        .requestMatchers("/dashboard").authenticated()

                        // ✅ API instituts
                        .requestMatchers("/api/instituts/**")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")

                        // ✅ Tout le reste
                        .anyRequest().authenticated()

                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}