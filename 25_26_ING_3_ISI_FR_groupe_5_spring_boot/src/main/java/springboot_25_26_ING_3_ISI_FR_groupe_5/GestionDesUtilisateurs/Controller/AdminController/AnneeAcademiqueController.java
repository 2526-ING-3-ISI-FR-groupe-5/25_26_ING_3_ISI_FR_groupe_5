package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.annee.AnneeRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre.SemestreRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.AnneeAcademiqueMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.SemestreMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InstitutRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.SemestreService;

import java.util.List;

@Controller
@RequestMapping("/admin/annees")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole( 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
public class AnneeAcademiqueController {
    private final AnneeAcademiqueService anneeService;
    private final AnneeAcademiqueMapper anneeMapper;
    private final SemestreService semestreService;
    private final SemestreMapper semestreMapper;
    private final InstitutSecurityService securityService;
    private final InstitutRepository institutRepository;

    @GetMapping
    public String liste(Model model, @RequestParam(required = false) Long institutId) {
        Long institutCible = null;

        try {
            institutCible = securityService.resolveInstitutId(institutId);
        } catch (Exception e) {
            // Super Admin sans institut sélectionné : on laisse passer
            institutCible = null;
        }

        List<Annee_academique> annees;
        Annee_academique anneeActive = null;

        if (institutCible != null) {
            annees = anneeService.getByInstitut(institutCible);
            try {
                anneeActive = anneeService.getAnneeActivePourInstitut(institutCible);
            } catch (Exception e) {
                // Pas d'année active
            }
        } else {
            // Super Admin : voir toutes les années
            annees = anneeService.getAll();
        }

        model.addAttribute("annees", anneeMapper.toResponseList(annees));
        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("selectedInstitutId", institutCible);
        model.addAttribute("form", new AnneeRequest());
        model.addAttribute("currentInstitutName", securityService.getCurrentInstitutName());

        if (securityService.shouldShowInstitutSelector()) {
            model.addAttribute("instituts", institutRepository.findAll());
        }

        return "annee/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") AnneeRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            model.addAttribute("annees", anneeMapper.toResponseList(anneeService.getAll()));
            try {
                model.addAttribute("anneeActive", anneeService.getAnneeActive());
            } catch (Exception e) {
                model.addAttribute("anneeActive", null);
            }
            return "annee/liste";
        }

        try {
            // ✅ Utiliser creerAvecInstitut() avec l'institutId du formulaire
            anneeService.creerAvecInstitut(
                    request.getNom(),
                    request.getDateDebut(),
                    request.getDateFin(),
                    request.isActive(),
                    request.getInstitutId(),  // 🆕
                    acteur
            );
            redirectAttributes.addFlashAttribute("succes", "✅ Année créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/annees?institutId=" + request.getInstitutId();
    }

    @PostMapping("/{id}/activer")
    public String activer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            Annee_academique annee = anneeService.findById(id);
            anneeService.activer(id, acteur);
            redirectAttributes.addFlashAttribute("succes",
                    "✅ Année " + annee.getNom() + " activée pour " + annee.getInstitut().getNom());
            return "redirect:/admin/annees?institutId=" + annee.getInstitut().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/admin/annees";
        }
    }


    @PostMapping("/{id}/desactiver")
    public String desactiver(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            Annee_academique annee = anneeService.findById(id);
            anneeService.desactiver(id, acteur);
            redirectAttributes.addFlashAttribute("succes",
                    "✅ Année " + annee.getNom() + " désactivée pour " + annee.getInstitut().getNom());
            return "redirect:/admin/annees?institutId=" + annee.getInstitut().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/admin/annees";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Annee_academique annee = anneeService.findById(id);
        model.addAttribute("annee", anneeMapper.toResponse(annee));
        model.addAttribute("semestres", semestreMapper.toResponseList(semestreService.getByAnnee(id)));
        model.addAttribute("semestreForm", new SemestreRequest());
        try {
            model.addAttribute("anneeActive", anneeService.getAnneeActive());
        } catch (Exception e) {
            model.addAttribute("anneeActive", null);
        }
        return "annee/detail";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            anneeService.supprimer(id, acteur);
            redirectAttributes.addFlashAttribute("succes", "Année supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/annees";
    }
}