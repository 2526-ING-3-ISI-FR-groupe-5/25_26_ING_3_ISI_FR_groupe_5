package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeEnseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EnseignantMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.ProgrammationUEMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EnseignantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.ProgrammationUEService;

@Controller
@RequestMapping("/enseignants")
@RequiredArgsConstructor
public class EnseignantController {

    private final EnseignantService enseignantService;
    private final EnseignantMapper enseignantMapper;
    private final AnneeAcademiqueService anneeService;
    private final ProgrammationUEService programmationService;
    private final ProgrammationUEMapper programmationMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String liste(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) Long anneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Annee_academique annee = anneeId != null
                ? anneeService.findById(anneeId)
                : anneeService.getAnneeActive();

        Page<Enseignant> enseignants = enseignantService.rechercher(
                annee.getId(), recherche, PageRequest.of(page, size)
        );

        model.addAttribute("enseignants",
                enseignantMapper.toResponseList(enseignants.getContent()));
        model.addAttribute("anneeActive", annee);
        model.addAttribute("annees", anneeService.getAll());
        model.addAttribute("recherche", recherche);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", enseignants.getTotalPages());
        model.addAttribute("form", new EnseignantRequest());
        return "enseignants/liste";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String detail(
            @PathVariable Long id,
            @RequestParam(required = false) Long anneeId,
            Model model
    ) {
        Annee_academique annee = anneeId != null
                ? anneeService.findById(anneeId)
                : anneeService.getAnneeActive();

        model.addAttribute("enseignant",
                enseignantMapper.toResponse(enseignantService.findById(id)));
        model.addAttribute("programmations",
                programmationMapper.toResponseList(
                        programmationService.getByEnseignantAndAnnee(id, annee.getId())));
        model.addAttribute("anneeActive", annee);
        model.addAttribute("annees", anneeService.getAll());
        return "enseignants/detail";
    }

    @GetMapping("/nouveau")
    @PreAuthorize("hasRole('ADMIN')")
    public String formulaireCreer(Model model) {
        model.addAttribute("form", new EnseignantRequest());
        return "enseignants/form";
    }

    @PostMapping("/creer")
    @PreAuthorize("hasRole('ADMIN')")
    public String creer(
            @Valid @ModelAttribute("form") EnseignantRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "enseignants/form";
        }

        try {
            Enseignant enseignant = enseignantMapper.toEntity(request);
            enseignantService.creer(enseignant);
            redirectAttributes.addFlashAttribute("succes",
                    "Enseignant créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/enseignants";
    }

    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasRole('ADMIN')")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        Enseignant enseignant = enseignantService.findById(id);
        EnseignantRequest form = new EnseignantRequest();
        form.setNom(enseignant.getNom());
        form.setPrenom(enseignant.getPrenom());
        form.setEmail(enseignant.getEmail());
        form.setTelephone(enseignant.getTelephone());
        form.setGrade(enseignant.getGrade());
        form.setTypeEnseignant(TypeEnseignant.valueOf(enseignant.getTypeEnseignant()));

        model.addAttribute("enseignant",
                enseignantMapper.toResponse(enseignant));
        model.addAttribute("form", form);
        return "enseignants/modifier";
    }

    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasRole('ADMIN')")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") EnseignantRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("enseignant",
                    enseignantMapper.toResponse(enseignantService.findById(id)));
            return "enseignants/modifier";
        }

        try {
            Enseignant data = enseignantMapper.toEntity(request);
            enseignantService.modifier(id, data);
            redirectAttributes.addFlashAttribute("succes",
                    "Enseignant modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/enseignants/" + id;
    }

    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggle(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            enseignantService.toggleActif(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Statut modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignants/" + id;
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetPassword(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            enseignantService.reinitialiserMotDePasse(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Mot de passe réinitialisé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignants/" + id;
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('ADMIN')")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            enseignantService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Enseignant supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignants";
    }
}
