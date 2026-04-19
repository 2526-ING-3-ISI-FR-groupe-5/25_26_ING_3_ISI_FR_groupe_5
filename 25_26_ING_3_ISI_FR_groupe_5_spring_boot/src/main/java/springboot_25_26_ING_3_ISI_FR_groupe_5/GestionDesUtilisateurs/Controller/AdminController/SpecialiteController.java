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
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.specialite.SpecialiteRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.FiliereMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.SpecialiteMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.FiliereService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.SpecialiteService;

import java.util.List;

@Controller
@RequestMapping("/admin/specialites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SpecialiteController {

    private final SpecialiteService specialiteService;
    private final SpecialiteMapper specialiteMapper;
    private final FiliereService filiereService;
    private final FiliereMapper filiereMapper;

    @GetMapping
    public String liste(
            @RequestParam(required = false) Long filiereId,
            Model model
    ) {
        List<Specialite> specialites = filiereId != null
                ? specialiteService.getByFiliere(filiereId)
                : specialiteService.getAll();

        model.addAttribute("specialites",
                specialiteMapper.toResponseList(specialites));
        model.addAttribute("filieres",
                filiereMapper.toResponseList(filiereService.getAll()));
        model.addAttribute("filiereIdSelectionne", filiereId);
        model.addAttribute("form", new SpecialiteRequest());
        return "specialites/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") SpecialiteRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            model.addAttribute("specialites",
                    specialiteMapper.toResponseList(specialiteService.getAll()));
            model.addAttribute("filieres",
                    filiereMapper.toResponseList(filiereService.getAll()));
            return "specialites/liste";
        }

        try {
            Specialite specialite = new Specialite();
            specialite.setNom(request.getNom());
            specialite.setCode(request.getCode());
            specialite.setDescription(request.getDescription());

            Filiere filiere = filiereService.findById(request.getFiliereId());
            specialite.setFiliere(filiere);

            specialiteService.creer(specialite, acteur);
            redirectAttributes.addFlashAttribute("succes", "Spécialité créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/specialites";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        Specialite specialite = specialiteService.findById(id);
        SpecialiteRequest form = new SpecialiteRequest();
        form.setNom(specialite.getNom());
        form.setCode(specialite.getCode());
        form.setDescription(specialite.getDescription());
        form.setFiliereId(specialite.getFiliere().getId());

        model.addAttribute("specialite", specialiteMapper.toResponse(specialite));
        model.addAttribute("filieres",
                filiereMapper.toResponseList(filiereService.getAll()));
        model.addAttribute("form", form);
        return "specialites/modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") SpecialiteRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        if (result.hasErrors()) {
            model.addAttribute("specialite",
                    specialiteMapper.toResponse(specialiteService.findById(id)));
            model.addAttribute("filieres",
                    filiereMapper.toResponseList(filiereService.getAll()));
            return "specialites/modifier";
        }

        try {
            Specialite data = new Specialite();
            data.setNom(request.getNom());
            data.setCode(request.getCode());
            data.setDescription(request.getDescription());
            specialiteService.modifier(id, data, acteur);
            redirectAttributes.addFlashAttribute("succes", "Spécialité modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/specialites";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur acteur
    ) {
        try {
            specialiteService.supprimer(id, acteur);
            redirectAttributes.addFlashAttribute("succes", "Spécialité supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/specialites";
    }

    @GetMapping("/{id}/json")
    @ResponseBody
    public SpecialiteRequest getSpecialiteJson(@PathVariable Long id) {
        Specialite specialite = specialiteService.findById(id);
        SpecialiteRequest request = new SpecialiteRequest();
        request.setNom(specialite.getNom());
        request.setCode(specialite.getCode());
        request.setDescription(specialite.getDescription());
        request.setFiliereId(specialite.getFiliere().getId());
        return request;
    }
}