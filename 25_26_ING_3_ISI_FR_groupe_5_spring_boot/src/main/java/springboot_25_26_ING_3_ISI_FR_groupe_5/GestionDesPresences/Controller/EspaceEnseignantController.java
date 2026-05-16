package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDate;

@Controller
@RequestMapping("/enseignant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ENSEIGNANT')")
public class EspaceEnseignantController {

    private final StatsService statsService;
    private final PlageHoraireService plageHoraireService;
    private final AppelsService appelsService;
    private final SessionAppelService sessionAppelService;
    private final AppelsMapper appelsMapper;

    // ══════════════════════════════════════════
    // DASHBOARD PRINCIPAL (STATS + COURS DU JOUR)
    // ══════════════════════════════════════════
    /*@GetMapping("/mon-espace")
    public String dashboard(Model model, @AuthenticationPrincipal Utilisateur u) {
        Long ensId = ((Enseignant) u).getId();

        // 1. Progression des UEs (Heures réalisées / prévues)
        model.addAttribute("uesProgression", statsService.getProgressionEnseignant(ensId));

        // 2. Cours programmés pour aujourd'hui
        model.addAttribute("coursAujourdhui", plageHoraireService.findCoursEnseignantAujourdhui(ensId, LocalDate.now()));

        return "enseignant/dashboard";
    }*/

    @GetMapping("/mon-espace")
    public String dashboard(Model model, @AuthenticationPrincipal Utilisateur u) {
        if (!(u instanceof Enseignant enseignant)) {
            return "redirect:/accessDenied";
        }

        Long ensId = enseignant.getId();

        // ✅ Progression des UEs
        model.addAttribute("uesProgression", statsService.getProgressionEnseignant(ensId));

        // ✅ Cours du jour (avec getters accessibles)
        model.addAttribute("coursAujourdhui", plageHoraireService.findCoursEnseignantAujourdhui(ensId, LocalDate.now()));

        return "/enseignant/dashboardEnseignant";
    }

    // ══════════════════════════════════════════
    // INTERFACE D'APPEL (La page que tu as analysée)
    // ══════════════════════════════════════════
    @GetMapping("/appels/cours/{plageId}")
    public String interfaceAppel(@PathVariable Long plageId, Model model) {
        PlageHoraire ph = plageHoraireService.findEntityById(plageId);

        model.addAttribute("plageHoraire", ph);

        // Liste des appels (étudiants) mappés en DTO
        var appels = appelsService.getByPlageHoraire(plageId);
        model.addAttribute("appels", appelsMapper.toResponseList(appels));

        // Session active si elle existe
        try {
            model.addAttribute("sessionActive", sessionAppelService.getSessionActive(plageId));
        } catch (Exception e) {
            model.addAttribute("sessionActive", null);
        }

        // Compteurs rapides
        model.addAttribute("nbPresents", appels.stream().filter(a -> a.isPresent()).count());
        model.addAttribute("nbRetards", appels.stream().filter(a -> a.isRetard()).count());
        model.addAttribute("nbAbsents", appels.stream().filter(a -> a.isAbsentToutLeCours()).count());

        return "appels/appel_interface";
    }

}