package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    // ✅ AJOUTER CETTE MÉTHODE - Exclure les routes publiques
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        List<String> excludedPaths = List.of(
                "/login",
                "/register",
                "/refresh-token",  // ← CRUCIAL : éviter la boucle
                "/error",
                "/css",
                "/js",
                "/images",
                "/webjars"
        );

        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.debug("🔍 JwtFilter — requête : {} {}", request.getMethod(), request.getRequestURI());

        String token = CookieUtils.extractCookie(request, "JWT_TOKEN");

        if (token == null) {
            log.debug("❌ Aucun cookie JWT_TOKEN trouvé pour : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("✅ Cookie JWT_TOKEN trouvé");

        try {
            String email = jwtService.extractUsername(token);
            log.debug("📧 Email extrait du token : {}", email);

            if (email != null && securityContextHolderStrategy.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.debug("👤 UserDetails chargé : {}", userDetails.getUsername());
                log.debug("🔑 Authorities : {}", userDetails.getAuthorities());

                boolean valid = jwtService.isTokenValid(token, userDetails);
                log.debug("🔐 Token valide : {}", valid);

                if (valid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContext context = securityContextHolderStrategy.createEmptyContext();
                    context.setAuthentication(authToken);
                    securityContextHolderStrategy.setContext(context);
                    securityContextRepository.saveContext(context, request, response);

                    log.debug("✅ Authentification enregistrée pour : {}", email);
                    log.debug("🎭 Rôles enregistrés : {}", userDetails.getAuthorities());
                }
            } else {
                log.debug("⚠️ Email null ou authentification déjà présente");
            }

        } catch (ExpiredJwtException e) {
            log.warn("⏰ Token expiré — redirection vers /refresh-token");
            response.sendRedirect("/refresh-token");
            return;
        } catch (Exception e) {
            log.error("💥 Erreur dans JwtFilter : {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
