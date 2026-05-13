package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    /**
     * Affiche les informations de l'utilisateur actuellement connecté
     * Utile pour diagnostiquer les problèmes d'autorisation
     */
    @GetMapping("/user-info")
    public String userInfo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("message", "❌ Aucun utilisateur connecté");
            return "debug/user-info";
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Utilisateur user) {
            // ✅ Données de l'utilisateur
            model.addAttribute("email", user.getEmail());
            model.addAttribute("nom", user.getNom());
            model.addAttribute("prenom", user.getPrenom());
            model.addAttribute("active", user.isEnabled());
            model.addAttribute("type", user.getClass().getSimpleName());

            // ✅ Institut
            if (user.getInstitut() != null) {
                model.addAttribute("institut", user.getInstitut().getNom());
                model.addAttribute("institutId", user.getInstitut().getId());
            } else {
                model.addAttribute("institut", "❌ AUCUN INSTITUT ASSIGNÉ");
                model.addAttribute("institutId", "null");
            }

            // ✅ Rôles en base de données
            List<String> rolesDb = user.getRoles().stream()
                    .map(Role::getNom)
                    .collect(Collectors.toList());
            model.addAttribute("rolesDb", rolesDb);

            // ✅ Authorities actuelles (ce que Spring Security voit vraiment)
            List<String> authorities = auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList());
            model.addAttribute("authorities", authorities);

            // ✅ Vérifications
            boolean hasSuperAdminRole = user.hasRole("SUPER_ADMIN");
            boolean hasSuperAdminAuth = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
            boolean hasAdminInstitutRole = user.hasRole("ADMIN_INSTITUT");
            boolean hasAdminInstitutAuth = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN_INSTITUT"));

            model.addAttribute("hasSuperAdminRole", hasSuperAdminRole);
            model.addAttribute("hasSuperAdminAuth", hasSuperAdminAuth);
            model.addAttribute("hasAdminInstitutRole", hasAdminInstitutRole);
            model.addAttribute("hasAdminInstitutAuth", hasAdminInstitutAuth);

            // ✅ Diagnostic
            StringBuilder diagnostic = new StringBuilder();
            diagnostic.append("\n📋 DIAGNOSTIC DE SÉCURITÉ:\n");
            diagnostic.append("━".repeat(50)).append("\n");

            if (!user.isEnabled()) {
                diagnostic.append("❌ PROBLÈME: L'utilisateur est INACTIF (active=false)\n");
            }

            if (rolesDb.isEmpty()) {
                diagnostic.append("❌ PROBLÈME: L'utilisateur n'a AUCUN RÔLE assigné\n");
            }

            if (!hasSuperAdminRole && !hasAdminInstitutRole && !rolesDb.isEmpty()) {
                diagnostic.append("⚠️ ATTENTION: L'utilisateur a des rôles mais pas SUPER_ADMIN ni ADMIN_INSTITUT\n");
            }

            if (hasSuperAdminRole && !hasSuperAdminAuth) {
                diagnostic.append("❌ PROBLÈME: hasRole('SUPER_ADMIN') = true mais authority manquante\n");
            }

            if (!hasSuperAdminRole && !hasAdminInstitutRole) {
                diagnostic.append("❌ CRITIQUE: Cet utilisateur ne peut PAS accéder à /admin/**\n");
                diagnostic.append("   → Il faut assigner SUPER_ADMIN ou ADMIN_INSTITUT en base de données\n");
            }

            if (hasAdminInstitutRole && user.getInstitut() == null) {
                diagnostic.append("❌ PROBLÈME: ADMIN_INSTITUT assigné mais institut_id = NULL\n");
            }

            diagnostic.append("━".repeat(50)).append("\n");
            model.addAttribute("diagnostic", diagnostic.toString());
            model.addAttribute("message", "✅ Utilisateur connecté");

        } else {
            model.addAttribute("message", "⚠️ Principal n'est pas une instance de Utilisateur: " + principal);
        }

        return "debug/user-info";
    }

    /**
     * Affiche les traces de debug
     */
    @GetMapping("/logs")
    public String logs(Model model) {
        log.debug("🔍 Appel debug endpoint");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            log.info("👤 Utilisateur: {}", auth.getName());
            log.info("🔑 Authorities: {}", auth.getAuthorities());
        }

        model.addAttribute("message", "Vérifiez les logs de la console => recherchez '🔍'");
        return "debug/logs";
    }
}

