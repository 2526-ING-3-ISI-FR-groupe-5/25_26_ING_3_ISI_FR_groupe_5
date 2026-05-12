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

// @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN_INSTITUT')")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUT')")
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final AnneeAcademiqueService anneeService;
    private final ClassesService classesService;
    private final InstitutSecurityService securityService;

    // ─────────────────────────────────────────────────────────────
    // CONFIG PAGINATION
    // ─────────────────────────────────────────────────────────────
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int[] PAGE_SIZE_OPTIONS = {10, 20, 50, 100};

    // ─────────────────────────────────────────────────────────────
    // LISTE DES INSCRIPTIONS AVEC PAGINATION + FILTRES
    // ─────────────────────────────────────────────────────────────

// Dans InscriptionController.listInscriptions()

    @GetMapping
    public String listInscriptions(
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Long anneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "etudiant.nom") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model,
            @AuthenticationPrincipal Utilisateur utilisateur
    ) {
        // 1. Résolution contexte
        Long institutCible = securityService.resolveInstitutId(utilisateur, null);

        // 2. Année par défaut
        if (anneeId == null) {
            Annee_academique active = anneeService.getAnneeActivePourInstitut(institutCible);
            anneeId = (active != null) ? active.getId() : null;
        }

        // 3. Pageable
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 4. Données
        Page<Inscription> inscriptionsPage = inscriptionService.getByClasseAndAnneePaginated(classeId, anneeId, pageable);

        // 5. Listes pour filtres
        List<Annee_academique> annees = (institutCible != null)
                ? anneeService.getByInstitut(institutCible)
                : anneeService.getAll();

        List<Classe> classes = (institutCible != null)
                ? classesService.getByInstitut(institutCible)
                : classesService.getAll();

        // 6. Modèle
        model.addAttribute("inscriptionsPage", inscriptionsPage);
        model.addAttribute("inscriptions", inscriptionsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("filters", new InscriptionController.InscriptionFilters(classeId, anneeId));
        model.addAttribute("annees", annees);
        model.addAttribute("classes", classes);
        model.addAttribute("selectedInstitutId", institutCible);
        model.addAttribute("PAGE_SIZE_OPTIONS", new int[]{10, 20, 50, 100}); // Constante pour le template

        return "inscription/list";
    }

    // ─────────────────────────────────────────────────────────────
    // RECORD POUR LES FILTRES (pratique pour Thymeleaf)
    // ─────────────────────────────────────────────────────────────

    public record InscriptionFilters(Long classeId, Long anneeId) {}

    // ─────────────────────────────────────────────────────────────
    // ACTION : CHANGER LA TAILLE DE PAGE (redirection propre)
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/change-page-size")
    public String changePageSize(
            @RequestParam int newSize,
            @ModelAttribute("filters") InscriptionFilters filters,
            @RequestParam(defaultValue = "etudiant.nom") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        // Redirige vers la liste avec les nouveaux paramètres
        return "redirect:/admin/inscriptions?" +
                buildQueryString(0, newSize, filters.classeId(), filters.anneeId(), sortBy, sortDir);
    }

    // ─────────────────────────────────────────────────────────────
    // UTILITAIRE : Construction de query string pour les liens
    // ─────────────────────────────────────────────────────────────

    private String buildQueryString(int page, int size, Long classeId, Long anneeId, String sortBy, String sortDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("page=").append(page);
        sb.append("&size=").append(size);
        sb.append("&sortBy=").append(sortBy);
        sb.append("&sortDir=").append(sortDir);
        if (classeId != null) sb.append("&classeId=").append(classeId);
        if (anneeId != null) sb.append("&anneeId=").append(anneeId);
        return sb.toString();
    }
    @GetMapping("/{id}")
    public String detailInscription(@PathVariable Long id, Model model, @AuthenticationPrincipal Utilisateur utilisateur) {
        Inscription inscription = inscriptionService.findById(id); // Vérifie déjà les droits d'accès

        // Récupère l'historique depuis ton journal d'actions ou une table dédiée
        List<DecisionHistory> historique = inscriptionService.getDecisionHistory(id);

        model.addAttribute("inscription", inscription);
        model.addAttribute("historiqueDecisions", historique);

        return "inscription/detail";
    }

}