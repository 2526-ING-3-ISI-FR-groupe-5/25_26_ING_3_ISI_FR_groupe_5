package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.*;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AnneeAcademiqueService anneeService;

    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal Utilisateur utilisateur,
            Model model
    ) {
        // Récupérer l'année active
        Annee_academique anneeActive = null;
        try {
            anneeActive = anneeService.getAnneeActive();
        } catch (Exception e) {
            // Pas d'année active
        }

        // Vérifier les rôles de l'utilisateur
        boolean estAdmin = utilisateur.hasRole("ADMIN");
        boolean estEnseignant = utilisateur.hasRole("ENSEIGNANT");
        boolean estEtudiant = utilisateur.hasRole("ETUDIANT");
        boolean estAssistant = utilisateur.hasRole("ASSISTANT");
        boolean estSurveillant = utilisateur.hasRole("SURVEILLANT");

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("estAdmin", estAdmin);
        model.addAttribute("estEnseignant", estEnseignant);
        model.addAttribute("estEtudiant", estEtudiant);
        model.addAttribute("estAssistant", estAssistant);
        model.addAttribute("estSurveillant", estSurveillant);

        return "dashboard";
    }
}