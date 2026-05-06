package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Migration.MigrationRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Migration.MigrationResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeMigration;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.AnneeAcademiqueMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InscriptionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.MigrationMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/migration")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
public class MigrationController {

    private final MigrationService migrationService;
    private final AnneeAcademiqueService anneeService;
    private final AnneeAcademiqueMapper anneeMapper;
    private final InscriptionService inscriptionService;
    private final MigrationMapper migrationMapper;
    private final EtudiantRepository etudiantRepository;
    private final EnseignantRepository enseignantRepository;
    private final UERepository ueRepository;
    private final ClassesRepository classesRepository;
    private final FiliereRepository filiereRepository;
    private final NiveauService niveauService;
    private final InstitutSecurityService securityService;
    private final InstitutRepository institutRepository;

    // ═══════════════════════════════════════════════════════════
    // PAGE PRINCIPALE DE MIGRATION
    // ═══════════════════════════════════════════════════════════

    @GetMapping
    public String index(Model model, @RequestParam(required = false) Long institutId) {
        // 🆕 Résoudre l'institut
        Long institutCible = securityService.resolveInstitutId(institutId);

        Annee_academique anneeActive;
        List<Inscription> inscriptions;
        List<String> etudiantsSansDecision;

        if (institutCible != null) {
            anneeActive = anneeService.getAnneeActivePourInstitut(institutCible);
            inscriptions = inscriptionService.getByClasseAndAnnee(null, anneeActive.getId());
            etudiantsSansDecision = migrationService.getEtudiantsSansDecision();
        } else {
            anneeActive = null;
            inscriptions = List.of();
            etudiantsSansDecision = List.of();
        }

        if (inscriptions == null) {
            inscriptions = List.of();
        }

        long admis = inscriptions.stream()
                .filter(i -> i.getDecisionFinAnnee() == DecisionFinAnnee.ADMIS)
                .count();
        long redoublants = inscriptions.stream()
                .filter(i -> i.getDecisionFinAnnee() == DecisionFinAnnee.REDOUBLANT)
                .count();
        long exclus = inscriptions.stream()
                .filter(i -> i.getDecisionFinAnnee() == DecisionFinAnnee.EXCLU)
                .count();
        long diplomes = inscriptions.stream()
                .filter(i -> i.getDecisionFinAnnee() == DecisionFinAnnee.DIPLOME)
                .count();
        long sansDecision = inscriptions.stream()
                .filter(i -> i.getDecisionFinAnnee() == null)
                .count();

        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("annees", anneeMapper.toResponseList(
                institutCible != null ? anneeService.getByInstitut(institutCible) : anneeService.getAll()
        ));
        model.addAttribute("totalInscriptions", inscriptions.size());
        model.addAttribute("admis", admis);
        model.addAttribute("redoublants", redoublants);
        model.addAttribute("exclus", exclus);
        model.addAttribute("diplomes", diplomes);
        model.addAttribute("sansDecision", sansDecision);
        model.addAttribute("etudiantsSansDecision", etudiantsSansDecision);

        // 🆕 Filtrer les listes par institut
        if (institutCible != null) {
            model.addAttribute("etudiants", etudiantRepository.findByInstitutId(institutCible));
            model.addAttribute("enseignants", enseignantRepository.findByInstitutId(institutCible));
            // Pour les autres entités, vous pouvez ajouter des méthodes similaires
        } else {
            model.addAttribute("etudiants", etudiantRepository.findAll());
            model.addAttribute("enseignants", enseignantRepository.findAll());
        }
        model.addAttribute("ues", ueRepository.findAll());
        model.addAttribute("classes", classesRepository.findAll());
        model.addAttribute("filieres", filiereRepository.findAll());
        model.addAttribute("niveaux", niveauService.getAll());

        // 🆕 Pour le sélecteur d'institut
        if (securityService.shouldShowInstitutSelector()) {
            model.addAttribute("instituts", institutRepository.findAll());
            model.addAttribute("selectedInstitutId", institutCible);
        }
        model.addAttribute("currentInstitutName", securityService.getCurrentInstitutName());

        model.addAttribute("form", MigrationRequest.builder().build());

        return "migration/index";
    }

    // ═══════════════════════════════════════════════════════════
    // SIMULATION (DRY-RUN)
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/simuler")
    public String simuler(
            @RequestParam Long nouvelleAnneeId,
            @RequestParam(required = false) Long institutId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Long institutCible = securityService.resolveInstitutId(institutId);
            MigrationResultat resultat = migrationService.simulerPourInstitut(institutCible, nouvelleAnneeId);

            Institut institut = institutRepository.findById(institutCible).orElse(null);
            MigrationResponse response = migrationMapper.toMigrationResponse(
                    resultat, TypeMigration.COMPLETE, institutCible,
                    institut != null ? institut.getNom() : null
            );

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("info",
                    "🔮 Simulation terminée. Aucune donnée n'a été modifiée.");

        } catch (Exception e) {
            log.error("Erreur lors de la simulation de migration", e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/migration" + (institutId != null ? "?institutId=" + institutId : "");
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION COMPLÈTE
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/lancer")
    public String lancer(
            @Valid @ModelAttribute("form") MigrationRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Veuillez sélectionner une année cible");
            return "redirect:/admin/migration";
        }

        try {
            Long institutCible = securityService.resolveInstitutId(request.getInstitutId());
            MigrationResultat resultat = migrationService.migrerPourInstitut(
                    institutCible, request.getNouvelleAnneeId(), acteur
            );

            Institut institut = institutRepository.findById(institutCible).orElse(null);
            MigrationResponse response = migrationMapper.toMigrationResponse(
                    resultat, request.getTypeMigration(), institutCible,
                    institut != null ? institut.getNom() : null
            );

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes",
                    "✅ Migration complète effectuée avec succès !");

        } catch (Exception e) {
            log.error("Erreur lors de la migration complète", e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/migration" + (request.getInstitutId() != null ? "?institutId=" + request.getInstitutId() : "");
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : ÉTUDIANT
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/etudiant/{etudiantId}")
    public String migrerEtudiant(
            @PathVariable Long etudiantId,
            @RequestParam Long nouvelleAnneeId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            MigrationResultat resultat = migrationService.migrerEtudiant(etudiantId, nouvelleAnneeId, acteur);
            MigrationResponse response = migrationMapper.toMigrationResponse(resultat, TypeMigration.ETUDIANT);

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes", "✅ Étudiant migré avec succès !");

        } catch (Exception e) {
            log.error("Erreur lors de la migration de l'étudiant {}", etudiantId, e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/migration";
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : ENSEIGNANT
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/enseignant/{enseignantId}")
    public String migrerEnseignant(
            @PathVariable Long enseignantId,
            @RequestParam Long nouvelleAnneeId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            MigrationResultat resultat = migrationService.migrerEnseignant(enseignantId, nouvelleAnneeId, acteur);
            MigrationResponse response = migrationMapper.toMigrationResponse(resultat, TypeMigration.ENSEIGNANT);

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes", "✅ Enseignant migré avec succès !");

        } catch (Exception e) {
            log.error("Erreur lors de la migration de l'enseignant {}", enseignantId, e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/migration";
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : UE
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/ue/{ueId}")
    public String migrerUE(
            @PathVariable Long ueId,
            @RequestParam Long nouvelleAnneeId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            MigrationResultat resultat = migrationService.migrerUE(ueId, nouvelleAnneeId, acteur);
            MigrationResponse response = migrationMapper.toMigrationResponse(resultat, TypeMigration.UE);

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes", "✅ UE migrée avec succès !");

        } catch (Exception e) {
            log.error("Erreur lors de la migration de l'UE {}", ueId, e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/migration";
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : CLASSE
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/classe/{classeId}")
    public String migrerClasse(
            @PathVariable Long classeId,
            @RequestParam Long nouvelleAnneeId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            MigrationResultat resultat = migrationService.migrerClasse(classeId, nouvelleAnneeId, acteur);
            MigrationResponse response = migrationMapper.toMigrationResponse(resultat, TypeMigration.CLASSE);

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes", "✅ Classe migrée avec succès !");

        } catch (Exception e) {
            log.error("Erreur lors de la migration de la classe {}", classeId, e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/migration";
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : FILIÈRE
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/filiere/{filiereId}")
    public String migrerFiliere(
            @PathVariable Long filiereId,
            @RequestParam Long nouvelleAnneeId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            MigrationResultat resultat = migrationService.migrerFiliere(filiereId, nouvelleAnneeId, acteur);
            MigrationResponse response = migrationMapper.toMigrationResponse(resultat, TypeMigration.FILIERE);

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes", "✅ Filière migrée avec succès !");

        } catch (Exception e) {
            log.error("Erreur lors de la migration de la filière {}", filiereId, e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/migration";
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : NIVEAU
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/niveau/{niveauId}")
    public String migrerNiveau(
            @PathVariable Long niveauId,
            @RequestParam Long nouvelleAnneeId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            MigrationResultat resultat = migrationService.migrerNiveau(niveauId, nouvelleAnneeId, acteur);
            MigrationResponse response = migrationMapper.toMigrationResponse(resultat, TypeMigration.NIVEAU);

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes", "✅ Niveau migré avec succès !");

        } catch (Exception e) {
            log.error("Erreur lors de la migration du niveau {}", niveauId, e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/migration";
    }

    // ═══════════════════════════════════════════════════════════
    // ENREGISTREMENT D'UNE DÉCISION DE FIN D'ANNÉE
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/decision/{inscriptionId}")
    public String enregistrerDecision(
            @PathVariable Long inscriptionId,
            @RequestParam DecisionFinAnnee decision,
            @RequestParam(required = false) String observations,
            @RequestParam(required = false) Long classeId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            inscriptionService.enregistrerDecision(inscriptionId, decision, observations, acteur);
            redirectAttributes.addFlashAttribute("succes", "✅ Décision enregistrée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement de la décision", e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return classeId != null
                ? "redirect:/admin/classes/" + classeId
                : "redirect:/admin/migration";
    }

    // ═══════════════════════════════════════════════════════════
    // API REST POUR AJAX
    // ═══════════════════════════════════════════════════════════

    @GetMapping("/statistiques")
    @ResponseBody
    public MigrationResponse getStatistiques(@RequestParam(required = false) Long institutId) {
        Long institutCible = securityService.resolveInstitutId(institutId);
        Annee_academique anneeActive = anneeService.getAnneeActivePourInstitut(institutCible);
        MigrationResultat resultat = migrationService.simulerPourInstitut(institutCible, anneeActive.getId());
        return migrationMapper.toMigrationResponse(resultat, TypeMigration.COMPLETE);
    }

    @GetMapping("/etudiants-sans-decision")
    @ResponseBody
    public List<String> getEtudiantsSansDecision() {
        return migrationService.getEtudiantsSansDecision();
    }
}