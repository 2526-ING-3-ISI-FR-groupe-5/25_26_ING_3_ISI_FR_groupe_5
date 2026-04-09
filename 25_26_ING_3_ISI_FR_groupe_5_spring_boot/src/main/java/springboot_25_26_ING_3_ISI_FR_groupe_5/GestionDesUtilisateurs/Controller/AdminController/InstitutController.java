package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.institut.InstitutRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.institut.InstitutResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InstitutMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InstitutService;

@Controller
@RequestMapping("/admin/instituts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InstitutController {

    private final InstitutService institutService;
    private final InstitutMapper institutMapper;

    @GetMapping
    public String liste(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Page<Institut> institutsPage = institutService.getAllPaginated(page, size, search);

        model.addAttribute("instituts", institutMapper.toResponseList(institutsPage.getContent()));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", institutsPage.getTotalPages());
        model.addAttribute("totalItems", institutsPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("form", new InstitutRequest());

        return "instituts/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") InstitutRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("instituts", institutMapper.toResponseList(institutService.getAll()));
            model.addAttribute("form", request);
            return "instituts/liste";
        }

        try {
            Institut institut = institutMapper.toEntity(request);
            institutService.creer(institut);
            redirectAttributes.addFlashAttribute("succes", "Institut créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/instituts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Institut institut = institutService.findById(id);
        model.addAttribute("institut", institutMapper.toResponse(institut));
        return "instituts/detail";
    }

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
        return "instituts/modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") InstitutRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("institut", institutMapper.toResponse(institutService.findById(id)));
            return "instituts/modifier";
        }

        try {
            Institut institutModifie = institutMapper.toEntity(request);
            institutService.modifier(id, institutModifie);
            redirectAttributes.addFlashAttribute("succes", "Institut modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/instituts";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            institutService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Institut supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/instituts";
    }
}