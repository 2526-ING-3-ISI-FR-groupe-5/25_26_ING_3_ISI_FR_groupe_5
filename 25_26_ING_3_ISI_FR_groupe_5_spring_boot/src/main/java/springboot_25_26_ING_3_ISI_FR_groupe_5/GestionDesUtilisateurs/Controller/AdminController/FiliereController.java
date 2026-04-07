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
@RequestMapping("/filieres")
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

        model.addAttribute("filieres",
                filiereMapper.toResponseList(filieres));
        model.addAttribute("ecoles",
                ecoleMapper.toResponseList(ecoleService.getAll()));
        // ✅ Correction : utiliser toResponseList avec libelle
        model.addAttribute("cycles",
                cycleMapper.toResponseList(cycleService.getAll()));
        model.addAttribute("ecoleIdSelectionne", ecoleId);
        model.addAttribute("cycleIdSelectionne", cycleId);
        model.addAttribute("form", new FiliereRequest());

        return "filieres/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("filiere",
                filiereMapper.toResponse(filiereService.findById(id)));
        return "filieres/detail";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") FiliereRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("filieres",
                    filiereMapper.toResponseList(filiereService.getAll()));
            model.addAttribute("ecoles",
                    ecoleMapper.toResponseList(ecoleService.getAll()));
            model.addAttribute("cycles",
                    cycleMapper.toResponseList(cycleService.getAll()));
            return "filieres/liste";
        }

        try {
            Filiere filiere = new Filiere();
            filiere.setNom(request.getNom());
            filiere.setCode(request.getCode());
            filiere.setDescription(request.getDescription());
            filiereService.creer(filiere, request.getEcoleId(), request.getCycleId());
            redirectAttributes.addFlashAttribute("succes",
                    "Filière créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/filieres";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        Filiere filiere = filiereService.findById(id);
        FiliereRequest form = new FiliereRequest();
        form.setNom(filiere.getNom());
        form.setCode(filiere.getCode());
        form.setDescription(filiere.getDescription());
        form.setEcoleId(filiere.getEcole().getId());
        form.setCycleId(filiere.getCycle().getId());

        model.addAttribute("filiere", filiereMapper.toResponse(filiere));
        model.addAttribute("ecoles",
                ecoleMapper.toResponseList(ecoleService.getAll()));
        model.addAttribute("cycles",
                cycleMapper.toResponseList(cycleService.getAll()));
        model.addAttribute("form", form);
        return "filieres/modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") FiliereRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("filiere",
                    filiereMapper.toResponse(filiereService.findById(id)));
            model.addAttribute("ecoles",
                    ecoleMapper.toResponseList(ecoleService.getAll()));
            model.addAttribute("cycles",
                    cycleMapper.toResponseList(cycleService.getAll()));
            return "filieres/modifier";
        }

        try {
            Filiere data = new Filiere();
            data.setNom(request.getNom());
            data.setCode(request.getCode());
            data.setDescription(request.getDescription());
            filiereService.modifier(id, data);
            redirectAttributes.addFlashAttribute("succes",
                    "Filière modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/filieres/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            filiereService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Filière supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/filieres";
    }
}
