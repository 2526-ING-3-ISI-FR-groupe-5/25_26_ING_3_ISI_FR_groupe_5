package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ecole.EcoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EcoleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EcoleService;

@Controller
@RequestMapping("/ecoles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EcoleController {

    private final EcoleService ecoleService;
    private final EcoleMapper ecoleMapper;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("ecoles",
                ecoleMapper.toResponseList(ecoleService.getAll()));
        model.addAttribute("nouvelleEcole",
                new EcoleRequest());
        return "ecoles/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("ecole",
                ecoleMapper.toResponse(ecoleService.findById(id)));
        return "ecoles/detail";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        Ecole ecole = ecoleService.findById(id);
        EcoleRequest request = new EcoleRequest();
        request.setNom(ecole.getNom());
        request.setAdresse(ecole.getAdresse());
        request.setEmail(ecole.getEmail());
        request.setTelephone(ecole.getTelephone());
        model.addAttribute("ecole", ecoleMapper.toResponse(ecole));
        model.addAttribute("form", request);
        return "ecoles/modifier";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("nouvelleEcole") EcoleRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("ecoles",
                    ecoleMapper.toResponseList(ecoleService.getAll()));
            return "ecoles/liste";
        }

        try {
            Ecole ecole = ecoleMapper.toEntity(request);
            ecoleService.creer(ecole);
            redirectAttributes.addFlashAttribute("succes",
                    "École créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/ecoles";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") EcoleRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("ecole",
                    ecoleMapper.toResponse(ecoleService.findById(id)));
            return "ecoles/modifier";
        }

        try {
            Ecole ecole = ecoleMapper.toEntity(request);
            ecoleService.modifier(id, ecole);
            redirectAttributes.addFlashAttribute("succes",
                    "École modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/ecoles/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ecoleService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes",
                    "École supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/ecoles";
    }
}