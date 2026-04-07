package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.surveillant.SurveillantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.SurveillantMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.SurveillantService;

@Controller
@RequestMapping("/surveillants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SurveillantController {

    private final SurveillantService surveillantService;
    private final SurveillantMapper surveillantMapper;

    @GetMapping
    public String liste(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<Surveillant> surveillants = surveillantService.getAll(
                PageRequest.of(page, size)
        );

        model.addAttribute("surveillants",
                surveillantMapper.toResponseList(surveillants.getContent()));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", surveillants.getTotalPages());
        model.addAttribute("form", new SurveillantRequest());
        return "surveillants/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireCreer(Model model) {
        model.addAttribute("form", new SurveillantRequest());
        return "surveillants/form";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") SurveillantRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "surveillants/form";
        }

        try {
            Surveillant surveillant = surveillantMapper.toEntity(request);
            surveillantService.creer(surveillant);
            redirectAttributes.addFlashAttribute("succes",
                    "Surveillant créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/surveillants";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        Surveillant surveillant = surveillantService.findById(id);
        SurveillantRequest form = new SurveillantRequest();
        form.setNom(surveillant.getNom());
        form.setPrenom(surveillant.getPrenom());
        form.setEmail(surveillant.getEmail());
        form.setTelephone(surveillant.getTelephone());
        form.setSecteur(surveillant.getSecteur());
        form.setTypeContrat(String.valueOf(surveillant.getTypeContrat()));

        model.addAttribute("surveillant",
                surveillantMapper.toResponse(surveillant));
        model.addAttribute("form", form);
        return "surveillants/modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") SurveillantRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("surveillant",
                    surveillantMapper.toResponse(surveillantService.findById(id)));
            return "surveillants/modifier";
        }

        try {
            Surveillant data = surveillantMapper.toEntity(request);
            surveillantService.modifier(id, data);
            redirectAttributes.addFlashAttribute("succes",
                    "Surveillant modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/surveillants";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            surveillantService.toggleActif(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Statut modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/surveillants";
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            surveillantService.reinitialiserMotDePasse(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Mot de passe réinitialisé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/surveillants";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            surveillantService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Surveillant supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/surveillants";
    }
}


