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

        model.addAttribute("stats", statsService.getStatsEtudiant(etudiant.getId()));

        if (etudiant.getClasse() != null) {
            try {
                model.addAttribute("sessionActive",
                        sessionAppelService.getSessionActivePourClasse(etudiant.getClasse().getId()));
            } catch (Exception e) {
                log.debug("Aucune session active classe {} : {}",
                        etudiant.getClasse().getId(), e.getMessage());
                model.addAttribute("sessionActive", null);
            }
        } else {
            model.addAttribute("sessionActive", null);
        }

        model.addAttribute("appels",
                appelsMapper.toResponseList(appelsService.getByEtudiant(etudiant.getId())));
        model.addAttribute("etudiant", etudiant);

        return "etudiant/dashboardEtudiant";
    }

    // ══════════════════════════════════════════
    // PAGE PWA — VALIDATION DE PRÉSENCE
    // GET /etudiant/valider-presence
    // ══════════════════════════════════════════

    @GetMapping("/valider-presence")
    public String afficherValidation(Model model, @AuthenticationPrincipal Utilisateur utilisateur) {
        if (!(utilisateur instanceof Etudiant etudiant)) {
            return "redirect:/accessDenied";
        }

        if (etudiant.getClasse() != null) {
            Long classeId = etudiant.getClasse().getId();

            // ✅ Session normale (en ligne — code PIN 6 chiffres, 3 min)
            try {
                model.addAttribute("session",
                        sessionAppelService.getSessionActivePourClasse(classeId));
            } catch (Exception e) {
                log.debug("Aucune session normale : {}", e.getMessage());
                model.addAttribute("session", null);
            }

            // ✅ AJOUTÉ — Session offline (code 8 caractères, durée du cours)
            try {
                model.addAttribute("sessionOffline",
                        sessionAppelService.getSessionOfflineActive(classeId));
            } catch (Exception e) {
                log.debug("Aucune session offline : {}", e.getMessage());
                model.addAttribute("sessionOffline", null);
            }

        } else {
            model.addAttribute("session", null);
            model.addAttribute("sessionOffline", null);
        }

        return "etudiant/valider-presence";
    }

    // ══════════════════════════════════════════
    // POST — VALIDATION PRÉSENCE (PWA)
    // POST /etudiant/valider-presence
    // ══════════════════════════════════════════

    @PostMapping("/valider-presence")
    public String validerPresence(
            @ModelAttribute AppelsRequest req,
            @AuthenticationPrincipal Utilisateur u,
            RedirectAttributes ra) {

        if (!(u instanceof Etudiant etudiant)) {
            return "redirect:/accessDenied";
        }

        // ✅ CORRIGÉ — La vérification GPS ne bloque que les sessions normales.
        // Pour une session offline, la sync différée peut arriver sans GPS précis.
        boolean isOfflineSync = req.getSessionAppelId() != null
                && sessionAppelService.findById(req.getSessionAppelId()).isOffline();

        if (!isOfflineSync
                && (req.getLatitudeEtudiant() == null || req.getLongitudeEtudiant() == null)) {
            ra.addFlashAttribute("erreur",
                    "Position GPS manquante. Autorisez la geolocalisation et reessayez.");
            return "redirect:/etudiant/valider-presence";
        }

        try {
            log.info("Etudiant {} valide sa presence (offline={})", etudiant.getId(), isOfflineSync);
            appelsService.validerParCode(req, etudiant.getId());
            ra.addFlashAttribute("succes", "Votre presence a ete enregistree !");

        } catch (Exception e) {
            log.warn("Echec validation presence etudiant {} : {}", etudiant.getId(), e.getMessage());

            String msg = e.getMessage();
            if (msg != null && msg.contains("perimetre")) {
                msg = "Vous etes trop loin de la salle de cours.";
            } else if (msg != null && msg.contains("Code invalide")) {
                msg = "Code incorrect. Verifiez le code affiche par votre enseignant.";
            } else if (msg != null && msg.contains("expire")) {
                msg = "Session expiree. Demandez a votre enseignant de renouveler le code.";
            } else if (msg != null && msg.contains("termine")) {
                msg = "Le cours est termine. Rapprochez-vous de votre enseignant.";
            }
            ra.addFlashAttribute("erreur", msg);
        }

        return "redirect:/etudiant/valider-presence";
    }
}