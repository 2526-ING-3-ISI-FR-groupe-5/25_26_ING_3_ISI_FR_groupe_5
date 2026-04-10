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
@RequestMapping("/admin/ues")
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
            @RequestParam(required = false) Long specialiteId,
            @RequestParam(required = false) String recherche,
            Model model
    ) {
        List<UE> ues;

        if (recherche != null && !recherche.isEmpty()) {
            ues = ueService.rechercher(recherche);
        } else if (specialiteId != null && specialiteId > 0) {
            ues = ueService.getBySpecialite(specialiteId);
        } else {
            ues = ueService.getAll();
        }

        model.addAttribute("ues", ueMapper.toResponseList(ues));
        model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
        model.addAttribute("specialiteIdSelectionne", specialiteId);
        model.addAttribute("recherche", recherche);
        model.addAttribute("form", new UERequest());

        return "ue/liste";
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
            model.addAttribute("ues", ueMapper.toResponseList(ueService.getAll()));
            model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
            return "ue/liste";
        }

        try {
            UE ue = ueMapper.toEntity(request);
            ueService.creer(ue, request.getSpecialiteId());
            redirectAttributes.addFlashAttribute("succes", "UE créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/ues";
    }

    // ❌ SUPPRIMER cette méthode (plus utilisée)
    // @GetMapping("/{id}/modifier")
    // public String formulaireModifier(...) { ... }

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
            redirectAttributes.addFlashAttribute("erreur", "Erreur de validation");
            return "redirect:/admin/ues";
        }

        try {
            UE data = ueMapper.toEntity(request);
            ueService.modifier(id, data);
            redirectAttributes.addFlashAttribute("succes", "UE modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/ues";
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('ADMIN')")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ueService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "UE supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/ues";
    }

    // ✅ Endpoint JSON pour la modale d'édition
    @GetMapping("/{id}/json")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT')")
    public UERequest getUeJson(@PathVariable Long id) {
        UE ue = ueService.findById(id);
        UERequest request = new UERequest();
        request.setNom(ue.getNom());
        request.setCode(ue.getCode());
        request.setLibelle(ue.getLibelle());
        request.setLibelleAnglais(ue.getLibelleAnglais());
        if (ue.getSpecialite() != null) {
            request.setSpecialiteId(ue.getSpecialite().getId());
        }
        return request;
    }
}