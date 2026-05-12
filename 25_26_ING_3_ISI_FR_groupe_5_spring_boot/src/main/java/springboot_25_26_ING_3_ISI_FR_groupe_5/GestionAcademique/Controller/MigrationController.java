package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.TypeMigration;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.AnneeAcademiqueMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.InterfaceService.IMigrationService.MigrationResultat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InscriptionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin/migration")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUT')")
public class MigrationController {

    private final MigrationService migrationService;
    private final InstitutSecurityService securityService;
    private final AnneeAcademiqueService anneeService;
    private final InscriptionService inscriptionService;

    private final InstitutRepository institutRepository;
    private final EtudiantRepository etudiantRepository;
    private final EnseignantRepository enseignantRepository;
    private final UERepository ueRepository;
    private final ClassesRepository classesRepository;
    private final FiliereRepository filiereRepository;
    private final NiveauService niveauService;

    private final AnneeAcademiqueMapper anneeMapper;
    private final InscriptionMapper inscriptionMapper;

    // ═══════════════════════════════════════════════════��═══════
    // UTILITAIRE DE MAPPING (remplace MapStruct pour ce cas)
    // ═══════════════════════════════════════════════════════════
    private MigrationResponse mapResultatToResponse(
            MigrationResultat resultat,
            TypeMigration type,
            Long institutId,
            String institutNom,
            boolean includeDetails
    ) {
        return MigrationResponse.builder()
                .typeMigration(type)
                .institutId(institutId)
                .institutNom(institutNom)
                .totalTraite(resultat.getTotalTraite())
                .totalAdmis(resultat.getAdmis())
                .totalRedoublants(resultat.getRedoublants())
                .totalExclus(resultat.getExclus())
                .totalDiplomes(resultat.getDiplomes())
                .totalIgnores(resultat.getIgnores())
                .admis(includeDetails ? resultat.getAdmisList() : List.of())
                .redoublants(includeDetails ? resultat.getRedoublantsList() : List.of())
                .exclus(includeDetails ? resultat.getExclusList() : List.of())
                .diplomes(includeDetails ? resultat.getDiplomesList() : List.of())
                .ignores(includeDetails ? resultat.getIgnoresList() : List.of())
                .message(resultat.toString())
                .dateMigration(LocalDateTime.now())
                .build();
    }

    // ═══════════════════════════════════════════════════════════
    // PAGE PRINCIPALE
    // ═══════════════════════════════════════════════════════════
    @GetMapping
    public String index(Model model, @RequestParam(required = false) Long institutId) {
        Long institutCible = securityService.resolveInstitutId(institutId);
        Annee_academique anneeActive = anneeService.getAnneeActivePourInstitut(institutCible);

        List<Inscription> inscriptionsPreview = inscriptionService
                .getByClasseAndAnneePaginated(null, anneeActive.getId(), PageRequest.of(0, 1000)).getContent();
        // .getByClasseAndAnneePaginated(Optional.ofNullable((Object) null), anneeActive.getId(), PageRequest.of(0, 1000)).getContent();

        long admis = inscriptionsPreview.stream().filter(i -> "ADMIS".equals(i.getDecisionFinAnnee())).count();
        long redoublants = inscriptionsPreview.stream().filter(i -> "REDOUBLANT".equals(i.getDecisionFinAnnee())).count();
        long exclus = inscriptionsPreview.stream().filter(i -> "EXCLU".equals(i.getDecisionFinAnnee())).count();
        long sansDecision = inscriptionsPreview.stream().filter(i -> i.getDecisionFinAnnee() == null).count();

        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("annees", anneeMapper.toResponseList(
                institutCible != null ? anneeService.getByInstitut(institutCible) : anneeService.getAll()));
        model.addAttribute("totalInscriptions", inscriptionsPreview.size());
        model.addAttribute("admis", admis);
        model.addAttribute("redoublants", redoublants);
        model.addAttribute("exclus", exclus);
        model.addAttribute("sansDecision", sansDecision);
        model.addAttribute("etudiantsSansDecision", migrationService.getEtudiantsSansDecision());

        if (institutCible != null) {
            model.addAttribute("etudiants", etudiantRepository.findByInstitutId(institutCible));
            model.addAttribute("enseignants", enseignantRepository.findByInstitutId(institutCible));
        } else {
            model.addAttribute("etudiants", etudiantRepository.findAll());
            model.addAttribute("enseignants", enseignantRepository.findAll());
        }
        model.addAttribute("ues", ueRepository.findAll());
        model.addAttribute("classes", classesRepository.findAll());
        model.addAttribute("filieres", filiereRepository.findAll());
        model.addAttribute("niveaux", niveauService.getAll());

        if (securityService.shouldShowInstitutSelector()) {
            model.addAttribute("instituts", institutRepository.findAll());
            model.addAttribute("selectedInstitutId", institutCible);
        }
        model.addAttribute("currentInstitutName", securityService.getCurrentInstitutName());
        model.addAttribute("form", MigrationRequest.builder().build());

        return "migration/index";
    }

    // ═══════════════════════════════════════════════════════════
    // LANCER MIGRATION COMPLÈTE
    // ═══════════════════════════════════════════════════════════
    @PostMapping("/lancer")
    public String lancer(
            @Valid @ModelAttribute("form") MigrationRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors() || request.getNouvelleAnneeId() == null) {
            redirectAttributes.addFlashAttribute("erreur", "Veuillez sélectionner une année cible valide");
            return "redirect:/admin/migration";
        }

        try {
            Long institutCible = securityService.resolveInstitutId(request.getInstitutId());
            MigrationResultat resultat = migrationService.migrerPourInstitut(
                    institutCible, request.getNouvelleAnneeId(), acteur);

            Institut institut = institutRepository.findById(institutCible).orElse(null);
            // ✅ Mapping manuel avec détails pour l'affichage
            MigrationResponse response = mapResultatToResponse(
                    resultat, request.getTypeMigration(), institutCible,
                    institut != null ? institut.getNom() : null, true);

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes", "✅ Migration complète effectuée avec succès !");
        } catch (Exception e) {
            log.error("Erreur migration complète", e);
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        String redirectUrl = "/admin/migration";
        if (request.getInstitutId() != null) redirectUrl += "?institutId=" + request.getInstitutId();
        return "redirect:" + redirectUrl;
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATIONS SÉLECTIVES (pattern unifié)
    // ═══════════════════════════════════════════════════════════
    @FunctionalInterface
    private interface MigrationAction { MigrationResultat execute() throws Exception; }

    private String executeSelectiveMigration(MigrationAction action, TypeMigration type, String entity, RedirectAttributes ra) {
        try {
            MigrationResultat res = action.execute();
            // ✅ Mapping manuel (sans détails pour garder la réponse légère)
            MigrationResponse response = mapResultatToResponse(res, type, null, null, false);
            ra.addFlashAttribute("resultat", response);
            ra.addFlashAttribute("succes", "✅ " + entity + " migré(e) avec succès !");
        } catch (Exception e) {
            log.error("Erreur migration {}", entity, e);
            ra.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }
        return "redirect:/admin/migration";
    }

    @PostMapping("/etudiant/{etudiantId}")
    public String migrerEtudiant(@PathVariable Long etudiantId, @RequestParam Long nouvelleAnneeId, RedirectAttributes ra, @AuthenticationPrincipal Utilisateur acteur) {
        return executeSelectiveMigration(() -> migrationService.migrerEtudiant(etudiantId, nouvelleAnneeId, acteur), TypeMigration.ETUDIANT, "Étudiant", ra);
    }

    @PostMapping("/enseignant/{enseignantId}")
    public String migrerEnseignant(@PathVariable Long enseignantId, @RequestParam Long nouvelleAnneeId, RedirectAttributes ra, @AuthenticationPrincipal Utilisateur acteur) {
        return executeSelectiveMigration(() -> migrationService.migrerEnseignant(enseignantId, nouvelleAnneeId, acteur), TypeMigration.ENSEIGNANT, "Enseignant", ra);
    }

    @PostMapping("/ue/{ueId}")
    public String migrerUE(@PathVariable Long ueId, @RequestParam Long nouvelleAnneeId, RedirectAttributes ra, @AuthenticationPrincipal Utilisateur acteur) {
        return executeSelectiveMigration(() -> migrationService.migrerUE(ueId, nouvelleAnneeId, acteur), TypeMigration.UE, "UE", ra);
    }

    @PostMapping("/classe/{classeId}")
    public String migrerClasse(@PathVariable Long classeId, @RequestParam Long nouvelleAnneeId, RedirectAttributes ra, @AuthenticationPrincipal Utilisateur acteur) {
        return executeSelectiveMigration(() -> migrationService.migrerClasse(classeId, nouvelleAnneeId, acteur), TypeMigration.CLASSE, "Classe", ra);
    }

    @PostMapping("/filiere/{filiereId}")
    public String migrerFiliere(@PathVariable Long filiereId, @RequestParam Long nouvelleAnneeId, RedirectAttributes ra, @AuthenticationPrincipal Utilisateur acteur) {
        return executeSelectiveMigration(() -> migrationService.migrerFiliere(filiereId, nouvelleAnneeId, acteur), TypeMigration.FILIERE, "Filière", ra);
    }

    @PostMapping("/niveau/{niveauId}")
    public String migrerNiveau(@PathVariable Long niveauId, @RequestParam Long nouvelleAnneeId, RedirectAttributes ra, @AuthenticationPrincipal Utilisateur acteur) {
        return executeSelectiveMigration(() -> migrationService.migrerNiveau(niveauId, nouvelleAnneeId, acteur), TypeMigration.NIVEAU, "Niveau", ra);
    }

    // ═══════════════════════════════════════════════════════════
    // API REST (AJAX)
    // ═══════════════════════════════════════════════════════════
    @GetMapping("/statistiques")
    @ResponseBody
    public MigrationResponse getStatistiques(@RequestParam(required = false) Long institutId) {
        Long institutCible = securityService.resolveInstitutId(institutId);
        Annee_academique anneeActive = anneeService.getAnneeActivePourInstitut(institutCible);
        MigrationResultat res = migrationService.simulerPourInstitut(institutCible, anneeActive.getId());
        return mapResultatToResponse(res, TypeMigration.SIMULATION, institutCible, null, false);
    }

    @GetMapping("/etudiants-sans-decision")
    @ResponseBody
    public List<String> getEtudiantsSansDecision() {
        return migrationService.getEtudiantsSansDecision();
    }
}