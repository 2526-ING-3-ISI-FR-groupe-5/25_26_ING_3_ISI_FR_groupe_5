package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.niveau.NiveauRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.FiliereMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.NiveauMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.FiliereService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.NiveauService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.SpecialiteService;

import java.util.List;

@Controller
@RequestMapping("/admin/niveaux")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NiveauController {

    private final NiveauService niveauService;
    private final NiveauMapper niveauMapper;
    private final FiliereService filiereService;
    private final FiliereMapper filiereMapper;
    private final SpecialiteService specialiteService;

    @GetMapping
    public String liste(
            @RequestParam(required = false) Long filiereId,
            Model model
    ) {
        List<Niveau> niveaux = filiereId != null
                ? niveauService.getByFiliere(filiereId)
                : niveauService.getAll();

        model.addAttribute("niveaux", niveauMapper.toResponseList(niveaux));
        model.addAttribute("filieres", filiereMapper.toResponseList(filiereService.getAll()));
        model.addAttribute("specialites", specialiteService.getAll());
        model.addAttribute("filiereIdSelectionne", filiereId);
        model.addAttribute("form", new NiveauRequest());

        return "niveau/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") NiveauRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
            model.addAttribute("filieres", filiereMapper.toResponseList(filiereService.getAll()));
            model.addAttribute("specialites", specialiteService.getAll());
            return "niveau/liste";
        }

        try {
            Niveau niveau = niveauMapper.toEntity(request);
            niveauService.creer(niveau, request.getFiliereId(), request.getSpecialiteId());
            redirectAttributes.addFlashAttribute("succes", "Niveau créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/niveaux";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        Niveau niveau = niveauService.findById(id);
        NiveauRequest form = new NiveauRequest();
        form.setNom(niveau.getNom());
        form.setCode(niveau.getCode());
        form.setOrdre(niveau.getOrdre());
        form.setFiliereId(niveau.getFiliere().getId());
        if (niveau.getSpecialite() != null) {
            form.setSpecialiteId(niveau.getSpecialite().getId());
        }

        model.addAttribute("niveau", niveauMapper.toResponse(niveau));
        model.addAttribute("filieres", filiereMapper.toResponseList(filiereService.getAll()));
        model.addAttribute("specialites", specialiteService.getAll());
        model.addAttribute("form", form);
        return "niveau/modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") NiveauRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("niveau", niveauMapper.toResponse(niveauService.findById(id)));
            model.addAttribute("filieres", filiereMapper.toResponseList(filiereService.getAll()));
            model.addAttribute("specialites", specialiteService.getAll());
            return "niveau/modifier";
        }

        try {
            Niveau data = niveauMapper.toEntity(request);
            niveauService.modifier(id, data, request.getSpecialiteId());
            redirectAttributes.addFlashAttribute("succes", "Niveau modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/niveaux";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            niveauService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Niveau supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/niveaux";
    }

    // Endpoint JSON pour la modale d'édition
    @GetMapping("/{id}/json")
    @ResponseBody
    public NiveauRequest getNiveauJson(@PathVariable Long id) {
        Niveau niveau = niveauService.findById(id);
        NiveauRequest request = new NiveauRequest();
        request.setNom(niveau.getNom());
        request.setCode(niveau.getCode());
        request.setOrdre(niveau.getOrdre());
        request.setFiliereId(niveau.getFiliere().getId());
        if (niveau.getSpecialite() != null) {
            request.setSpecialiteId(niveau.getSpecialite().getId());
        }
        return request;
    }
}