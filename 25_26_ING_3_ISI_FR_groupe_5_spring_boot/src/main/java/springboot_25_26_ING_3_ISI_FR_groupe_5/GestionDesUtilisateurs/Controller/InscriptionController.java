package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.InstitutContexteActif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.InstitutContexteActifRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.InstitutRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription.DecisionHistory;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InscriptionService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/inscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUT')")
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final AnneeAcademiqueService anneeService;
    private final ClassesService classesService;
    private final InstitutSecurityService securityService;
    private final InstitutContexteActifRepository contexteRepo;
    private final InstitutRepository institutRepository;

    @GetMapping
    public String listInscriptions(
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Long anneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "etudiant.nom") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        // 1. Institut courant
        Long institutCible = securityService.resolveInstitutId(utilisateur, null);

        // 2. Résolution de l'année active : InstitutContexteActif > fallback anneeService.getAnneeActive()
        Annee_academique anneeActive = null;
        Long anneeActiveId = null;
        if (institutCible != null) {
            anneeActive = contexteRepo.findByInstitutId(institutCible)
                    .map(InstitutContexteActif::getAnneeAcademique)
                    .orElse(null);
            // Fallback : si InstitutContexteActif pas encore configuré
            if (anneeActive == null) {
                try { anneeActive = anneeService.getAnneeActive(); }
                catch (Exception e) { log.warn("Aucune année active trouvée pour l'institut {}: {}", institutCible, e.getMessage()); }
            }
            anneeActiveId = anneeActive != null ? anneeActive.getId() : null;
        }

        // 3. Année à utiliser pour le filtre : param URL > année active
        Long anneeIdParam = (anneeId != null) ? anneeId : anneeActiveId;

        // 4. Pagination & Tri
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 5. Chargement des inscriptions — page vide si aucune année disponible
        Page<Inscription> inscriptionsPage = Page.empty(pageable);
        if (anneeIdParam != null) {
            try {
                inscriptionsPage = inscriptionService.getByClasseAndAnneePaginated(classeId, anneeIdParam, pageable);
            } catch (Exception e) {
                log.error("Erreur chargement inscriptions: {}", e.getMessage());
                model.addAttribute("error", "Erreur lors du chargement des inscriptions : " + e.getMessage());
            }
        }

        // 6. Données pour les filtres
        List<Annee_academique> annees = (institutCible != null)
                ? anneeService.getByInstitut(institutCible)
                : anneeService.getAll();
        List<Classe> classes = (institutCible != null)
                ? classesService.getByInstitut(institutCible)
                : classesService.getAll();
        List<Institut> instituts = institutRepository.findAll(); // Bug 3 — manquait

        // 7. Modèle
        model.addAttribute("inscriptionsPage", inscriptionsPage);
        model.addAttribute("inscriptions", inscriptionsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("filters", new InscriptionFilters(classeId, anneeIdParam));
        model.addAttribute("annees", annees);
        model.addAttribute("classes", classes);
        model.addAttribute("instituts", instituts);
        model.addAttribute("selectedInstitutId", institutCible);
        model.addAttribute("anneeActiveId", anneeActiveId);
        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("PAGE_SIZE_OPTIONS", new int[]{10, 20, 50, 100});

        return "inscription/list";
    }

    @GetMapping("/{id}")
    public String detailInscription(@PathVariable Long id, Model model) {
        Inscription inscription = inscriptionService.findById(id);
        List<DecisionHistory> historique = inscriptionService.getDecisionHistory(id);

        // ✅ ID année active pour remplacer .active
        Long anneeActiveId = null;
        if (inscription.getClasse() != null && inscription.getClasse().getInstitutId() != null) {
            anneeActiveId = contexteRepo.findByInstitutId(inscription.getClasse().getInstitutId())
                    .map(InstitutContexteActif::getAnneeAcademique)
                    .map(Annee_academique::getId)
                    .orElse(null);
        }

        model.addAttribute("inscription", inscription);
        model.addAttribute("historiqueDecisions", historique);
        model.addAttribute("anneeActiveId", anneeActiveId);
        return "inscription/detail";
    }

    @PostMapping("/{id}/decision")
    public String enregistrerDecision(
            @PathVariable Long id,
            @RequestParam String decision,
            @RequestParam(required = false) String observations,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        inscriptionService.enregistrerDecision(id, DecisionFinAnnee.valueOf(decision), observations, utilisateur);
        return "redirect:/admin/inscriptions/" + id;
    }

    public record InscriptionFilters(Long classeId, Long anneeId) {}
}