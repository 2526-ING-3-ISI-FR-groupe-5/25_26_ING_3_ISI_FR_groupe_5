package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.CookieUtils;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.JwtService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.RefreshTokenService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UtilisateurRepository utilisateurRepository;

    // ══════════════════════════════════════════
    // PAGE LOGIN
    // ══════════════════════════════════════════
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String expired,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("erreur", "Email ou mot de passe incorrect");
        }
        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté avec succès");
        }
        if (expired != null) {
            model.addAttribute("erreur", "Votre session a expiré, veuillez vous reconnecter");
        }
        return "login";
    }

    // ══════════════════════════════════════════
    // TRAITEMENT LOGIN
    // ══════════════════════════════════════════
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // Vérifier que le compte est actif
            if (!utilisateur.isActive()) {
                redirectAttributes.addFlashAttribute("erreur",
                        "Votre compte est désactivé");
                return "redirect:/login?error=true";
            }

            // Générer JWT
            String jwt = jwtService.generateJwtToken(utilisateur);

            // Générer Refresh Token
            var refreshToken = refreshTokenService.creer(email);

            // Stocker dans les cookies
            CookieUtils.addCookie(response, "JWT_TOKEN", jwt, 86400);
            CookieUtils.addCookie(response, "REFRESH_TOKEN",
                    refreshToken.getToken(), 604800); // 7 jours

            log.info("✅ Connexion réussie pour : {}", email);

            return "redirect:/dashboard";

        } catch (BadCredentialsException e) {
            log.warn("❌ Mauvais identifiants pour : {}", email);
            redirectAttributes.addFlashAttribute("erreur",
                    "Email ou mot de passe incorrect");
            return "redirect:/login?error=true";

        } catch (DisabledException e) {
            log.warn("❌ Compte désactivé : {}", email);
            redirectAttributes.addFlashAttribute("erreur",
                    "Votre compte est désactivé");
            return "redirect:/login?error=true";

        } catch (Exception e) {
            log.error("💥 Erreur lors de la connexion : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erreur",
                    "Une erreur est survenue, veuillez réessayer");
            return "redirect:/login?error=true";
        }
    }

    // ══════════════════════════════════════════
    // REFRESH TOKEN
    // ══════════════════════════════════════════
    @GetMapping("/refresh-token")
    public String refreshToken(
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        String refreshTokenValue = CookieUtils.extractCookie(request, "REFRESH_TOKEN");

        if (refreshTokenValue == null) {
            return "redirect:/login?expired=true";
        }

        try {
            var refreshToken = refreshTokenService.verifier(refreshTokenValue);
            Utilisateur utilisateur = (Utilisateur) refreshToken.getUtilisateur();

            // Générer nouveau JWT
            String newJwt = jwtService.generateJwtToken(utilisateur);
            CookieUtils.addCookie(response, "JWT_TOKEN", newJwt, 86400);

            log.info("🔄 Token rafraîchi pour : {}", utilisateur.getEmail());
            return "redirect:/dashboard";

        } catch (Exception e) {
            log.warn("⏰ Refresh token invalide : {}", e.getMessage());
            CookieUtils.deleteCookie(response, "JWT_TOKEN");
            CookieUtils.deleteCookie(response, "REFRESH_TOKEN");
            return "redirect:/login?expired=true";
        }
    }
}
