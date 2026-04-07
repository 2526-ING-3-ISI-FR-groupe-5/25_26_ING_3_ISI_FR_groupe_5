package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Cycle.CycleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.CycleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.CycleService;

@Controller
@RequestMapping("/cycles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CycleController {

    private final CycleService cycleService;
    private final CycleMapper cycleMapper;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("cycles", cycleMapper.toResponseList(cycleService.getAll()));
        model.addAttribute("form", new CycleRequest());
        return "cycles/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") CycleRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("cycles", cycleMapper.toResponseList(cycleService.getAll()));
            return "cycles/liste";
        }

        try {
            Cycle cycle = cycleMapper.toEntity(request);
            cycleService.creer(cycle);
            redirectAttributes.addFlashAttribute("succes", "Cycle créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/cycles";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @RequestParam TypeCycle typeCycle,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Cycle data = new Cycle();
            data.setTypeCycle(typeCycle);
            cycleService.modifier(id, data);
            redirectAttributes.addFlashAttribute("succes", "Cycle modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/cycles";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            cycleService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Cycle supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/cycles";
    }
}