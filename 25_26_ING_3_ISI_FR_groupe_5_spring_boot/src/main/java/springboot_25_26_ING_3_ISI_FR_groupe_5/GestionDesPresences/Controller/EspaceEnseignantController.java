package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.AnneeAcademiqueMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDate;

/**
 * Dashboard principal de l'enseignant.
 *
 * ✅ Les routes /enseignant/appels/** ont été déplacées dans AppelsMvcController
 * pour éviter le conflit AmbiguousHandlerMappingException.
 * Ce controller gère uniquement le dashboard et la progression.
 */
@Slf4j
@Controller
@RequestMapping("/enseignant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ENSEIGNANT')")
public class EspaceEnseignantController {

    private final StatsService statsService;
    private final PlageHoraireService plageHoraireService;
    private final AnneeAcademiqueMapper anneeMapper;

    // ══════════════════════════════════════════
    // DASHBOARD PRINCIPAL
    // ══════════════════════════════════════════

    @GetMapping("/mon-espace")
    public String dashboard(Model model, @AuthenticationPrincipal Utilisateur u) {
        if (!(u instanceof Enseignant enseignant)) {
            return "redirect:/accessDenied";
        }

        Long ensId = enseignant.getId();
        var uesProgression = statsService.getProgressionEnseignant(ensId);
        var coursAujourdhui = plageHoraireService.findCoursEnseignantAujourdhui(ensId, LocalDate.now());

        // Taux moyen calculé côté Java — évite le risque SpEL dans Thymeleaf
        int tauxMoyen = (uesProgression == null || uesProgression.isEmpty()) ? 0
                : (int) uesProgression.stream()
                .mapToInt(s -> (int) Math.round(s.getProgression()))
                .average()
                .orElse(0);

        model.addAttribute("uesProgression", uesProgression);
        model.addAttribute("coursAujourdhui", coursAujourdhui);
        model.addAttribute("tauxMoyen", tauxMoyen);

        return "enseignant/dashboardEnseignant";
    }

    // ══════════════════════════════════════════
    // PAGE D'APPEL
    // Délégué à AppelsMvcController via /enseignant/appels/{id}/appel
    // ══════════════════════════════════════════
    // Aucune route /appels/** ici — tout est dans AppelsMvcController
}