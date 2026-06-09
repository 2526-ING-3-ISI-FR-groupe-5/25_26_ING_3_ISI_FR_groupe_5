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
@EnableMethodSecurity  // ← Active @PreAuthorize au niveau méthode
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Qualifier("customUserDetailsService")
    private final UserDetailsService userDetailsService;

    // ✅ URLs publiques — Patterns compatibles PathPatternParser (Spring 6+)
    private static final String[] PUBLIC_URL = {
            "/login",
            "/logout",
            "/refresh-token",
            "/notFound",
            "/accessDenied",
            "/sessionExpired",
            "/error",
            "/admin",
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",

            "/css/**",
            "/js/**",
            "/images/**",

            "/manifest.json",
            "/sw.js",

            "/icon-192x192.png",
            "/icon-512x512.png",

            "/fa.ico",
            "/favicon.ico",

            "/",
            "/actuator/health"
    };

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, exc) -> res.sendRedirect("/login"))
                        .accessDeniedHandler((req, res, exc) -> res.sendRedirect("/accessDenied"))
                )

                // ═══════════════════════════════════════════════════════════
                // 🔐 RÈGLES D'AUTORISATION — ORDRE: spécifique → général
                // ═══════════════════════════════════════════════════════════
                .authorizeHttpRequests(auth -> auth
                        // 1. URLs publiques
                        .requestMatchers(PUBLIC_URL).permitAll()

                        // 2. 🆕 Endpoints étudiants — Patterns corrigés (sans /** suivi de texte)
                        .requestMatchers("/api/appels/valider-code").hasRole("ETUDIANT")
                        .requestMatchers("/api/sessions-appel/*/active").hasAnyRole("ETUDIANT", "ENSEIGNANT", "ASSISTANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/api/sessions-appel/classe/*/active").hasAnyRole("ETUDIANT", "ENSEIGNANT", "ASSISTANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/etudiant/**").hasAnyRole("ETUDIANT", "SUPER_ADMIN", "ADMIN_INSTITUT")

                        // 3. Enseignants & Assistants
                        .requestMatchers("/api/appels/**").hasAnyRole("ENSEIGNANT", "ASSISTANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/api/sessions-appel/**").hasAnyRole("ENSEIGNANT", "ASSISTANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/enseignant/**").hasAnyRole("ENSEIGNANT", "SUPER_ADMIN", "ADMIN_INSTITUT")

                        // 4. Admins
                        .requestMatchers("/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/journal/**").hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/api/instituts/**").hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")

                        // 5. Autres rôles
                        .requestMatchers("/surveillant/**").hasAnyRole("SURVEILLANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/assistant/**").hasAnyRole("ASSISTANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        // EDT — accessible aux utilisateurs authentifies ; le @PreAuthorize
                        // par methode contraint WRITE aux ASSISTANT/admin et READ a tous.
                        .requestMatchers("/emplois-du-temps/**").authenticated()
                        // Classes — meme principe : @PreAuthorize au niveau methode gere les roles
                        .requestMatchers("/classes/**").authenticated()

                        // 6. Fallback
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JWT_TOKEN", "REFRESH_TOKEN")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}