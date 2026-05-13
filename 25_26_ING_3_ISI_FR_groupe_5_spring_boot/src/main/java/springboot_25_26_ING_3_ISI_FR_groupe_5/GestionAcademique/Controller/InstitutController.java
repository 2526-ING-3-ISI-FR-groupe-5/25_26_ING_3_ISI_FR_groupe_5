package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.institut.InstitutRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.InstitutMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutService;

@Controller
@RequestMapping("/admin/instituts")
@RequiredArgsConstructor

//@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
@PreAuthorize("hasAnyRole('ADMIN_INSTITUT','SUPER_ADMIN' )")
public class InstitutController {

    private final InstitutService institutService;
    private final InstitutMapper institutMapper;
    private final InstitutSecurityService securityService;

    // ═══════════════════════════════════════════════════════════
    // LISTE DES INSTITUTS
    // ═══════════════════════════════════════════════════════════

    @GetMapping
    public String liste(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Page<Institut> institutsPage = institutService
                .getAllPaginated(page, size, search);

        model.addAttribute("instituts",
                institutMapper.toResponseList(institutsPage.getContent()));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", institutsPage.getTotalPages());
        model.addAttribute("totalItems", institutsPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("form", new InstitutRequest());

        // ✅ Commenter temporairement
        // model.addAttribute("currentInstitutName",
        //     securityService.getCurrentInstitutName());

        return "instituts/liste";
    }

    // ═══════════════════════════════════════════════════════════
    // CRÉATION
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") InstitutRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            // 🆕 Recharger la liste en cas d'erreur
            Page<Institut> institutsPage = institutService.getAllPaginated(0, 10, null);
            model.addAttribute("instituts", institutMapper.toResponseList(institutsPage.getContent()));
            model.addAttribute("form", request);
            return "instituts/liste";
        }

        try {
            Institut institut = institutMapper.toEntity(request);
            institutService.creer(institut, acteur);
            redirectAttributes.addFlashAttribute("succes", "✅ Institut créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/admin/instituts";
    }

    // ═══════════════════════════════════════════════════════════
    // DÉTAIL
    // ═══════════════════════════════════════════════════════════

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Institut institut = institutService.findById(id);
        model.addAttribute("institut", institutMapper.toResponse(institut));
        model.addAttribute("currentInstitutName", securityService.getCurrentInstitutName());
        return "instituts/detail";
    }

    // ═══════════════════════════════════════════════════════════
    // FORMULAIRE DE MODIFICATION
    // ═══════════════════════════════════════════════════════════

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Institut institut = institutService.findById(id);
        InstitutRequest request = new InstitutRequest();
        request.setNom(institut.getNom());
        request.setVille(institut.getVille());
        request.setAdresse(institut.getAdresse());
        request.setEmail(institut.getEmail());
        request.setTelephone(institut.getTelephone());
        request.setLocalite(institut.getLocalite());

        model.addAttribute("institut", institutMapper.toResponse(institut));
        model.addAttribute("form", request);
        model.addAttribute("currentInstitutName", securityService.getCurrentInstitutName());
        return "instituts/modifier";
    }

    // ═══════════════════════════════════════════════════════════
    // TRAITEMENT DE LA MODIFICATION
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") InstitutRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            model.addAttribute("institut", institutMapper.toResponse(institutService.findById(id)));
            model.addAttribute("currentInstitutName", securityService.getCurrentInstitutName());
            return "instituts/modifier";
        }

        try {
            Institut institutModifie = institutMapper.toEntity(request);
            institutService.modifier(id, institutModifie, acteur);
            redirectAttributes.addFlashAttribute("succes", "✅ Institut modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
            return "redirect:/admin/instituts/" + id + "/modifier";
        }

        return "redirect:/admin/instituts";
    }

    // ═══════════════════════════════════════════════════════════
    // SUPPRESSION
    // ═══════════════════════════════════════════════════════════

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            institutService.supprimer(id, acteur);
            redirectAttributes.addFlashAttribute("succes", "✅ Institut supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }
        return "redirect:/admin/instituts";
    }
}