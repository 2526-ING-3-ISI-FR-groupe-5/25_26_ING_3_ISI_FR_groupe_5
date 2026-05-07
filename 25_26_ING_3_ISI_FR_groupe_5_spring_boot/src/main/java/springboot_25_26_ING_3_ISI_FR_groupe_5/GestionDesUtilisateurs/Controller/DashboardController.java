package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.JournalActionService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AnneeAcademiqueService anneeService;
    private final InstitutSecurityService securityService;
    private final IJournalActionService journalActionService;

    // ✅ Priorité des rôles
    private static final List<String> ROLES_PRIORITE = List.of(
            "SUPER_ADMIN",
            "ADMIN_INSTITUT",
            "ASSISTANT",
            "ENSEIGNANT",
            "SURVEILLANT",
            "ETUDIANT"
    );

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard(
            @AuthenticationPrincipal Utilisateur utilisateur,
            Model model) {

        // ============================================
        // Vérification des rôles
        // ✅ hasRole() vérifie sans le préfixe ROLE_
        // ============================================
        boolean estSuperAdmin    = utilisateur.hasRole("SUPER_ADMIN");
        boolean estAdminInstitut = utilisateur.hasRole("ADMIN_INSTITUT");
        boolean estEnseignant    = utilisateur.hasRole("ENSEIGNANT");
        boolean estEtudiant      = utilisateur.hasRole("ETUDIANT");
        boolean estAssistant     = utilisateur.hasRole("ASSISTANT");
        boolean estSurveillant   = utilisateur.hasRole("SURVEILLANT");

        // ✅ estAdmin = vrai si SUPER_ADMIN ou ADMIN_INSTITUT
        boolean estAdmin = estSuperAdmin || estAdminInstitut;

        // ✅ Rôle principal selon priorité
        String rolePrincipal = ROLES_PRIORITE.stream()
                .filter(utilisateur::hasRole)
                .findFirst()
                .orElse("INCONNU");

        // ============================================
        // Institut de l'utilisateur
        // ============================================
        Institut institut = utilisateur.getInstitut();
        Long institutId = institut != null ? institut.getId() : null;

        // ============================================
        // Année académique active
        // ============================================
        Annee_academique anneeActive = null;
        if (institutId != null) {
            try {
                anneeActive = anneeService
                        .getAnneeActivePourInstitut(institutId);
            } catch (Exception e) {
                log.warn("Aucune année active pour l'institut {}",
                        institutId);
            }
        }

        // ============================================
        // Sélecteur d'institut — Super Admin uniquement
        // ============================================
        boolean showInstitutSelector = estSuperAdmin;
        String currentInstitutName = estSuperAdmin
                ? securityService.getCurrentInstitutName()
                : (institut != null ? institut.getNom() : "");

        // ============================================
        // Permissions fines
        // ============================================
        boolean peutVoirJournal = estSuperAdmin || estAdminInstitut;
        boolean peutFaireAppel  = estEnseignant || estSurveillant;
        boolean peutGererEmploiDuTemps = estAssistant || estAdmin;

        // ============================================
        // Attributs pour la vue
        // ============================================
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("institut", institut);
        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("rolePrincipal", rolePrincipal);

        // ✅ Flags rôles
        model.addAttribute("estSuperAdmin", estSuperAdmin);
        model.addAttribute("estAdminInstitut", estAdminInstitut);
        model.addAttribute("estAdmin", estAdmin);
        model.addAttribute("estEnseignant", estEnseignant);
        model.addAttribute("estEtudiant", estEtudiant);
        model.addAttribute("estAssistant", estAssistant);
        model.addAttribute("estSurveillant", estSurveillant);

        // ✅ Sélecteur institut
        model.addAttribute("showInstitutSelector", showInstitutSelector);
        model.addAttribute("currentInstitutName", currentInstitutName);

        // ✅ Permissions
        model.addAttribute("peutVoirJournal", peutVoirJournal);
        model.addAttribute("peutFaireAppel", peutFaireAppel);
        model.addAttribute("peutGererEmploiDuTemps", peutGererEmploiDuTemps);

        // ✅ Dernières actions — Admin uniquement
        if (estAdmin) {
            try {
                model.addAttribute("dernieresActions",
                        journalActionService.getByUtilisateur(
                                utilisateur.getId(),
                                PageRequest.of(0, 5)
                        ).getContent()
                );
            } catch (Exception e) {
                log.warn("Erreur chargement dernières actions");
            }
        }

        log.info("Dashboard — {} [{}] — Institut: {}",
                utilisateur.getEmail(),
                rolePrincipal,
                institut != null ? institut.getNom() : "Global"
        );

        return "dashboard";
    }
}