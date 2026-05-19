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

        // ✅ CORRIGÉ — cast sécurisé avec instanceof
        // AVANT : (Etudiant) utilisateur → ClassCastException si mauvais rôle
        if (!(utilisateur instanceof Etudiant etudiant)) {
            return "redirect:/accessDenied";
        }

        // 1. Statistiques semestre actif
        model.addAttribute("stats", statsService.getStatsEtudiant(etudiant.getId()));

        // 2. Session active pour sa classe
        // ✅ CORRIGÉ — etudiant.getClasse() peut être null (LAZY ou non affecté)
        if (etudiant.getClasse() != null) {
            try {
                model.addAttribute("sessionActive",
                        sessionAppelService.getSessionActivePourClasse(etudiant.getClasse().getId()));
            } catch (Exception e) {
                log.debug("Aucune session active pour la classe {} : {}",
                        etudiant.getClasse().getId(), e.getMessage());
                model.addAttribute("sessionActive", null);
            }
        } else {
            model.addAttribute("sessionActive", null);
        }

        // 3. Historique récent des appels
        var historique = appelsService.getByEtudiant(etudiant.getId());
        model.addAttribute("appels", appelsMapper.toResponseList(historique));

        model.addAttribute("etudiant", etudiant);

        // ✅ CORRIGÉ — template étudiant, pas le dashboard enseignant
        // AVANT : "enseignant/dashboardEnseignant" → affichait la mauvaise vue
        return "etudiant/dashboardEtudiant";
    }

    // ══════════════════════════════════════════
    // VALIDATION DE PRÉSENCE (QR / PIN)
    // ══════════════════════════════════════════

    @PostMapping("/valider-presence")
    public String validerPresence(
            @ModelAttribute AppelsRequest req,
            @AuthenticationPrincipal Utilisateur u,
            RedirectAttributes ra) {

        // ✅ CORRIGÉ — cast sécurisé
        if (!(u instanceof Etudiant etudiant)) {
            return "redirect:/accessDenied";
        }

        try {
            log.info("Etudiant {} valide sa presence", etudiant.getId());
            appelsService.validerParCode(req, etudiant.getId());
            ra.addFlashAttribute("succes", "Votre presence a ete enregistree !");
        } catch (Exception e) {
            log.warn("Echec validation presence etudiant {} : {}", etudiant.getId(), e.getMessage());
            ra.addFlashAttribute("erreur", "Echec de validation : " + e.getMessage());
        }
        return "redirect:/etudiant/mon-espace";
    }
}