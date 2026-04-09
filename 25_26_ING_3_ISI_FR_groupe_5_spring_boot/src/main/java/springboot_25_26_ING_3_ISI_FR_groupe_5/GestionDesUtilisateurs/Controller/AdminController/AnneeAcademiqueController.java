package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.annee.AnneeRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre.SemestreRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.AnneeAcademiqueMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.SemestreMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.SemestreService;

@Controller
@RequestMapping("/admin/annees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnneeAcademiqueController {

    private final AnneeAcademiqueService anneeService;
    private final AnneeAcademiqueMapper anneeMapper;
    private final SemestreService semestreService;
    private final SemestreMapper semestreMapper;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("annees", anneeMapper.toResponseList(anneeService.getAll()));
        try {
            model.addAttribute("anneeActive", anneeService.getAnneeActive());
        } catch (Exception e) {
            model.addAttribute("anneeActive", null);
        }
        model.addAttribute("form", new AnneeRequest());
        return "annee/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") AnneeRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
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
            anneeService.creer(request.getNom(), request.getDateDebut(), request.getDateFin(), request.isActive());
            redirectAttributes.addFlashAttribute("succes", "Année académique créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/annees";
    }

    @PostMapping("/{id}/activer")
    public String activer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            anneeService.activer(id);
            redirectAttributes.addFlashAttribute("succes", "Année activée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/annees";
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
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            anneeService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Année supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/annees";
    }
}