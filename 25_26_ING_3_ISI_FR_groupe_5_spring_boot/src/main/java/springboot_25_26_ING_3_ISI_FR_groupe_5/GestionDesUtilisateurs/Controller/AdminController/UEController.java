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
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ue.UERequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.SpecialiteMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.UEMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.SpecialiteService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.UEService;

import java.util.List;

@Controller
@RequestMapping("/ues")
@RequiredArgsConstructor
public class UEController {

    private final UEService ueService;
    private final UEMapper ueMapper;
    private final SpecialiteService specialiteService;
    private final SpecialiteMapper specialiteMapper;
    private final AnneeAcademiqueService anneeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT')")
    public String liste(
            @RequestParam(required = false) Long anneeId,
            @RequestParam(required = false) String recherche,
            Model model
    ) {
        Annee_academique annee = null;
        try {
            annee = anneeId != null
                    ? anneeService.findById(anneeId)
                    : anneeService.getAnneeActive();
        } catch (Exception e) {
            // Pas d'année active
        }

        List<UE> ues;
        if (recherche != null && !recherche.isEmpty()) {
            ues = ueService.rechercher(recherche);
        } else {
            // ✅ Récupérer TOUTES les UE
            ues = ueService.getAll();
        }

        // ✅ Debug
        System.out.println("Nombre d'UE trouvées : " + ues.size());

        model.addAttribute("ues", ueMapper.toResponseList(ues));
        model.addAttribute("specialites",
                specialiteMapper.toResponseList(specialiteService.getAll()));
        model.addAttribute("annees", anneeService.getAll());
        model.addAttribute("anneeActive", annee);
        model.addAttribute("recherche", recherche);
        model.addAttribute("form", new UERequest());
        return "ues/liste";
    }

    @PostMapping("/creer")
    @PreAuthorize("hasRole('ADMIN')")
    public String creer(
            @Valid @ModelAttribute("form") UERequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("ues",
                    ueMapper.toResponseList(
                            ueService.getByAnnee(anneeService.getAnneeActive().getId())));
            model.addAttribute("specialites",
                    specialiteMapper.toResponseList(specialiteService.getAll()));
            return "ues/liste";
        }

        try {
            UE ue = new UE();
            ue.setNom(request.getNom());
            ue.setCode(request.getCode());
            ue.setLibelle(request.getLibelle());
            ue.setLibelleAnglais(request.getLibelleAnglais()); // ⚠️ AJOUTER CETTE LIGNE
            ueService.creer(ue, request.getSpecialiteId());
            redirectAttributes.addFlashAttribute("succes",
                    "UE créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/ues";
    }

    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasRole('ADMIN')")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        UE ue = ueService.findById(id);
        UERequest form = new UERequest();
        form.setNom(ue.getNom());
        form.setCode(ue.getCode());
        form.setLibelle(ue.getLibelle());

        form.setSpecialiteId(ue.getSpecialite().getId());

        model.addAttribute("ue", ueMapper.toResponse(ue));
        model.addAttribute("specialites",
                specialiteMapper.toResponseList(specialiteService.getAll()));
        model.addAttribute("form", form);
        return "ues/modifier";
    }

    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasRole('ADMIN')")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") UERequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("ue", ueMapper.toResponse(ueService.findById(id)));
            model.addAttribute("specialites",
                    specialiteMapper.toResponseList(specialiteService.getAll()));
            return "ues/modifier";
        }

        try {
            UE data = new UE();
            data.setNom(request.getNom());
            data.setCode(request.getCode());
            data.setLibelle(request.getLibelle());
            data.setLibelleAnglais(request.getLibelleAnglais());
            ueService.modifier(id, data);
            redirectAttributes.addFlashAttribute("succes",
                    "UE modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/ues";
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('ADMIN')")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ueService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes",
                    "UE supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/ues";
    }
}