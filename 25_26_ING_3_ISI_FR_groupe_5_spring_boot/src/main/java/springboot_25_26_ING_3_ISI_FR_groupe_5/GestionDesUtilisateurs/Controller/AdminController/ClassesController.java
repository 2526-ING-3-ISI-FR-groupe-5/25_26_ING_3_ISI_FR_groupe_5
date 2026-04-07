package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classe.ClassesRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService classesService;
    private final ClassesMapper classesMapper;
    private final SpecialiteService specialiteService;
    private final SpecialiteMapper specialiteMapper;
    private final NiveauService niveauService;
    private final NiveauMapper niveauMapper;
    private final AnneeAcademiqueService anneeService;
    private final InscriptionService inscriptionService;
    private final InscriptionMapper inscriptionMapper;
    private final ProgrammationUEService programmationService;
    private final ProgrammationUEMapper programmationMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT')")
    public String liste(
            @RequestParam(required = false) Long specialiteId,
            @RequestParam(required = false) Long niveauId,
            @RequestParam(required = false) Long anneeId,
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

        List<Classe> classesList = new ArrayList<>();

        if (niveauId != null && niveauId > 0) {
            // Filtrer par niveau
            classesList = classesService.getByNiveau(niveauId);
        } else if (specialiteId != null && specialiteId > 0) {
            // ✅ Filtrer par spécialité : récupérer les niveaux puis les classes
            List<Niveau> niveaux = niveauService.getBySpecialite(specialiteId);
            for (Niveau niveau : niveaux) {
                classesList.addAll(classesService.getByNiveau(niveau.getId()));
            }
        } else {
            // Toutes les classes
            classesList = classesService.getAll();
        }

        // Debug
        System.out.println("=== DEBUG CLASSES CONTROLLER ===");
        System.out.println("specialiteId reçu = " + specialiteId);
        System.out.println("niveauId reçu = " + niveauId);
        System.out.println("Nombre de classes trouvées : " + classesList.size());

        // Charger les niveaux pour le select
        List<Niveau> tousNiveaux = niveauService.getAll();

        model.addAttribute("classes", classesMapper.toResponseList(classesList));
        model.addAttribute("niveaux", niveauMapper.toResponseList(tousNiveaux));
        model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
        model.addAttribute("annees", anneeService.getAll());
        model.addAttribute("anneeActive", annee);
        model.addAttribute("specialiteIdSelectionne", specialiteId);
        model.addAttribute("niveauIdSelectionne", niveauId);
        model.addAttribute("form", new ClassesRequest());

        return "classes/liste";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT')")
    public String detail(
            @PathVariable Long id,
            @RequestParam(required = false) Long anneeId,
            Model model
    ) {
        Annee_academique annee = anneeId != null
                ? anneeService.findById(anneeId)
                : anneeService.getAnneeActive();

        Classe classe = classesService.findById(id);

        List<Inscription> inscriptions = inscriptionService
                .getByClasseAndAnnee(id, annee.getId());

        List<ProgrammationUE> programmations = programmationService
                .getByClasseAndAnnee(id, annee.getId());

        model.addAttribute("classe", classesMapper.toResponse(classe));
        model.addAttribute("anneeActive", annee);
        model.addAttribute("annees", anneeService.getAll());
        model.addAttribute("inscriptions", inscriptionMapper.toResponseList(inscriptions));
        model.addAttribute("programmations", programmationMapper.toResponseList(programmations));
        model.addAttribute("totalEtudiants", inscriptions.size());

        return "classes/detail";
    }

    @PostMapping("/creer")
    @PreAuthorize("hasRole('ADMIN')")
    public String creer(
            @Valid @ModelAttribute("form") ClassesRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
            model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
            return "classes/liste";
        }

        try {
            classesService.creer(request.getNom(), request.getNiveauId());
            redirectAttributes.addFlashAttribute("succes", "Classe créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/classes";
    }

    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasRole('ADMIN')")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        Classe classe = classesService.findById(id);
        ClassesRequest form = new ClassesRequest();
        form.setNom(classe.getNom());
        form.setNiveauId(classe.getNiveau().getId());

        model.addAttribute("classe", classesMapper.toResponse(classe));
        model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
        model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
        model.addAttribute("form", form);

        return "classes/modifier";
    }

    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasRole('ADMIN')")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") ClassesRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("classe", classesMapper.toResponse(classesService.findById(id)));
            model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
            model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
            return "classes/modifier";
        }

        try {
            classesService.modifier(id, request.getNom(), request.getNiveauId());
            redirectAttributes.addFlashAttribute("succes", "Classe modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/classes/" + id;
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('ADMIN')")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            classesService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Classe supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/classes";
    }
}