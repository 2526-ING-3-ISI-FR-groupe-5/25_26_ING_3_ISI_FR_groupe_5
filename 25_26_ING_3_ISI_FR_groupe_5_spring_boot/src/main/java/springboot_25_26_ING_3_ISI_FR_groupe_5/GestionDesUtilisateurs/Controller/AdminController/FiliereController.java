package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.filiere.FiliereRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.CycleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EcoleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.FiliereMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.CycleService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EcoleService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.FiliereService;

import java.util.List;

@Controller
@RequestMapping("/admin/filieres")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FiliereController {

    private final FiliereService filiereService;
    private final FiliereMapper filiereMapper;
    private final EcoleService ecoleService;
    private final EcoleMapper ecoleMapper;
    private final CycleService cycleService;
    private final CycleMapper cycleMapper;

    @GetMapping
    public String liste(
            @RequestParam(required = false) Long ecoleId,
            @RequestParam(required = false) Long cycleId,
            Model model
    ) {
        List<Filiere> filieres;

        if (ecoleId != null && cycleId != null) {
            filieres = filiereService.getByEcoleAndCycle(ecoleId, cycleId);
        } else if (ecoleId != null) {
            filieres = filiereService.getByEcole(ecoleId);
        } else if (cycleId != null) {
            filieres = filiereService.getByCycle(cycleId);
        } else {
            filieres = filiereService.getAll();
        }

        model.addAttribute("filieres", filiereMapper.toResponseList(filieres));
        model.addAttribute("ecoles", ecoleMapper.toResponseList(ecoleService.getAll()));
        model.addAttribute("cycles", cycleMapper.toResponseList(cycleService.getAll()));
        model.addAttribute("ecoleIdSelectionne", ecoleId);
        model.addAttribute("cycleIdSelectionne", cycleId);
        model.addAttribute("createForm", new FiliereRequest());  // Pour la modale de création
        model.addAttribute("editForm", new FiliereRequest());    // Pour la modale d'édition

        return "filieres/liste";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("createForm") FiliereRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("filieres", filiereMapper.toResponseList(filiereService.getAll()));
            model.addAttribute("ecoles", ecoleMapper.toResponseList(ecoleService.getAll()));
            model.addAttribute("cycles", cycleMapper.toResponseList(cycleService.getAll()));
            model.addAttribute("createForm", request);
            model.addAttribute("editForm", new FiliereRequest());
            return "filieres/liste";
        }

        try {
            filiereService.creer(request);
            redirectAttributes.addFlashAttribute("succes", "Filière créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/filieres";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("editForm") FiliereRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            List<Filiere> filieres = filiereService.getAll();
            model.addAttribute("filieres", filiereMapper.toResponseList(filieres));
            model.addAttribute("ecoles", ecoleMapper.toResponseList(ecoleService.getAll()));
            model.addAttribute("cycles", cycleMapper.toResponseList(cycleService.getAll()));
            model.addAttribute("createForm", new FiliereRequest());
            model.addAttribute("editForm", request);
            model.addAttribute("editId", id);
            return "filieres/liste";
        }

        try {
            filiereService.modifier(id, request);
            redirectAttributes.addFlashAttribute("succes", "Filière modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/filieres";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            filiereService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Filière supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/filieres";
    }

    // Endpoint pour récupérer les données d'une filière en JSON (pour pré-remplir la modale)
    @GetMapping("/{id}/json")
    @ResponseBody
    public FiliereRequest getFiliereJson(@PathVariable Long id) {
        Filiere filiere = filiereService.findById(id);
        FiliereRequest request = new FiliereRequest();
        request.setNom(filiere.getNom());
        request.setCode(filiere.getCode());
        request.setDescription(filiere.getDescription());
        request.setEcoleId(filiere.getEcole().getId());
        request.setCycleId(filiere.getCycle().getId());
        return request;
    }
}