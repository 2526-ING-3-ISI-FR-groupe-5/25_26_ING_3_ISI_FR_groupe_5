package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Cycle.CycleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.CycleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EcoleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.CycleService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EcoleService;

@Controller
@RequestMapping("/admin/cycles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CycleController {

    private final CycleService cycleService;
    private final CycleMapper cycleMapper;
    private final EcoleService ecoleService;
    private final EcoleMapper ecoleMapper;

    @GetMapping
    public String liste(
            @RequestParam(required = false) Long ecoleId,
            Model model
    ) {
        java.util.List<Cycle> cycles;

        if (ecoleId != null) {
            cycles = cycleService.getByEcole(ecoleId);
            model.addAttribute("ecoleFiltre", ecoleService.findById(ecoleId));
        } else {
            cycles = cycleService.getAll();
        }

        model.addAttribute("cycles", cycleMapper.toResponseList(cycles));
        model.addAttribute("ecoles", ecoleMapper.toResponseList(ecoleService.getAll()));
        model.addAttribute("ecoleId", ecoleId);
        model.addAttribute("form", new CycleRequest());

        return "cycle/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") CycleRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            model.addAttribute("cycles", cycleMapper.toResponseList(cycleService.getAll()));
            model.addAttribute("ecoles", ecoleMapper.toResponseList(ecoleService.getAll()));
            return "cycle/liste";
        }

        try {
            Cycle cycle = cycleMapper.toEntity(request);
            cycleService.creer(cycle, acteur);
            redirectAttributes.addFlashAttribute("succes", "Cycle créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/cycles";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Cycle cycle = cycleService.findById(id);
        CycleRequest request = new CycleRequest();
        request.setTypeCycle(cycle.getTypeCycle());

        model.addAttribute("cycle", cycleMapper.toResponse(cycle));
        model.addAttribute("form", request);
        model.addAttribute("ecoles", ecoleMapper.toResponseList(ecoleService.getAll()));

        return "cycle/modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") CycleRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            model.addAttribute("cycle", cycleMapper.toResponse(cycleService.findById(id)));
            model.addAttribute("ecoles", ecoleMapper.toResponseList(ecoleService.getAll()));
            return "cycle/modifier";
        }

        try {
            Cycle data = new Cycle();
            data.setTypeCycle(request.getTypeCycle());
            cycleService.modifier(id, data, acteur);
            redirectAttributes.addFlashAttribute("succes", "Cycle modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/cycles";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            cycleService.supprimer(id, acteur);
            redirectAttributes.addFlashAttribute("succes", "Cycle supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/cycles";
    }
}