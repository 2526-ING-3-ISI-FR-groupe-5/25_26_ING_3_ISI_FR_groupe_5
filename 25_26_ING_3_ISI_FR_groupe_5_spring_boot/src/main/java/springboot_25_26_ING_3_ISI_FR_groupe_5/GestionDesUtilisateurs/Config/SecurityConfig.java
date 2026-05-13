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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Qualifier("customUserDetailsService")
    private final UserDetailsService userDetailsService;

    private static final String[] PUBLIC_URL = {
            "/login",
            "/refresh-token/**",
            "/notFound/**",
            "/accessDenied/**",
            "/sessionExpired",
            "/error/**",
            "/admin1/**",
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/accessDenied")
                        )
                )

                // ✅ AJOUT OBLIGATOIRE — sans ça, permitAll() ne fonctionne pas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URL).permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/journal/**").hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/enseignant/**").hasAnyRole("ENSEIGNANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/etudiant/**").hasAnyRole("ETUDIANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/surveillant/**").hasAnyRole("SURVEILLANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/assistant/**").hasAnyRole("ASSISTANT", "SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/api/instituts/**").hasAnyRole("SUPER_ADMIN", "ADMIN_INSTITUT")
                        .requestMatchers("/api/appels/**").hasAnyRole("ENSEIGNANT", "ASSISTANT", "ADMIN_INSTITUT")
                        .requestMatchers("/api/sessions-appel/**").hasAnyRole("ETUDIANT", "ENSEIGNANT", "ASSISTANT", "ADMIN_INSTITUT")
                        .requestMatchers("/emploisDeTemps/**").hasAnyRole("SUPER_ADMIN", "ASSISTANT")
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