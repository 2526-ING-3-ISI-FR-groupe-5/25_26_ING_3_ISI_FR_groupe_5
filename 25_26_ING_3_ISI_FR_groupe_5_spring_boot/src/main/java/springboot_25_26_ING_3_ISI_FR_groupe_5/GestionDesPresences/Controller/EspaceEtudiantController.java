package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.AppelsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.SessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.StatsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

@Slf4j
@Controller
@RequestMapping("/etudiant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
public class EspaceEtudiantController {

    private final AppelsService appelsService;
    private final StatsService statsService;
    private final SessionAppelService sessionAppelService;
    private final AppelsMapper appelsMapper;

    // ══════════════════════════════════════════
    // DASHBOARD ÉTUDIANT
    // ══════════════════════════════════════════

    @GetMapping("/mon-espace")
    public String dashboard(Model model, @AuthenticationPrincipal Utilisateur utilisateur) {
        if (!(utilisateur instanceof Etudiant etudiant)) {
            return "redirect:/accessDenied";
        }

        // Stats semestre actif
        model.addAttribute("stats", statsService.getStatsEtudiant(etudiant.getId()));

        // Session active pour sa classe — null si aucune
        if (etudiant.getClasse() != null) {
            try {
                var session = sessionAppelService.getSessionActivePourClasse(etudiant.getClasse().getId());
                model.addAttribute("sessionActive", session);
            } catch (Exception e) {
                log.debug("Aucune session active classe {} : {}", etudiant.getClasse().getId(), e.getMessage());
                model.addAttribute("sessionActive", null);
            }
        } else {
            model.addAttribute("sessionActive", null);
        }

        // Historique des appels
        model.addAttribute("appels",
                appelsMapper.toResponseList(appelsService.getByEtudiant(etudiant.getId())));

        model.addAttribute("etudiant", etudiant);

        return "etudiant/dashboardEtudiant";
    }

    // ══════════════════════════════════════════
    // PAGE PWA — VALIDATION DE PRÉSENCE
    // GET /etudiant/valider-presence
    // ══════════════════════════════════════════

    /**
     * Affiche la page de validation de présence (PWA).
     * Charge la session active de la classe de l'étudiant.
     * Si aucune session active : affiche un message d'attente.
     *
     * Le code PIN et les coordonnées GPS sont saisis sur cette page
     * puis soumis via POST /etudiant/valider-presence.
     */
    @GetMapping("/valider-presence")
    public String afficherValidation(Model model, @AuthenticationPrincipal Utilisateur utilisateur) {
        if (!(utilisateur instanceof Etudiant etudiant)) {
            return "redirect:/accessDenied";
        }

        // Charger la session active si elle existe
        if (etudiant.getClasse() != null) {
            try {
                var session = sessionAppelService.getSessionActivePourClasse(
                        etudiant.getClasse().getId());
                // On ne passe pas le code au template — sécurité
                model.addAttribute("session", session);
            } catch (Exception e) {
                log.debug("Aucune session active pour affichage validation : {}", e.getMessage());
                model.addAttribute("session", null);
            }
        } else {
            model.addAttribute("session", null);
        }

        return "etudiant/valider-presence";
    }

    // ══════════════════════════════════════════
    // POST — VALIDATION PRÉSENCE (PWA)
    // POST /etudiant/valider-presence
    // ══════════════════════════════════════════

    /**
     * Reçoit le code PIN + coordonnées GPS et valide la présence.
     *
     * Champs attendus dans AppelsRequest :
     * - sessionAppelId   : id de la session
     * - codeSaisi        : code PIN 6 chiffres
     * - latitudeEtudiant : depuis navigator.geolocation
     * - longitudeEtudiant: depuis navigator.geolocation
     */
    @PostMapping("/valider-presence")
    public String validerPresence(
            @ModelAttribute AppelsRequest req,
            @AuthenticationPrincipal Utilisateur u,
            RedirectAttributes ra) {

        if (!(u instanceof Etudiant etudiant)) {
            return "redirect:/accessDenied";
        }

        // Vérification GPS côté serveur — double sécurité
        if (req.getLatitudeEtudiant() == null || req.getLongitudeEtudiant() == null) {
            ra.addFlashAttribute("erreur",
                    "Position GPS manquante. Autorisez la geolocalisation et reessayez.");
            return "redirect:/etudiant/valider-presence";
        }

        try {
            log.info("Etudiant {} valide sa presence (lat={}, lng={})",
                    etudiant.getId(),
                    req.getLatitudeEtudiant(),
                    req.getLongitudeEtudiant());

            appelsService.validerParCode(req, etudiant.getId());
            ra.addFlashAttribute("succes", "Votre presence a ete enregistree !");

        } catch (Exception e) {
            log.warn("Echec validation presence etudiant {} : {}", etudiant.getId(), e.getMessage());

            // Message d'erreur lisible selon le type d'échec
            String msg = e.getMessage();
            if (msg != null && msg.contains("perimetre")) {
                msg = "Vous etes trop loin de la salle de cours. Rapprochez-vous et reessayez.";
            } else if (msg != null && msg.contains("Code invalide")) {
                msg = "Code incorrect. Verifiez le code affiche par votre enseignant.";
            } else if (msg != null && msg.contains("expire")) {
                msg = "La session a expire. Demandez a votre enseignant de renouveler le code.";
            }
            ra.addFlashAttribute("erreur", msg);
        }

        return "redirect:/etudiant/valider-presence";
    }
}