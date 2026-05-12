package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.AppelsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.SessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.StatsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

@Controller
@RequestMapping("/etudiant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
public class EspaceEtudiantController {

    private final AppelsService appelsService;
    private final StatsService statsService;
    private final SessionAppelService sessionAppelService;
    private final AppelsMapper appelsMapper;

    @GetMapping("/mon-espace")
    public String dashboard(Model model, @AuthenticationPrincipal Utilisateur utilisateur) {
        Etudiant etudiant = (Etudiant) utilisateur;

        // 1. Statistiques du semestre actif
        model.addAttribute("stats", statsService.getStatsEtudiant(etudiant.getId()));

        // 2. Vérifier s'il y a un appel en cours pour sa classe
        SessionAppel sessionActive = sessionAppelService.getSessionActivePourClasse(etudiant.getClasse().getId());
        model.addAttribute("sessionActive", sessionActive);

        // 3. Historique récent des appels
        var historique = appelsService.getByEtudiant(etudiant.getId());
        model.addAttribute("appels", appelsMapper.toResponseList(historique));

        return "etudiants/dashboard";
    }

    @PostMapping("/valider-presence")
    public String validerPresence(@ModelAttribute AppelsRequest req,
                                  @AuthenticationPrincipal Utilisateur u,
                                  RedirectAttributes ra) {
        try {
            appelsService.validerParCode(req, ((Etudiant) u).getId());
            ra.addFlashAttribute("succes", "Votre présence a été enregistrée !");
        } catch (Exception e) {
            ra.addFlashAttribute("erreur", "Échec de validation : " + e.getMessage());
        }
        return "redirect:/etudiant/mon-espace";
    }
}