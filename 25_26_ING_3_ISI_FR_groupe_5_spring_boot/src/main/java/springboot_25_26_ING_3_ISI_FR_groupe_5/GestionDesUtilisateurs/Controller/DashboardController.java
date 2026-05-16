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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.InstitutContexteActif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.InstitutContexteActifRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AnneeAcademiqueService anneeService;
    private final InstitutContexteActifRepository contexteRepo;
    private final InstitutSecurityService securityService;
    private final IJournalActionService journalActionService;

    private static final List<String> ROLES_PRIORITE = List.of(
            "SUPER_ADMIN", "ADMIN_INSTITUT", "ASSISTANT", "ENSEIGNANT", "SURVEILLANT", "ETUDIANT"
    );

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard(
            @AuthenticationPrincipal Utilisateur utilisateur,
            Model model) {

        // 1. Rôles & Priorité
        boolean estSuperAdmin    = utilisateur.hasRole("SUPER_ADMIN");
        boolean estAdminInstitut = utilisateur.hasRole("ADMIN_INSTITUT");
        boolean estEnseignant    = utilisateur.hasRole("ENSEIGNANT");
        boolean estEtudiant      = utilisateur.hasRole("ETUDIANT");
        boolean estAssistant     = utilisateur.hasRole("ASSISTANT");
        boolean estSurveillant   = utilisateur.hasRole("SURVEILLANT");
        boolean estAdmin         = estSuperAdmin || estAdminInstitut;

        String rolePrincipal = ROLES_PRIORITE.stream()
                .filter(utilisateur::hasRole)
                .findFirst()
                .orElse("INCONNU");

        // 2. Institut
        Institut institut = utilisateur.getInstitut();
        Long institutId = institut != null ? institut.getId() : null;

        // 3. ✅ CONTEXTE ACTIF (remplace l'ancien findByActiveTrue)
        Annee_academique anneeActive = null;
        Semestre semestreActif = null;
        if (institutId != null) {
            InstitutContexteActif contexte = contexteRepo.findByInstitutId(institutId).orElse(null);
            if (contexte != null) {
                anneeActive = contexte.getAnneeAcademique();
                semestreActif = contexte.getSemestre();
            }
        }

        // 4. Sélecteur institut (Super Admin uniquement)
        boolean showInstitutSelector = estSuperAdmin;
        String currentInstitutName = estSuperAdmin
                ? securityService.getCurrentInstitutName()
                : (institut != null ? institut.getNom() : "Global");

        // 5. Permissions fines
        boolean peutVoirJournal = estSuperAdmin || estAdminInstitut;
        boolean peutFaireAppel  = estEnseignant || estSurveillant;
        boolean peutGererEmploiDuTemps = estAssistant || estAdmin;

        // 6. Exposition au modèle
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("institut", institut);
        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("semestreActif", semestreActif);
        model.addAttribute("rolePrincipal", rolePrincipal);

        model.addAttribute("estSuperAdmin", estSuperAdmin);
        model.addAttribute("estAdminInstitut", estAdminInstitut);
        model.addAttribute("estAdmin", estAdmin);
        model.addAttribute("estEnseignant", estEnseignant);
        model.addAttribute("estEtudiant", estEtudiant);
        model.addAttribute("estAssistant", estAssistant);
        model.addAttribute("estSurveillant", estSurveillant);

        model.addAttribute("showInstitutSelector", showInstitutSelector);
        model.addAttribute("currentInstitutName", currentInstitutName);
        model.addAttribute("peutVoirJournal", peutVoirJournal);
        model.addAttribute("peutFaireAppel", peutFaireAppel);
        model.addAttribute("peutGererEmploiDuTemps", peutGererEmploiDuTemps);

        // 7. Dernières actions (Admin)
        if (estAdmin) {
            try {
                model.addAttribute("dernieresActions",
                        journalActionService.getByUtilisateur(utilisateur.getId(), PageRequest.of(0, 5)).getContent());
            } catch (Exception e) {
                log.warn("Erreur chargement dernières actions: {}", e.getMessage());
            }
        }

        log.info("Dashboard — {} [{}] — Institut: {}", utilisateur.getEmail(), rolePrincipal, currentInstitutName);
        return "dashboard";
    }
}