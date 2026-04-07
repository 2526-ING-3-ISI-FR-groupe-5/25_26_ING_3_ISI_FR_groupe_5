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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.AssistantMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.ClassesMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AssistantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.ClassesService;

@Controller
@RequestMapping("/assistants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AssistantController {

    private final AssistantService assistantService;
    private final AssistantMapper assistantMapper;
    private final ClassesService classesService;
    private final ClassesMapper classesMapper;
    private final AnneeAcademiqueService anneeService;

    @GetMapping
    public String liste(
            @RequestParam(required = false) String recherche,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<AssistantPedagogique> assistants = assistantService.rechercher(
                recherche, PageRequest.of(page, size)
        );

        model.addAttribute("assistants",
                assistantMapper.toResponseList(assistants.getContent()));
        model.addAttribute("recherche", recherche);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", assistants.getTotalPages());
        model.addAttribute("form", new AssistantRequest());
        return "assistants/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireCreer(Model model) {
        model.addAttribute("form", new AssistantRequest());
        model.addAttribute("classes",
                classesMapper.toResponseList(
                        classesService.getByAnnee(
                                anneeService.getAnneeActive().getId(),
                                null, PageRequest.of(0, 100)
                        ).getContent()));
        return "assistants/form";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") AssistantRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("classes",
                    classesMapper.toResponseList(
                            classesService.getByAnnee(
                                    anneeService.getAnneeActive().getId(),
                                    null, PageRequest.of(0, 100)
                            ).getContent()));
            return "assistants/form";
        }

        try {
            AssistantPedagogique assistant = assistantMapper.toEntity(request);
            assistantService.creer(assistant, request.getClasseIds());
            redirectAttributes.addFlashAttribute("succes",
                    "Assistant créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/assistants";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        AssistantPedagogique assistant = assistantService.findById(id);
        AssistantRequest form = new AssistantRequest();
        form.setNom(assistant.getNom());
        form.setPrenom(assistant.getPrenom());
        form.setEmail(assistant.getEmail());
        form.setTelephone(assistant.getTelephone());
        form.setFonction(assistant.getFonction());

        model.addAttribute("assistant", assistantMapper.toResponse(assistant));
        model.addAttribute("classes",
                classesMapper.toResponseList(
                        classesService.getByAnnee(
                                anneeService.getAnneeActive().getId(),
                                null, PageRequest.of(0, 100)
                        ).getContent()));
        model.addAttribute("form", form);
        return "assistants/modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") AssistantRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("assistant",
                    assistantMapper.toResponse(assistantService.findById(id)));
            model.addAttribute("classes",
                    classesMapper.toResponseList(
                            classesService.getByAnnee(
                                    anneeService.getAnneeActive().getId(),
                                    null, PageRequest.of(0, 100)
                            ).getContent()));
            return "assistants/modifier";
        }

        try {
            AssistantPedagogique data = assistantMapper.toEntity(request);
            assistantService.modifier(id, data);
            if (request.getClasseIds() != null && !request.getClasseIds().isEmpty()) {
                assistantService.affecterClasses(id, request.getClasseIds());
            }
            redirectAttributes.addFlashAttribute("succes",
                    "Assistant modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/assistants";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            assistantService.toggleActif(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Statut modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/assistants";
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            assistantService.reinitialiserMotDePasse(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Mot de passe réinitialisé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/assistants";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            assistantService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Assistant supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/assistants";
    }
}