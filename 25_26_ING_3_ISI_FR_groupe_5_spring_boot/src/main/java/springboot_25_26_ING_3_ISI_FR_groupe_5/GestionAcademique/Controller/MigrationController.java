package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ✅ Import correct — DTO.Migration, pas Entity
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationResultat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationResponse;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.TypeMigration;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.AnneeAcademiqueMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.AssistantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InscriptionService;

import java.time.LocalDateTime;
import java.util.List;

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
    private final NiveauService niveauService;
    private final AnneeAcademiqueMapper anneeMapper;

    private final InstitutRepository institutRepository;
    private final EcoleRepository ecoleRepository;
    private final FiliereRepository filiereRepository;
    private final SpecialiteRepository specialiteRepository;
    private final ClassesRepository classesRepository;
    private final UERepository ueRepository;
    private final EtudiantRepository etudiantRepository;
    private final EnseignantRepository enseignantRepository;
    private final AssistantRepository assistantRepository;
    private final MigrationBatchRepository batchRepository;

/*    private TypeMigration typeMigrationFromString(String type) {
        return switch (type) {
            case "institut"   -> TypeMigration.INSTITUT;
            case "ecole"      -> TypeMigration.ECOLE;
            case "filiere"    -> TypeMigration.FILIERE;
            case "specialite" -> TypeMigration.SPECIALITE;
            case "niveau"     -> TypeMigration.NIVEAU;
            case "classe"     -> TypeMigration.CLASSE;
            case "etudiant"   -> TypeMigration.ETUDIANT;
            case "enseignant" -> TypeMigration.ENSEIGNANT;
            case "assistant"  -> TypeMigration.ASSISTANT;
            case "ue"         -> TypeMigration.UE;
            default           -> TypeMigration.SELECTIVE;
        };
    }*/


    // ═══════════════════════════════════════════════════════════
    // UTILITAIRE — MigrationResultat → MigrationResponse
    // ═══════════════════════════════════════════════════════════

    private MigrationResponse mapResultatToResponse(
            MigrationResultat resultat,
            TypeMigration type,
            Long institutId,
            String institutNom,
            boolean includeDetails) {

        return MigrationResponse.builder()
                .typeMigration(type)
                .institutId(institutId)
                .institutNom(institutNom)
                .totalTraite(resultat.getTotalMigre())
                .totalAdmis(resultat.getAdmis())
                .totalRedoublants(resultat.getRedoublants())
                .totalExclus(resultat.getExclus())
                .totalDiplomes(resultat.getDiplomes())
                .totalIgnores(resultat.getIgnores())
                // ✅ CORRIGÉ — utilise getXxxList() au lieu de getXxx()
                .admis(includeDetails ? resultat.getAdmisList()        : List.of())
                .redoublants(includeDetails ? resultat.getRedoublantsList() : List.of())
                .exclus(includeDetails ? resultat.getExclusList()       : List.of())
                .diplomes(includeDetails ? resultat.getDiplomesList()   : List.of())
                .ignores(includeDetails ? resultat.getIgnoresList()     : List.of())
                .message(resultat.resume())
                .dateMigration(LocalDateTime.now())
                .build();
    }

    // ═══════════════════════════════════════════════════════════
    // TEMPLATE 1 — DASHBOARD PRINCIPAL
    // GET /admin/migration
    // ═══════════════════════════════════════════════════════════

    @GetMapping
    public String index(
            Model model,
            @RequestParam(required = false) Long institutId,
            @AuthenticationPrincipal Utilisateur acteur) {

        Long institutCible = securityService.resolveInstitutId(acteur, institutId);
        Annee_academique anneeActive = anneeService.getAnneeActivePourInstitut(institutCible);

        List<Inscription> preview = anneeActive == null ? List.of()
                : inscriptionService.getByClasseAndAnneePaginated(
                null, anneeActive.getId(), PageRequest.of(0, 1000)).getContent();

        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("annees", anneeMapper.toResponseList(
                institutCible != null
                        ? anneeService.getByInstitut(institutCible)
                        : anneeService.getAll()));

        model.addAttribute("totalInscriptions", preview.size());
        model.addAttribute("admis",        preview.stream().filter(i -> i.getDecisionFinAnnee() != null && i.getDecisionFinAnnee().name().equals("ADMIS")).count());
        model.addAttribute("redoublants",  preview.stream().filter(i -> i.getDecisionFinAnnee() != null && i.getDecisionFinAnnee().name().equals("REDOUBLANT")).count());
        model.addAttribute("exclus",       preview.stream().filter(i -> i.getDecisionFinAnnee() != null && i.getDecisionFinAnnee().name().equals("EXCLU")).count());
        model.addAttribute("sansDecision", preview.stream().filter(i -> i.getDecisionFinAnnee() == null).count());
        model.addAttribute("etudiantsSansDecision",
                anneeActive == null ? List.of() : migrationService.getEtudiantsSansDecision());

        model.addAttribute("batchsRecents",
                institutCible != null
                        ? batchRepository.findByInstitutIdOrderByDateExecutionDesc(
                        institutCible, PageRequest.of(0, 5))
                        : List.of());

        if (securityService.shouldShowInstitutSelector()) {
            model.addAttribute("instituts", institutRepository.findAll());
            model.addAttribute("selectedInstitutId", institutCible);
        }
        model.addAttribute("currentInstitutName", securityService.getCurrentInstitutName());
        model.addAttribute("form", MigrationRequest.builder().build());

        return "migration/index";
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION COMPLÈTE
    // POST /admin/migration/lancer
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/lancer")
    public String lancer(
            @ModelAttribute("form") MigrationRequest request,
            RedirectAttributes ra,
            @AuthenticationPrincipal Utilisateur acteur) {

        if (request.getNouvelleAnneeId() == null) {
            ra.addFlashAttribute("erreur", "Veuillez sélectionner une année cible valide");
            return "redirect:/admin/migration";
        }

        try {
            Long institutCible = securityService.resolveInstitutId(acteur, request.getInstitutId());
            MigrationResultat resultat = migrationService.migrerPourInstitut(
                    institutCible, request.getNouvelleAnneeId(), acteur);

            Institut institut = institutCible != null
                    ? institutRepository.findById(institutCible).orElse(null) : null;

            ra.addFlashAttribute("resultat", mapResultatToResponse(
                    resultat, TypeMigration.COMPLETE, institutCible,
                    institut != null ? institut.getNom() : null, true));
            ra.addFlashAttribute("succes", "Migration complète effectuée avec succès !");

        } catch (Exception e) {
            log.error("Erreur migration complète", e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/migration"
                + (request.getInstitutId() != null ? "?institutId=" + request.getInstitutId() : "");
    }

    // ═══════════════════════════════════════════════════════════
    // TEMPLATE 2 — PAGE SÉLECTION MULTI-CHOIX
    // GET /admin/migration/selectif/{type}
    // ═══════════════════════════════════════════════════════════

    @GetMapping("/selectif/{type}")
    public String afficherSelectif(
            @PathVariable String type,
            @RequestParam(required = false) Long institutId,
            Model model,
            RedirectAttributes ra,          // ✅ CORRIGÉ — ra déclaré
            @AuthenticationPrincipal Utilisateur acteur) {

        Long institutCible = securityService.resolveInstitutId(acteur, institutId);

        switch (type) {
            case "institut" -> {
                model.addAttribute("titre", "Migrer des Instituts");
                model.addAttribute("items", institutRepository.findAll());
                model.addAttribute("labelChamp", "Nom de l'institut");
            }
            case "ecole" -> {
                model.addAttribute("titre", "Migrer des Écoles");
                model.addAttribute("items", institutCible != null
                        ? ecoleRepository.findByInstitut_Id(institutCible)
                        : ecoleRepository.findAll());
                model.addAttribute("labelChamp", "Nom de l'école");
            }
            case "filiere" -> {
                model.addAttribute("titre", "Migrer des Filières");
                // ✅ CORRIGÉ — findByInstitutId ajouté dans FiliereRepository
                model.addAttribute("items", institutCible != null
                        ? filiereRepository.findByInstitutId(institutCible)
                        : filiereRepository.findAll());
                model.addAttribute("labelChamp", "Nom de la filière");
            }
            case "specialite" -> {
                model.addAttribute("titre", "Migrer des Spécialités");
                model.addAttribute("items", institutCible != null
                        ? specialiteRepository.findByInstitutId(institutCible)
                        : specialiteRepository.findAll());
                model.addAttribute("labelChamp", "Nom de la spécialité");
            }
            case "niveau" -> {
                model.addAttribute("titre", "Migrer des Niveaux");
                // ✅ CORRIGÉ — utilise getAll() ou getByFiliere() selon contexte
                model.addAttribute("items", niveauService.getAll());
                model.addAttribute("labelChamp", "Nom du niveau");
            }
            case "classe" -> {
                model.addAttribute("titre", "Migrer des Classes");
                model.addAttribute("items", institutCible != null
                        ? classesRepository.findByInstitutId(institutCible)
                        : classesRepository.findAll());
                model.addAttribute("labelChamp", "Nom de la classe");
            }
            case "etudiant" -> {
                model.addAttribute("titre", "Migrer des Étudiants");
                model.addAttribute("items", institutCible != null
                        ? etudiantRepository.findByInstitutId(institutCible)
                        : etudiantRepository.findAll());
                model.addAttribute("labelChamp", "Nom / Matricule");
            }
            case "enseignant" -> {
                model.addAttribute("titre", "Migrer des Enseignants");
                model.addAttribute("items", institutCible != null
                        ? enseignantRepository.findByInstitutId(institutCible)
                        : enseignantRepository.findAll());
                model.addAttribute("labelChamp", "Nom de l'enseignant");
            }
            case "assistant" -> {
                model.addAttribute("titre", "Migrer des Assistants Pédagogiques");
                model.addAttribute("items", institutCible != null
                        ? assistantRepository.findByInstitutId(institutCible)
                        : assistantRepository.findAll());
                model.addAttribute("labelChamp", "Nom de l'assistant");
            }
            case "ue" -> {
                model.addAttribute("titre", "Migrer des UE");
                model.addAttribute("items", ueRepository.findAll());
                model.addAttribute("labelChamp", "Code / Nom de l'UE");
            }
            default -> {
                ra.addFlashAttribute("erreur", "Type de migration inconnu : " + type);
                return "redirect:/admin/migration";
            }
        }

        model.addAttribute("type", type);
        model.addAttribute("annees", anneeMapper.toResponseList(
                institutCible != null
                        ? anneeService.getByInstitut(institutCible)
                        : anneeService.getAll()));
        model.addAttribute("selectedInstitutId", institutCible);

        return "migration/selectif-"+type;
    }

    // ═══════════════════════════════════════════════════════════
    // EXÉCUTION MIGRATION SÉLECTIVE
    // POST /admin/migration/selectif/{type}
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/selectif/{type}")
    public String executerSelectif(
            @PathVariable String type,
            @RequestParam List<Long> ids,
            @RequestParam Long nouvelleAnneeId,
            @RequestParam(required = false) Long institutId,
            RedirectAttributes ra,
            @AuthenticationPrincipal Utilisateur acteur) {

        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("erreur", "Veuillez sélectionner au moins un élément");
            return "redirect:/admin/migration/selectif/" + type;
        }

        try {
            MigrationResultat resultat = switch (type) {
                case "institut"   -> migrationService.migrerInstituts(ids, nouvelleAnneeId, acteur);
                case "ecole"      -> migrationService.migrerEcoles(ids, nouvelleAnneeId, acteur);
                case "filiere"    -> migrationService.migrerFilieres(ids, nouvelleAnneeId, acteur);
                case "specialite" -> migrationService.migrerSpecialites(ids, nouvelleAnneeId, acteur);
                case "niveau"     -> migrationService.migrerNiveaux(ids, nouvelleAnneeId, acteur);
                case "classe"     -> migrationService.migrerClasses(ids, nouvelleAnneeId, acteur);
                case "etudiant"   -> migrationService.migrerEtudiants(ids, nouvelleAnneeId, acteur);
                case "enseignant" -> migrationService.migrerEnseignants(ids, nouvelleAnneeId, acteur);
                case "assistant"  -> migrationService.migrerAssistants(ids, nouvelleAnneeId, acteur);
                case "ue"         -> migrationService.migrerUEs(ids, nouvelleAnneeId, acteur);
                default -> throw new IllegalArgumentException("Type inconnu : " + type);
            };

            // ✅ Utilise le type spécifique
            ra.addFlashAttribute("resultat",
                    mapResultatToResponse(resultat, typeMigrationFromString(type), institutId, null, true));
            ra.addFlashAttribute("succes", "Migration " + type + " effectuée avec succès !");

        } catch (Exception e) {
            log.error("Erreur migration sélective type={}", type, e);
            ra.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/admin/migration/selectif/" + type;
        }

        return "redirect:/admin/migration"
                + (institutId != null ? "?institutId=" + institutId : "");
    }

    // ✅ Nouvelle méthode
    private TypeMigration typeMigrationFromString(String type) {
        return switch (type) {
            case "institut"   -> TypeMigration.INSTITUT;
            case "ecole"      -> TypeMigration.ECOLE;
            case "filiere"    -> TypeMigration.FILIERE;
            case "specialite" -> TypeMigration.SPECIALITE;
            case "niveau"     -> TypeMigration.NIVEAU;
            case "classe"     -> TypeMigration.CLASSE;
            case "etudiant"   -> TypeMigration.ETUDIANT;
            case "enseignant" -> TypeMigration.ENSEIGNANT;
            case "assistant"  -> TypeMigration.ASSISTANT;
            case "ue"         -> TypeMigration.UE;
            default           -> TypeMigration.SELECTIVE;
        };
    }

    // ═══════════════════════════════════════════════════════════
    // TEMPLATE 3 — RÉSULTAT / DÉTAIL D'UN BATCH
    // GET /admin/migration/resultat/{batchId}
    // ═══════════════════════════════════════════════════════════

    @GetMapping("/resultat/{batchId}")
    public String afficherResultat(
            @PathVariable Long batchId,
            Model model) {

        model.addAttribute("batch",
                batchRepository.findByIdWithDetails(batchId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Batch introuvable : " + batchId)));
        return "migration/resultat";
    }

    // ═══════════════════════════════════════════════════════════
    // PUBLIER / ROLLBACK
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/publier/{batchId}")
    public String publier(
            @PathVariable Long batchId,
            RedirectAttributes ra,
            @AuthenticationPrincipal Utilisateur acteur) {
        try {
            migrationService.publierMigration(batchId, acteur);
            ra.addFlashAttribute("succes", "Migration publiée définitivement.");
        } catch (Exception e) {
            log.error("Erreur publication batch {}", batchId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/migration/resultat/" + batchId;
    }

    @PostMapping("/rollback/{batchId}")
    public String rollback(
            @PathVariable Long batchId,
            RedirectAttributes ra,
            @AuthenticationPrincipal Utilisateur acteur) {
        try {
            migrationService.rollbackMigration(batchId, acteur);
            ra.addFlashAttribute("succes", "Migration annulée avec succès.");
        } catch (Exception e) {
            log.error("Erreur rollback batch {}", batchId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/migration";
    }

    // ═══════════════════════════════════════════════════════════
    // API REST (AJAX)
    // ═══════════════════════════════════════════════════════════

    @GetMapping("/statistiques")
    @ResponseBody
    public MigrationResponse getStatistiques(
            @RequestParam Long nouvelleAnneeId,
            @RequestParam(required = false) Long institutId,
            @AuthenticationPrincipal Utilisateur acteur) {

        Long institutCible = securityService.resolveInstitutId(acteur, institutId);
        MigrationResultat res = migrationService.simulerPourInstitut(institutCible, nouvelleAnneeId);
        return mapResultatToResponse(res, TypeMigration.SIMULATION, institutCible, null, false);
    }

    @GetMapping("/etudiants-sans-decision")
    @ResponseBody
    public List<String> getEtudiantsSansDecision() {
        return migrationService.getEtudiantsSansDecision();
    }
}