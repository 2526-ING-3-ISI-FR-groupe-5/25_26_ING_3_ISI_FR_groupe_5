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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // ✅ @Qualifier pour indiquer quel bean utiliser
    @Qualifier("customUserDetailsService")
    private final UserDetailsService userDetailsService;

    private static final String[] PUBLIC_URL = {
            "/login",
            "/refresh-token",
            "/notFound",
            "/accessDenied",
            "/sessionExpired",
            "/error",
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

                // ✅ IF Thymeleaf → IF_REQUIRED au lieu de STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login")
                        )
                        .accessDeniedPage("/notFound")
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URL).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/enseignant/**").hasRole("ENSEIGNANT")
                        .requestMatchers("/etudiant/**").hasRole("ETUDIANT")
                        .requestMatchers("/surveillant/**").hasRole("SURVEILLANT")
                        .requestMatchers("/assistant/**").hasRole("ASSISTANT")
                        .requestMatchers("/journal/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // ✅ Logout sans injecter RefreshTokenService dans SecurityConfig
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