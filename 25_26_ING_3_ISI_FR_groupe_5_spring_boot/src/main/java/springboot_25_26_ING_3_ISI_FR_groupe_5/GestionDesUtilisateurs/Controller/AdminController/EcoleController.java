package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

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
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ecole.EcoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EcoleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InstitutMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EcoleService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InstitutService;

@Controller
@RequestMapping("/admin/ecoles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EcoleController {

    private final EcoleService ecoleService;
    private final InstitutService institutService;
    private final EcoleMapper ecoleMapper;
    private final InstitutMapper institutMapper;

    @GetMapping
    public String liste(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long institutId,
            Model model
    ) {
        Page<Ecole> ecolesPage;

        if (institutId != null) {
            ecolesPage = ecoleService.getByInstitutPaginated(institutId, page, size);
            model.addAttribute("institutFiltre", institutService.findById(institutId));
        } else {
            ecolesPage = ecoleService.getAllPaginated(page, size, search);
        }

        model.addAttribute("ecoles", ecoleMapper.toResponseList(ecolesPage.getContent()));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ecolesPage.getTotalPages());
        model.addAttribute("totalItems", ecolesPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("institutId", institutId);
        model.addAttribute("instituts", institutMapper.toResponseList(institutService.getAll()));
        model.addAttribute("form", new EcoleRequest());

        return "ecoles/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") EcoleRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            model.addAttribute("ecoles", ecoleMapper.toResponseList(ecoleService.getAll()));
            model.addAttribute("instituts", institutMapper.toResponseList(institutService.getAll()));
            model.addAttribute("form", request);
            return "ecoles/liste";
        }

        try {
            ecoleService.creer(request, acteur);
            redirectAttributes.addFlashAttribute("succes", "École créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/ecoles";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Ecole ecole = ecoleService.findById(id);
        model.addAttribute("ecole", ecoleMapper.toResponse(ecole));
        return "ecoles/detail";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Ecole ecole = ecoleService.findById(id);
        EcoleRequest request = new EcoleRequest();
        request.setNom(ecole.getNom());
        request.setAdresse(ecole.getAdresse());
        request.setEmail(ecole.getEmail());
        request.setTelephone(ecole.getTelephone());
        request.setInstitutId(ecole.getInstitut().getId());

        model.addAttribute("ecole", ecoleMapper.toResponse(ecole));
        model.addAttribute("form", request);
        model.addAttribute("instituts", institutMapper.toResponseList(institutService.getAll()));

        return "ecoles/modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") EcoleRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            model.addAttribute("ecole", ecoleMapper.toResponse(ecoleService.findById(id)));
            model.addAttribute("instituts", institutMapper.toResponseList(institutService.getAll()));
            return "ecoles/modifier";
        }

        try {
            ecoleService.modifier(id, request, acteur);
            redirectAttributes.addFlashAttribute("succes", "École modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/ecoles";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            ecoleService.supprimer(id, acteur);
            redirectAttributes.addFlashAttribute("succes", "École supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/ecoles";
    }
}